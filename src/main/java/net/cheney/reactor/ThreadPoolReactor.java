package net.cheney.reactor;

import java.io.IOException;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.log4j.Logger;

public final class ThreadPoolReactor extends Reactor {
	private static final Logger LOG = Logger.getLogger(ThreadPoolReactor.class);

	private final ExecutorService executor;
	private boolean running = false;
	
	private final Lock queuelock, selectorlock;

	private final Queue<Runnable> pendingInvocations = new ConcurrentLinkedQueue<Runnable>();

	protected ThreadPoolReactor(final ExecutorService executor) throws IOException {
		super();
		this.executor = executor;
		
		ReadWriteLock l = new ReentrantReadWriteLock();
		this.queuelock = l.readLock();
		this.selectorlock = l.writeLock();
	}

	public static ThreadPoolReactor open(ExecutorService executor) throws IOException {
		return new ThreadPoolReactor(executor);
	}

	public static Reactor open() throws IOException {
		return open(Executors.newCachedThreadPool());
	}

	@Override
	protected <T extends SelectableChannel> void register(final T channel, final int ops, final AsyncChannel<T> asyncChannel) throws ClosedChannelException {
		if(queuelock.tryLock()) {
			try {
				registerNow(channel, ops, asyncChannel);
			} finally {
				queuelock.unlock();
			}
		} else {
			registerLater(channel, ops, asyncChannel);
		}
	}
	
	@Override
	AsyncServerChannel newAsyncServerChannel(ServerProtocolFactory factory) throws IOException {
		return new ThreadPoolAsyncServerChannel(this, factory, executor);
	}
	
	private final <T extends SelectableChannel> void registerLater(final T channel, final int ops, final AsyncChannel<T> asyncChannel) {
		invokeLater(new Runnable() {
			public void run() {
				try {
					registerNow(channel, ops, asyncChannel);
				} catch (ClosedChannelException e) {
					LOG.error(String.format("Unable to register channel %s, with ops %d", channel, ops));
				}
			}
		});
	}

	private final void invokeLater(final Runnable r) {
		pendingInvocations.add(r);
		wakeup();
	}
	
	@Override
	protected final void enableInterest(final SelectableChannel sc, final int op) {
		if (queuelock.tryLock()) {
			try {
				enableInterestNow(sc, op);
			} finally {
				queuelock.unlock();
			}
		} else {
			enableInterestLater(sc, op);
		}
	}

	private final void enableInterestLater(final SelectableChannel sc, final int op) {
		invokeLater(new Runnable() {
			public void run() {
				enableInterestNow(sc, op); 
			};
		});
	}
	 
	@Override
	final void disableInterest(final SelectableChannel sc, final int op) {
		if (queuelock.tryLock()) {
			try {
				disableInterestNow(sc, op);
			} finally {
				queuelock.unlock();
			}
		} else {
			disableInterestLater(sc, op);
		}
	}

	private final void disableInterestLater(final SelectableChannel sc, final int op) {
		invokeLater(new Runnable() {
			public void run() {
				disableInterestNow(sc, op);
			};
		});
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
		try {
			// The selector lock has to be held during the whole of the doSelect() method 
			// as the event handlers may try to change the interest set of a key which
			// is in the pending queue to be registered, yet because the selectorlock
			// is not held they are able to execute ahead of the registration
			selectorlock.lock();
			super.doSelect();
		} finally {
			selectorlock.unlock();
		}
	}
	
	private final void doPendingInvocations() {
		for(Runnable r = pendingInvocations.poll(); r != null ; r = pendingInvocations.poll()) {
			try {
				r.run();
			} catch (CancelledKeyException ignored) {
				//
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