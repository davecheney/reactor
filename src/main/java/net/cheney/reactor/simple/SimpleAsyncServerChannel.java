package net.cheney.reactor.simple;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import javax.annotation.Nonnull;

import net.cheney.reactor.AsyncServerChannel;
import net.cheney.reactor.AsyncSocketChannel;
import net.cheney.reactor.Reactor;
import net.cheney.reactor.ServerProtocolFactory;

public class SimpleAsyncServerChannel extends AsyncServerChannel {
	
	SimpleAsyncServerChannel(@Nonnull Reactor reactor, @Nonnull ServerProtocolFactory factory) throws IOException {
		super(reactor, factory);
	}
	
	@Override
	protected AsyncSocketChannel createAsyncSocketChannel(@Nonnull SocketChannel sc) throws IOException {
		return new SimpleAsyncSocketChannel(reactor(), sc);
	}
	
	

}
