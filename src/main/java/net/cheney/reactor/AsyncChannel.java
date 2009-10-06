package net.cheney.reactor;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.SelectableChannel;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

public abstract class AsyncChannel<T extends SelectableChannel> implements Closeable {
	
	private final T sc;
	private final Reactor reactor;

	AsyncChannel(@Nonnull Reactor reactor, @Nonnull T sc, @Nonnegative int ops) throws IOException {
		this.sc = sc;
		this.reactor = reactor;
		sc.configureBlocking(false);
		reactor().register(sc, ops, this);
	}
	
	public final void close() throws IOException {
		sc.close();
	}
	
	protected final T channel() {
		return this.sc;
	}
	
	protected final Reactor reactor() {
		return this.reactor;
	}
	
	@Override
	public final String toString() {
		return super.toString()+"["+channel()+"]";
	}
}
