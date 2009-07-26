package net.cheney.reactor.threadpool;

import java.io.IOException;

import net.cheney.reactor.AbstractReactorTestCase;
import net.cheney.reactor.Reactor;

public class ThreadPoolReactorTest extends AbstractReactorTestCase {

	@Override
	protected Reactor openReactor() throws IOException {
		return ThreadPoolReactor.open();
	}

}
