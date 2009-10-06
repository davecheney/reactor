package net.cheney.reactor;

import javax.annotation.Nonnull;

public abstract class ClientProtocolFactory implements CompletionHandler<AsyncSocketChannel> {

	public final void completed(@Nonnull AsyncSocketChannel channel) {
		onConnect(channel);
	}

	protected abstract void onConnect(final AsyncSocketChannel channel);

}