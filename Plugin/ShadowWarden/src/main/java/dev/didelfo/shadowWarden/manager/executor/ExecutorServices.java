package dev.didelfo.shadowWarden.manager.executor;

import dev.didelfo.shadowWarden.ShadowWarden;

import java.util.concurrent.*;

public class ExecutorServices {

    private final ThreadPoolExecutor executor;

    public ExecutorServices(ShadowWarden pl) {
        executor = new ThreadPoolExecutor(
                pl.getConfig().getInt("threadspool.min"),
                pl.getConfig().getInt("threadspool.max"),
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    public void execute(Runnable task) {
        executor.submit(task);
    }

    public void shutdown() {
        executor.shutdown();
    }

    public ThreadPoolExecutor getExecutor() {
        return executor;
    }
}
