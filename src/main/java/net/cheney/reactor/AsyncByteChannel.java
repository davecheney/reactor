package net.cheney.reactor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;

import javax.annotation.Nonnull;

public abstract class AsyncByteChannel<T extends SelectableChannel & ByteChannel> extends AsyncChannel<T> {

	private static final CompletionHandler<ByteBuffer[]> NULL_WRITE_HANDLER = new CompletionHandler<ByteBuffer[]>() {
		public void completed(final ByteBuffer[] result) {
			// nothing
		};
	};
	
	AsyncByteChannel(@Nonnull Reactor reactor, @Nonnull T channel, @Nonnull int ops) throws IOException {
		super(reactor, channel, ops);
	}

	public abstract void read(ByteBuffer buff, CompletionHandler<ByteBuffer> handler);
	
	public abstract void read(EventHandler<T> eventHandler);
	
	public abstract void write(ByteBuffer buff, CompletionHandler<ByteBuffer> handler); 
	
	public abstract void write(ByteBuffer[] buffs, CompletionHandler<ByteBuffer[]> handler);
	
	public abstract void write(EventHandler<T> eventHandler);
	
	public final void write(@Nonnull ByteBuffer... buffers) {
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
	
}
