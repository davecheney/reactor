package net.cheney.reactor;

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

import org.apache.log4j.Logger;

import static java.util.Collections.emptySet;

public abstract class Reactor {
	private static final Logger LOG = Logger.getLogger(Reactor.class);
	
	private final Selector selector;

	Reactor() throws IOException {
		selector = SelectorProvider.provider().openSelector();
	}
	
	final Selector selector() {
		return selector;
	}
	
	final void enableInterest(final AsyncChannel<?> channel, final int ops) {
		enableInterest(channel.channel(), ops);
	}
	
	abstract void enableInterest(final SelectableChannel sc, int ops);
	
	final void disableInterest(final AsyncChannel<?> channel, final int ops) {
		disableInterest(channel.channel(), ops);
	}

	abstract void disableInterest(final SelectableChannel sc, int ops);
	
	abstract AsyncSocketChannel newAsyncSocketChannel(final ClientProtocolFactory factory) throws IOException;
	
	abstract <T extends SelectableChannel> void register(final T channel, final int ops, final AsyncChannel<T> asyncChannel) throws IOException;
	
	public final AsyncServerChannel listenTCP(final SocketAddress addr, final ServerProtocolFactory factory) throws IOException {
		final AsyncServerChannel channel = newAsyncServerChannel(factory);
		return channel.listen(addr);
	}
	
	abstract AsyncServerChannel newAsyncServerChannel(ServerProtocolFactory factory) throws IOException;

	Set<SelectionKey> selectNow() throws IOException {
		if (selector.select() > 0) {
			return selector.selectedKeys();
		} else {
			return emptySet();
		}
	}

	public final void connect(final SocketAddress addr, final ClientProtocolFactory factory) throws IOException {
		final AsyncSocketChannel channel = newAsyncSocketChannel(factory);
		channel.connect(addr);
	}
	
	final void wakeup() {
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

	private final void handleSelectionKey(final SelectionKey key) throws IOException {
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
			LOG.error(String.format("Channel: %s [%s] interestOps: %d Unhandled readyOps: %d",key.channel(), key.attachment(), key.interestOps(), key.readyOps()));
		}		
	}

	final <T extends SelectableChannel> void registerNow(final T channel, final int ops, final AsyncChannel<T> asyncChannel) throws ClosedChannelException {
		channel.register(selector(), ops, asyncChannel);
		LOG.info(String.format("Channel [%s] now registered on [%s]", channel, selector()));
	}

	final void enableInterestNow(final SelectableChannel sc, int ops) {
		final SelectionKey sk = sc.keyFor(selector());
		assert sk != null : "channel ["+sc+"] is not registered with selector["+selector()+"]";
		try {
			sk.interestOps(sk.interestOps() | ops);
		} catch (CancelledKeyException e) {
			LOG.error(String.format("Unable to set ops %d on key %s, channel %s", ops, sk, sc));
		}
	}

	final void disableInterestNow(final SelectableChannel sc, int ops) {
		final SelectionKey sk = sc.keyFor(selector());
		assert sk != null : "channel ["+sc+"] is not registered with selector["+selector()+"]";
		try {
			sk.interestOps(sk.interestOps() & ~ops);
		} catch (CancelledKeyException e) {
			LOG.error(String.format("Unable to set ops %d on key %s, channel %s", ops, sk, sc));
		}
	}
	
	public abstract void start();
	
	public abstract void stop();
	
}
