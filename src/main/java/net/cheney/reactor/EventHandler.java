package net.cheney.reactor;

import java.io.IOException;

public interface EventHandler<T> {

	boolean handleEvent(final T channel) throws IOException;	
}
