package net.cheney.reactor;

public abstract class ServerProtocolFactory implements CompletionHandler<AsyncSocketChannel>{

	public final void completed(final AsyncSocketChannel channel) {
		doAccept(channel);
	}

	protected abstract void doAccept(final AsyncSocketChannel channel);

}
