package net.cheney.reactor;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.SelectableChannel;

public abstract class AsyncChannel<T extends SelectableChannel> implements Closeable {
	
	private final T channel;
	private final Reactor reactor;

	protected AsyncChannel(final Reactor reactor, final T channel, final int ops) throws IOException {
		this.channel = channel;
		this.reactor = reactor;
		channel.configureBlocking(false);
		reactor().register(channel, ops, this);
	}
	
	public final void close() throws IOException {
		channel.close();
	}
	
	protected final T channel() {
		return this.channel;
	}
	
	final Reactor reactor() {
		return this.reactor;
	}
	

}
