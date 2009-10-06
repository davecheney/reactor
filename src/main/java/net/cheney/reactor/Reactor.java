package net.cheney.reactor;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.SelectorProvider;
import java.util.Collection;
import java.util.Set;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import org.apache.log4j.Logger;

import static java.util.Collections.emptySet;

public abstract class Reactor implements Closeable {
	private static final Logger LOG = Logger.getLogger(Reactor.class);
	
	private final Selector selector;

	protected Reactor() throws IOException {
		selector = SelectorProvider.provider().openSelector();
	}
	
	final Selector selector() {
		return selector;
	}
	
	final void enableInterest(@Nonnull AsyncChannel<?> channel, @Nonnegative int ops) {
		enableInterest(channel.channel(), ops);
	}
	
	protected abstract void enableInterest(SelectableChannel sc, int ops);
	
	final void disableInterest(@Nonnull AsyncChannel<?> channel, @Nonnegative int ops) {
		disableInterest(channel.channel(), ops);
	}

	protected abstract void disableInterest(SelectableChannel sc, int ops);
	
	protected abstract AsyncSocketChannel newAsyncSocketChannel(ClientProtocolFactory factory) throws IOException;
	
	protected abstract <T extends SelectableChannel> void register(T channel, int ops, AsyncChannel<T> asyncChannel) throws IOException;
	
	public final AsyncServerChannel listenTCP(@Nonnull SocketAddress addr, @Nonnull ServerProtocolFactory factory) throws IOException {
		final AsyncServerChannel channel = newAsyncServerChannel(factory);
		return channel.listen(addr);
	}
	
	protected abstract AsyncServerChannel newAsyncServerChannel(ServerProtocolFactory factory) throws IOException;

	Set<SelectionKey> selectNow() throws IOException {
		if (selector.select() > 0) {
			return selector.selectedKeys();
		} else {
			return emptySet();
		}
	}

	public final void connect(@Nonnull SocketAddress addr, @Nonnull ClientProtocolFactory factory) throws IOException {
		final AsyncSocketChannel channel = newAsyncSocketChannel(factory);
		channel.connect(addr);
	}
	
	protected final void wakeup() {
		selector.wakeup();
	}

	protected void doSelect() throws IOException {
		final Collection<SelectionKey> keys = selectNow();
		for (final SelectionKey key : keys) {
			try {
				handleSelectionKey(key);
			} catch (IOException e) {
				LOG.error("IOException in " + key.attachment(), e);
				key.cancel(); key.channel().close();
			} catch (Exception e) {
				/**
				 * Catch any unhandled exception at this point and close the
				 * channel Errors and Throwables are not handled because they
				 * represent something really broken with the JVM rather than
				 * the individual connection
				 */
				LOG.error("Unhandled Exception in " + key.attachment(), e);
				key.channel().close();
				key.cancel();
			}
		}
		keys.clear();
	}

	private final void handleSelectionKey(@Nonnull SelectionKey key) throws IOException {
		switch (key.readyOps()) {
		case SelectionKey.OP_READ:
			((AsyncByteChannel<?>) key.attachment()).doRead();
			break;

		case SelectionKey.OP_WRITE:
			((AsyncByteChannel<?>) key.attachment()).doWrite();
			break;

		case SelectionKey.OP_READ | SelectionKey.OP_WRITE:
			((AsyncByteChannel<?>) key.attachment()).doRead();
			if (key.isValid()) {
				((AsyncByteChannel<?>) key.attachment()).doWrite();
			}
			break;

		case SelectionKey.OP_ACCEPT:
			((AsyncServerChannel) key.attachment()).onAccept();
			break;
			
		case SelectionKey.OP_CONNECT:
			((AsyncSocketChannel) key.attachment()).onConnect();
			break;

		default:
			LOG.error(String.format("Channel: %s [%s] interestOps: %s Unhandled readyOps: %s",key.channel(), key.attachment(), InterestOp.parse(key.interestOps()), InterestOp.parse(key.readyOps())));
		}		
	}

	protected final <T extends SelectableChannel> void registerNow(@Nonnull T channel, @Nonnegative int ops, @Nonnull AsyncChannel<T> asyncChannel) throws ClosedChannelException {
		channel.register(selector(), ops, asyncChannel);
		LOG.info(String.format("Channel [%s] now registered on [%s]", channel, selector()));
	}

	protected final void enableInterestNow(@Nonnull SelectableChannel sc, @Nonnegative int ops) {
		if (sc.isOpen()) {
			final SelectionKey sk = sc.keyFor(selector());
			assert sk != null : "channel ["+sc+"] is not registered with selector["+selector()+"]";
			try {
				sk.interestOps(sk.interestOps() | ops);
			} catch (CancelledKeyException e) {
				LOG.error(String.format("Unable to enable ops %s on key %s, channel %s", InterestOp.parse(ops), sk, sc));
			}
		} else {
			LOG.warn(String.format("Unable to enable ops %s, channel %s", InterestOp.parse(ops), sc));
		}
	}

	protected final void disableInterestNow(@Nonnull SelectableChannel sc, @Nonnegative int ops) {
		if (sc.isOpen()) {
			final SelectionKey sk = sc.keyFor(selector());
			assert sk != null : "channel ["+sc+"] is not registered with selector["+selector()+"]";
			try {
				sk.interestOps(sk.interestOps() & ~ops);
			} catch (CancelledKeyException e) {
				LOG.error(String.format("Unable to disable ops %s on key %s, channel %s", InterestOp.parse(ops), sk, sc));
			}
		} else {
			LOG.warn(String.format("Unable to disable ops %s, channel %s", InterestOp.parse(ops), sc));
		}
	}
	
	public abstract void start();
	
	public abstract void stop();
	
	@Override
	public void close() throws IOException {
		this.selector.close();
	}
	
}
