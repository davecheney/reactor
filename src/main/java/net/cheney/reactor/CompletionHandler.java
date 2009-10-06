package net.cheney.reactor;

public interface CompletionHandler<T> {

	void completed(T result);
	
}