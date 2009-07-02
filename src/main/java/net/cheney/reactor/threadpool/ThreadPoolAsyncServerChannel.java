package net.cheney.reactor.threadpool;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;

import net.cheney.reactor.AsyncServerChannel;
import net.cheney.reactor.AsyncSocketChannel;
import net.cheney.reactor.Reactor;
import net.cheney.reactor.ServerProtocolFactory;

public final class ThreadPoolAsyncServerChannel extends AsyncServerChannel {
	private final ExecutorService executor;

	protected ThreadPoolAsyncServerChannel(final Reactor reactor, final ServerProtocolFactory factory, final ExecutorService executor) throws IOException {
		super(reactor, factory);
		this.executor = executor;
	}
	
	@Override
	protected final AsyncSocketChannel createAsyncSocketChannel(final SocketChannel sc) throws IOException {
		return new ThreadPoolAsyncSocketChannel(reactor(), sc, executor);
	}

}