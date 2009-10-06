package net.cheney.reactor;

import java.nio.channels.SelectionKey;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnegative;

enum InterestOp {

	ACCEPT(SelectionKey.OP_ACCEPT),
	CONNECT(SelectionKey.OP_CONNECT),
	READ(SelectionKey.OP_READ),
	WRITE(SelectionKey.OP_WRITE);
	
	private final int op;

	private InterestOp(int op) {
		this.op = op;
	}
	
	static Set<InterestOp> parse(@Nonnegative int o) {
		Set<InterestOp> ops = new HashSet<InterestOp>();
		for(InterestOp value : values()) {
			if ((o & value.op) == o) {
				ops.add(value);
			}
		}
		return ops;
	}
}
