package com.autoupdater.server.external;

/**
 * Interface for Cache source objects.
 * 
 * Allows obtain from source output for passed input.
 *
 * @param <I> type of input data
 * @param <O> type of output data
 */
public interface CacheSource <I extends Comparable<I>, O> {
	/**
	 * Returns output data for passed input data.
	 * 
	 * @param input input data
	 * @return output data
	 */
	public O getElement (I input);
}
