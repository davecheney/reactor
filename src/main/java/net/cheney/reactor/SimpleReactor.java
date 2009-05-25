package net.cheney.reactor;

import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class SimpleReactor extends Reactor {

	private boolean running = false;

	private SimpleReactor() throws IOException {
		super();
	}

	public static SimpleReactor open() throws IOException {
		return new SimpleReactor();
	}

	@Override
	final void disableInterest(final SelectableChannel sc, final int ops) {
		disableInterestNow(sc, ops);
	}

	@Override
	final void enableInterest(final SelectableChannel sc, final int ops) {
		enableInterestNow(sc, ops);
	}

	@Override
	final AsyncSocketChannel newAsyncSocketChannel(
			final ClientProtocolFactory factory) throws IOException {
		return new SimpleAsyncSocketChannel(this, factory);
	}

	@Override
	final AsyncServerChannel newAsyncServerChannel(
			final ServerProtocolFactory factory) throws IOException {
		return new SimpleAsyncServerChannel(this, factory);
	}

	@Override
	final <T extends SelectableChannel> void register(final T channel, final int ops, final AsyncChannel<T> asyncChannel) throws IOException {
		registerNow(channel, ops, asyncChannel);
	}

	@Override
	public void start() {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.execute(new Runnable() {
			public void run() {
				running = true;
				while (running) {
					try {
						doSelect();
					} catch (IOException e) {
						e.printStackTrace();
						running = false;
					}
				}
			}
		});
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

}
