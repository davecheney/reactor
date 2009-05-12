package net.cheney.reactor;

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
	 *
	 */
	public abstract void onConnect();

	/**
	 * Called by the transport when the underlying transport is disconnected
	 *
	 */
	public abstract void onDisconnect();
}
