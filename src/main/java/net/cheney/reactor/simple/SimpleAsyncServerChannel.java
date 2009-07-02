package net.cheney.reactor.simple;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import net.cheney.reactor.AsyncServerChannel;
import net.cheney.reactor.AsyncSocketChannel;
import net.cheney.reactor.Reactor;
import net.cheney.reactor.ServerProtocolFactory;

public class SimpleAsyncServerChannel extends AsyncServerChannel {
	
	SimpleAsyncServerChannel(final Reactor reactor, final ServerProtocolFactory factory) throws IOException {
		super(reactor, factory);
	}
	
	@Override
	protected AsyncSocketChannel createAsyncSocketChannel(SocketChannel sc) throws IOException {
		return new SimpleAsyncSocketChannel(reactor(), sc);
	}
	
	

}
