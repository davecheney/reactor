package net.cheney.reactor;

import java.io.IOException;

public abstract class Protocol<T extends AsyncByteChannel<?>> {

	private final T channel;
	
	protected Protocol(final T channel) {
		this.channel = channel;
	}
	
	protected final T channel() {
		return channel;
	}
	
	/**
	 * Called by the transport once the underlying transport is connected. 
	 * @throws IOException 
	 *
	 */
	public abstract void onConnect() throws IOException;

	/**
	 * Called by the transport when the underlying transport is disconnected
	 *
	 */
	public abstract void onDisconnect();
}
