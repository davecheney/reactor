package net.cheney.reactor;

import java.io.IOException;
import java.nio.channels.SelectableChannel;

public interface EventHandler<T extends SelectableChannel> {

	boolean handleEvent(final T channel) throws IOException;	
}
