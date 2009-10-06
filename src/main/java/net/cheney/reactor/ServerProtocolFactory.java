package net.cheney.reactor;

import javax.annotation.Nonnull;

public abstract class ServerProtocolFactory implements CompletionHandler<AsyncSocketChannel>{

	public final void completed(@Nonnull AsyncSocketChannel channel) {
		doAccept(channel);
	}

	protected abstract void doAccept(AsyncSocketChannel channel);

}
