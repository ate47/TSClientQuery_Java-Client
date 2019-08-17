package fr.harmonia.tsclientquery.event.executor;

/**
 * an executor to execute runnable
 * 
 * @author ATE47
 * @see AsynchronousEventExecutor
 * @see SynchronousEventExecutor
 */
public interface RunnablesExecutor {
	/**
	 * a a runnable to execute
	 */
	void add(Runnable runnable);

	/**
	 * stop this executor
	 */
	void stop();

	/**
	 * stop this executor
	 */
	void start();
}
