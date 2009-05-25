package net.cheney.reactor;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public class SimpleAsyncServerChannel extends AsyncServerChannel {
	
	SimpleAsyncServerChannel(final Reactor reactor, final ServerProtocolFactory factory) throws IOException {
		super(reactor, factory);
	}
	
	@Override
	AsyncSocketChannel createAsyncSocketChannel(SocketChannel sc) throws IOException {
		return new SimpleAsyncSocketChannel(reactor(), sc);
	}
	
	

}
