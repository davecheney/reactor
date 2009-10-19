package net.cheney.reactor;

import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

public abstract class AsyncCharChannel<T extends SelectableChannel> extends AsyncChannel<T> {

	private static final CompletionHandler<CharBuffer[]> NULL_WRITE_HANDLER = new CompletionHandler<CharBuffer[]>() {
		public void completed(final CharBuffer[] result) {
			// nothing
		};
	};

	AsyncCharChannel(@Nonnull Reactor reactor, @Nonnull T sc, @Nonnegative int ops) throws IOException {
		super(reactor, sc, ops);
	}
	
	public abstract void read(final CharBuffer buff, final CompletionHandler<CharBuffer> handler);
	
	public abstract void read(EventHandler<T> eventHandler);
	
	public abstract void write(final CharBuffer buff, final CompletionHandler<CharBuffer> handler); 
	
	public abstract void write(final CharBuffer[] buffs, final CompletionHandler<CharBuffer[]> handler);
	
	public abstract void write(EventHandler<T> eventHandler);
	
	public final void write(@Nonnull CharBuffer... buffers) {
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
