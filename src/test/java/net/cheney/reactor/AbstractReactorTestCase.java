package net.cheney.reactor;

import java.io.IOException;
import java.util.concurrent.locks.Lock;

import org.junit.Test;

public abstract class AbstractReactorTestCase {

	@Test public void testCreateAndClose() throws IOException {
		Reactor reactor = openReactor();
		reactor.close();
	}
	
	protected abstract Reactor openReactor() throws IOException;
}
