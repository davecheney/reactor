package net.cheney.reactor;

import java.io.IOException;
import java.net.SocketAddress;
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
		final SelectionKey sk = channel.channel().keyFor(selector);
		if(sk != null) {
			enableInterest(sk, ops);
		} else {
			LOG.warn(String.format("Unable to enable ops %d, %s is not connected to %s", ops, channel.channel(), selector));
		}
	}
	
	protected abstract void enableInterest(final SelectionKey sk, int ops);
	
	final void disableInterest(final AsyncChannel<?> channel, final int ops) {
		final SelectionKey sk = channel.channel().keyFor(selector);
		if(sk != null) {
			disableInterest(sk, ops);
		} else {
			LOG.warn(String.format("Unable to disable ops %d, %s is not connected to %s", ops, channel.channel(), selector));
		}
	}

	abstract void disableInterest(final SelectionKey sk, int ops);
	
	protected abstract AsyncSocketChannel newAsyncSocketChannel(final ClientProtocolFactory factory) throws IOException;
	
	abstract <T extends SelectableChannel> void register(final T channel, final int ops, final AsyncChannel<?> asyncChannel) throws IOException;
	
	public abstract AsyncServerChannel listenTCP(final SocketAddress addr, final ServerProtocolFactory factory) throws IOException;
	
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
			
		case SelectionKey.OP_CONNECT | SelectionKey.OP_READ:
			((AsyncSocketChannel) key.attachment()).onConnect();
			((AsyncByteChannel<?>) key.attachment()).doRead();
			break;
			
		case SelectionKey.OP_CONNECT | SelectionKey.OP_READ | SelectionKey.OP_WRITE:
			((AsyncByteChannel<?>) key.attachment()).doRead();
			if (key.isValid()) {
				((AsyncByteChannel<?>) key.attachment()).doWrite();
			}
			break;
			
		default:
			LOG.error(String.format("Channel: %s interestOps: %d Unhandled readyOps: %d",key.channel(), key.interestOps(), key.readyOps()));
		}		
	}

}
