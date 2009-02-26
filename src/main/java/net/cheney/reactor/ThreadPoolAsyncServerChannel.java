package net.cheney.reactor;

import static java.lang.String.format;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;

public final class ThreadPoolAsyncServerChannel extends AsyncServerChannel {
	private static final Logger LOG = Logger.getLogger(ThreadPoolAsyncServerChannel.class);
	
	private final ExecutorService executor;

	ThreadPoolAsyncServerChannel(final Reactor reactor, final ServerProtocolFactory factory, final ExecutorService executor) throws IOException {
		super(reactor, factory);
		this.executor = executor;
	}
	
	@Override
	protected final AsyncSocketChannel createAsyncSocketChannel(final SocketChannel sc) throws IOException {
		return new ThreadPoolAsyncSocketChannel(reactor(), sc, executor);
	}

	@Override
	protected final AsyncServerChannel listen(final SocketAddress addr) throws IOException {
		final ThreadPoolAsyncServerChannel c = (ThreadPoolAsyncServerChannel) super.listen(addr);
		LOG.info(format("%s listening on %s", c.getClass().getSimpleName(), c.channel().socket()));
		return c;
	}
}