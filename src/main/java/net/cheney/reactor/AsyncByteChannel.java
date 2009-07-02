package net.cheney.reactor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;

public abstract class AsyncByteChannel<T extends SelectableChannel & ByteChannel> extends AsyncChannel<T> {

	private static final CompletionHandler<ByteBuffer[]> NULL_WRITE_HANDLER = new CompletionHandler<ByteBuffer[]>() {
		public void completed(final ByteBuffer[] result) {
			// nothing
		};
	};
	
	AsyncByteChannel(final Reactor reactor, final T channel, final int ops) throws IOException {
		super(reactor, channel, ops);
	}

	public abstract void read(final ByteBuffer buff, final CompletionHandler<ByteBuffer> handler);
	
	public abstract void read(EventHandler<T> eventHandler);
	
	public abstract void write(final ByteBuffer buff, final CompletionHandler<ByteBuffer> handler); 
	
	public abstract void write(final ByteBuffer[] buffs, final CompletionHandler<ByteBuffer[]> handler);
	
	public abstract void write(EventHandler<T> eventHandler);
	
	public final void write(final ByteBuffer... buffers) {
		write(buffers, NULL_WRITE_HANDLER);
	}
	
	public abstract void doWrite() throws IOException;

	public abstract void doRead() throws IOException;
	
	protected final void disableReadInterest() {
		reactor().disableInterest(this, SelectionKey.OP_READ);
	}

	protected final void enableReadInterest() {
		reactor().enableInterest(this, SelectionKey.OP_READ);
	}
	
	protected final void disableWriteInterest() {
		reactor().disableInterest(this, SelectionKey.OP_WRITE);
	}
	
	protected final void enableWriteInterest() {
		reactor().enableInterest(this, SelectionKey.OP_WRITE);
	}
	
	@Override
	public final String toString() {
		return super.toString()+"["+channel()+"]";
	}
}
