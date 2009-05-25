package net.cheney.reactor;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;

public final class ThreadPoolAsyncServerChannel extends AsyncServerChannel {
	private final ExecutorService executor;

	ThreadPoolAsyncServerChannel(final Reactor reactor, final ServerProtocolFactory factory, final ExecutorService executor) throws IOException {
		super(reactor, factory);
		this.executor = executor;
	}
	
	@Override
	final AsyncSocketChannel createAsyncSocketChannel(final SocketChannel sc) throws IOException {
		return new ThreadPoolAsyncSocketChannel(reactor(), sc, executor);
	}

}