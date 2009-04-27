package net.cheney.reactor;

import net.cheney.reactor.AsyncChannel.CompletionHandler;

public abstract class ServerProtocolFactory implements CompletionHandler<AsyncSocketChannel>{

	public final void completed(final AsyncSocketChannel channel) {
		doAccept(channel);
	}

	protected abstract void doAccept(final AsyncSocketChannel channel);

}
