package net.glowstone.executor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

/**
 * The executor that can run the ChunkRunnables. ChunkRunnables that are closed to the player are prioritized, since
 * they are the most relevant to the player.
 */
public final class PriorityExecutor<PriorityRunnable extends net.glowstone.executor.PriorityRunnable> {

    private final ThreadPoolExecutor executor;
    private final SortableBlockingQueue<PriorityRunnable> queue;

    /**
     * Create a PriorityExecutor that can run ChunkRunnables. The PriorityExecutor uses a thread pool executor
     * internally.
     *
     * @param poolSize The number of threads in the pool
     * @throws IllegalArgumentException if poolSize < 0
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public PriorityExecutor(int poolSize) throws IllegalArgumentException {
        queue = new SortableBlockingQueue<>(PriorityRunnable::compareTo);
        BlockingQueue<Runnable> castedQueue = (BlockingQueue<Runnable>) ((BlockingQueue) queue);
        executor = new ThreadPoolExecutor(poolSize, poolSize, 0L, TimeUnit.MILLISECONDS, castedQueue);
        executor.prestartAllCoreThreads();
    }

    /**
     * Create a PriorityExecutor that can run ChunkRunnables. The PriorityExecutor uses a thread pool executor
     * internally.
     */
    public PriorityExecutor() {
        this(Runtime.getRuntime().availableProcessors());
    }

    /**
     * Execute the given runnables and remove the runnables that were previously enqueued and match the given predicate.
     *
     * @param toExecute The runnables to be executed.
     * @param predicate The predicate used to determine which runnables should be removed.
     * @return the removed runnables.
     */
    public Collection<PriorityRunnable> executeAndCancel(
            Collection<PriorityRunnable> toExecute,
            Predicate<PriorityRunnable> predicate
    ) {
        Collection<PriorityRunnable> removed = new ArrayList<>();

        queue.transaction(queue -> {
            queue.forEach(PriorityRunnable::updatePriority);
            queue.removeIf(runnable -> {
                if (predicate.test(runnable)) {
                    removed.add(runnable);
                    return true;
                }

                return false;
            });
            queue.addAll(toExecute);
            queue.sort();
        });

        return removed;
    }

    /**
     * Initiates an orderly shutdown in which previously submitted tasks are executed, but no new tasks will be
     * accepted. Invocation has no additional effect if already shut down. This method does not wait for previously
     * submitted tasks to complete execution.
     */
    public void shutdown() {
        executor.shutdown();
    }
}
