package net.cheney.reactor;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.SelectableChannel;

public abstract class AsyncChannel<T extends SelectableChannel> implements Closeable {
	
	private final T sc;
	private final Reactor reactor;

	AsyncChannel(final Reactor reactor, final T sc, final int ops) throws IOException {
		this.sc = sc;
		this.reactor = reactor;
		sc.configureBlocking(false);
		reactor().register(sc, ops, this);
	}
	
	public final void close() throws IOException {
		sc.keyFor(reactor().selector()).cancel();
		sc.close();
	}
	
	protected final T channel() {
		return this.sc;
	}
	
	final Reactor reactor() {
		return this.reactor;
	}
	

}
