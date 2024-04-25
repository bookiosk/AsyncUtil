package io.github.bookiosk.executor;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author bookiosk
 */
public class AsyncThreadPool extends ThreadPoolExecutor {

    private volatile static ThreadPoolExecutor asyncThreadPool;

    public AsyncThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    public static ThreadPoolExecutor getInstance() {
        if (asyncThreadPool == null) {
            synchronized (AsyncThreadPool.class) {
                if (asyncThreadPool == null) {
                    int availableProcessors = Runtime.getRuntime().availableProcessors();
                    asyncThreadPool = new AsyncThreadPool(
                            availableProcessors,
                            availableProcessors * 2,
                            60,
                            TimeUnit.SECONDS,
                            new LinkedBlockingQueue<>(),
                            new AsyncThreadFactory(),
                            new AsyncPolicy());
                }
            }
        }
        return asyncThreadPool;
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
    }

    @Override
    protected void terminated() {
        super.terminated();
    }

    private static class AsyncThreadFactory implements ThreadFactory {

        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        AsyncThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = "async-util-" +
                    poolNumber.getAndIncrement() +
                    "-thread-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }

    private static class AsyncPolicy implements RejectedExecutionHandler {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {

        }
    }
}
