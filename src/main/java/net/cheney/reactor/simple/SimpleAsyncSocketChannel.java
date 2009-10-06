package net.cheney.reactor.simple;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import javax.annotation.Nonnull;

import net.cheney.reactor.AsyncSocketChannel;
import net.cheney.reactor.ClientProtocolFactory;
import net.cheney.reactor.CompletionHandler;
import net.cheney.reactor.EventHandler;
import net.cheney.reactor.Reactor;

public final class SimpleAsyncSocketChannel extends AsyncSocketChannel {

	protected SimpleAsyncSocketChannel(@Nonnull Reactor reactor, @Nonnull ClientProtocolFactory factory) throws IOException {
		super(reactor, SocketChannel.open(), factory, 0);
	}
	
	protected SimpleAsyncSocketChannel(@Nonnull Reactor reactor, @Nonnull SocketChannel sc) throws IOException {
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
