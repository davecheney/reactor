package net.cheney.reactor;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.SelectableChannel;

public abstract class AsyncChannel<T extends SelectableChannel> implements Closeable {
	
	public interface CompletionHandler<T> {

		void completed(T result);
		
	}

	private final T channel;
	private final Reactor reactor;

	protected AsyncChannel(final Reactor reactor, final T channel) throws IOException {
		this.channel = channel;
		this.reactor = reactor;
		channel.configureBlocking(false);
		reactor().register(channel, 0, this);
	}
	
	public final void close() throws IOException {
		channel.close();
	}
	
	protected final T channel() {
		return this.channel;
	}
	
	protected final Reactor reactor() {
		return this.reactor;
	}
	

}
