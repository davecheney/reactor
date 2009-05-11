package net.cheney.reactor;

public interface CompletionHandler<T> {

	void completed(final T result);
	
}