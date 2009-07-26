package net.cheney.reactor.simple;

import java.io.IOException;

import org.junit.Test;


public class SimpleReactorTest {

	@Test public void testCreateAndClose() throws IOException {
		SimpleReactor reactor = SimpleReactor.open();
		reactor.close();
	}
}

