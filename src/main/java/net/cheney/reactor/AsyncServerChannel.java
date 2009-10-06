package net.cheney.reactor;

import static java.lang.String.format;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;

import javax.annotation.Nonnull;

import org.apache.log4j.Logger;

public abstract class AsyncServerChannel extends AsyncChannel<ServerSocketChannel> {
	private static final Logger LOG = Logger.getLogger(AsyncServerChannel.class);

	private final ServerProtocolFactory factory;
	
	private static final int DEFAULT_RECEIVE_BUFFER_SIZE = 65535;

	protected AsyncServerChannel(@Nonnull Reactor reactor, @Nonnull ServerProtocolFactory factory) throws IOException {
		super(reactor, createServerSocketChannel(), 0);
		this.factory = factory;
	}
	
	protected final AsyncServerChannel listen(@Nonnull SocketAddress addr) throws IOException {
		channel().socket().bind(addr);
		enableAcceptInterest(); 
		LOG.info(format("%s listening on %s", this, channel().socket()));
		return this;
	}
	
	static final ServerSocketChannel createServerSocketChannel() throws IOException {
		final ServerSocketChannel ssc = SelectorProvider.provider().openServerSocketChannel();
		final ServerSocket socket = ssc.socket();
		socket.setReuseAddress(true);
		socket.setReceiveBufferSize(DEFAULT_RECEIVE_BUFFER_SIZE);
		return ssc;
	}
	
	protected final ServerProtocolFactory factory() {
		return this.factory;
	}

	public final void onAccept() throws IOException {
		final SocketChannel sc = channel().accept();
		if(sc != null) {
			setSocketParameters(sc.socket());
			factory().completed(createAsyncSocketChannel(sc));
		}		
	}
	
	private final void setSocketParameters(@Nonnull Socket socket) throws SocketException {
		socket.setSendBufferSize(65535);
		socket.setTcpNoDelay(true);
	}

	protected abstract AsyncSocketChannel createAsyncSocketChannel(SocketChannel sc) throws IOException;

	public final void enableAcceptInterest() {
		reactor().enableInterest(this, SelectionKey.OP_ACCEPT);
	}
	
	public final void disableAcceptInterest() {
		reactor().disableInterest(this, SelectionKey.OP_ACCEPT);
	}

}

