package net.cheney.reactor;

import java.io.IOException;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

public final class ThreadPoolReactor extends Reactor {
	private static final Logger LOG = Logger.getLogger(ThreadPoolReactor.class);

	private final ExecutorService executor;
	private boolean running = false;
	
	private final BlockingQueue<Runnable> pendingOperations = new ArrayBlockingQueue<Runnable>(1024);

	protected ThreadPoolReactor(final ExecutorService executor) throws IOException {
		super();
		this.executor = executor;
	}

	public static ThreadPoolReactor open(ExecutorService executor) throws IOException {
		return new ThreadPoolReactor(executor);
	}

	public static Reactor open() throws IOException {
		return open(Executors.newCachedThreadPool());
	}

	@Override
	protected <T extends SelectableChannel> void register(final T channel, final int ops, final AsyncChannel<T> asyncChannel) throws ClosedChannelException {
		registerLater(channel, ops, asyncChannel);
	}
	
	@Override
	AsyncServerChannel newAsyncServerChannel(ServerProtocolFactory factory) throws IOException {
		return new ThreadPoolAsyncServerChannel(this, factory, executor);
	}
	
	private final <T extends SelectableChannel> void registerLater(final T channel, final int ops, final AsyncChannel<T> asyncChannel) {
		class Register implements Runnable {
			public void run() {
				try {
					registerNow(channel, ops, asyncChannel);
				} catch (ClosedChannelException e) {
					LOG.error(String.format("Unable to register channel %s, with ops %d", channel, ops));
				}
			}
		}
		invokeLater(new Register());
	}

	private final void invokeLater(final Runnable r) {
		pendingOperations.add(r);
		wakeup();
	}
	
	@Override
	protected final void enableInterest(final SelectableChannel sc, final int op) {
		enableInterestLater(sc, op);
	}

	private final void enableInterestLater(final SelectableChannel sc, final int op) {
		class EnableInterest implements Runnable {
			public void run() {
				enableInterestNow(sc, op); 
			};
		}
		invokeLater(new EnableInterest());
	}
	 
	@Override
	final void disableInterest(final SelectableChannel sc, final int op) {
		disableInterestLater(sc, op);
	}

	private final void disableInterestLater(final SelectableChannel sc, final int op) {
		class DisableInterest implements Runnable {
			public void run() {
				disableInterestNow(sc, op);
			};
		}
		invokeLater(new DisableInterest());
	}

	@Override
	protected final AsyncSocketChannel newAsyncSocketChannel(final ClientProtocolFactory factory) throws IOException {
		return new ThreadPoolAsyncSocketChannel(this, factory, executor);
	}

	public final void start() {
		running = true;
		executor.execute(new Runner());
	}
	
	public final void stop() {
		running = false;
	}
	
	@Override
	public final void doSelect() throws IOException {
		doPendingInvocations();
		super.doSelect();
	}
	
	private final void doPendingInvocations() {
		final Collection<Runnable> tasks = new ArrayList<Runnable>();
		pendingOperations.drainTo(tasks);
//		LOG.info("processing: "+tasks.size());
		for(Runnable r : tasks) {
			try {
//				LOG.info(r.toString());
				r.run();
			} catch (CancelledKeyException ignored) {
				LOG.warn("Unable to execute "+r.toString());
			}
		}
	}
	
	private class Runner implements Runnable {

		public void run() {
			try {
				if(running) {
					doSelect();
					executor.execute(this);
				}
			} catch (IOException e) {
				e.printStackTrace();
//				running = false;
			}
		}
		
	}

}