package net.cheney.reactor;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public abstract class AsyncSocketChannel extends AsyncByteChannel<SocketChannel> {
	
	private final ClientProtocolFactory factory;

	protected AsyncSocketChannel(final Reactor reactor, final SocketChannel channel, final ClientProtocolFactory factory) throws IOException {
		super(reactor, channel);
		this.factory = factory;
	}
	
	public final void enableConnectInterest() {
		reactor().enableInterest(this, SelectionKey.OP_CONNECT);
	}
	
	public final void disableConnectInterest() {
		reactor().disableInterest(this, SelectionKey.OP_CONNECT);
	}

	public final SocketAddress peer() {
		return socket().getRemoteSocketAddress();
	}
	
	public final Socket socket() {
		return channel().socket();
	}

	public final void onConnect() throws IOException {
		channel().finishConnect();
		this.factory.completed(this);
	}

	public final void connect(final SocketAddress addr) throws IOException {
		channel().connect(addr);
		enableConnectInterest();
	}
}
