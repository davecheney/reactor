package net.cheney.reactor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;

public abstract class AsyncServerChannel extends AsyncChannel<ServerSocketChannel> {

	private final ServerProtocolFactory factory;

	protected AsyncServerChannel(final Reactor reactor, final ServerProtocolFactory factory) throws IOException {
		super(reactor, createServerSocketChannel(), SelectionKey.OP_ACCEPT);
		this.factory = factory;
	}
	
	protected AsyncServerChannel listen(final SocketAddress addr) throws IOException {
		channel().socket().bind(addr);
//		enableAcceptInterest(); 
		return this;
	}
	
	final static ServerSocketChannel createServerSocketChannel() throws IOException {
		final ServerSocketChannel ssc = SelectorProvider.provider().openServerSocketChannel();
		final ServerSocket socket = ssc.socket();
		socket.setReuseAddress(true);
		socket.setReceiveBufferSize(65535);
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
	
	private final void setSocketParameters(final Socket socket) throws SocketException {
		socket.setSendBufferSize(65535);
		socket.setTcpNoDelay(true);
	}

	abstract AsyncSocketChannel createAsyncSocketChannel(SocketChannel sc) throws IOException;

	public final void enableAcceptInterest() {
		reactor().enableInterest(this, SelectionKey.OP_ACCEPT);
	}
	
	public final void disableAcceptInterest() {
		reactor().disableInterest(this, SelectionKey.OP_ACCEPT);
	}

}

