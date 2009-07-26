package net.cheney.reactor.threadpool;

import java.io.IOException;

import org.junit.Test;

public class ThreadPoolReactorTest {

	@Test public void testCreateAndClose() throws IOException {
		ThreadPoolReactor reactor = ThreadPoolReactor.open();
		reactor.close();
	}
}
