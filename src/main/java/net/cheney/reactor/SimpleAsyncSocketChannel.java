package net.cheney.reactor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public final class SimpleAsyncSocketChannel extends AsyncSocketChannel {

	SimpleAsyncSocketChannel(final Reactor reactor, final ClientProtocolFactory factory) throws IOException {
		super(reactor, SocketChannel.open(), factory, 0);
	}
	
	SimpleAsyncSocketChannel(final Reactor reactor, final SocketChannel sc) throws IOException {
		super(reactor, sc, null, 0);
	}

	@Override
	public void doRead() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void doWrite() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void read(ByteBuffer buff, CompletionHandler<ByteBuffer> handler) {
		// TODO Auto-generated method stub

	}

	@Override
	public void read(EventHandler<SocketChannel> eventHandler) {
		// TODO Auto-generated method stub

	}

	@Override
	public void write(ByteBuffer buff, CompletionHandler<ByteBuffer> handler) {
		// TODO Auto-generated method stub

	}

	@Override
	public void write(ByteBuffer[] buffs,
			CompletionHandler<ByteBuffer[]> handler) {
		// TODO Auto-generated method stub

	}

	@Override
	public void write(EventHandler<SocketChannel> eventHandler) {
		// TODO Auto-generated method stub

	}

}
