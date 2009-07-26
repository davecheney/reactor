package net.cheney.reactor.simple;

import java.io.IOException;

import net.cheney.reactor.AbstractReactorTestCase;
import net.cheney.reactor.Reactor;

public class SimpleReactorTest extends AbstractReactorTestCase {

	@Override
	protected Reactor openReactor() throws IOException {
		return SimpleReactor.open();
	}

	
}

