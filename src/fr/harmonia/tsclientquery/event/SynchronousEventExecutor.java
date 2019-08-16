package fr.harmonia.tsclientquery.event;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * synchronous implementation of {@link RunnablesExecutor}, the
 * {@link SynchronousEventExecutor#callRunnables()} method must be call when you
 * want to execute the runnables
 * 
 * @author ATE47
 *
 */
public class SynchronousEventExecutor implements RunnablesExecutor {
	private final BlockingQueue<Runnable> runnables = new LinkedBlockingQueue<Runnable>();

	@Override
	public void add(Runnable runnable) {
		runnables.add(runnable);
	}

	@Override
	public void stop() {
		runnables.clear();
	}

	@Override
	public void start() {
	}

	/**
	 * call the runnables
	 */
	public void callRunnables() {
		Runnable r;
		while ((r = runnables.poll()) != null)
			r.run();
	}

}
