package io.github.bookiosk.executor;

import io.github.bookiosk.callback.DefaultGroupCallback;
import io.github.bookiosk.callback.IGroupCallback;
import io.github.bookiosk.wrapper.WorkerWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 类入口，可以根据自己情况调整core线程的数量
 *
 * @author bookiosk
 */
public class Async {

    private static final Logger logger = LoggerFactory.getLogger(Async.class);

    /**
     * 默认不定长线程池
     */
    private static final ThreadPoolExecutor COMMON_POOL = AsyncThreadPool.getInstance();
    /**
     * 注意，这里是个static，也就是只能有一个线程池。用户自定义线程池时，也只能定义一个
     */
    private static ExecutorService executorService;

    /**
     * 同步阻塞,直到所有都完成,或失败
     * @param timeout 超时时间
     * @param workerWrapper 要执行的组合体
     */
    public static boolean beginWork(long timeout, WorkerWrapper... workerWrapper) throws ExecutionException, InterruptedException {
        executorService = COMMON_POOL;
        return beginWork(timeout, executorService, workerWrapper);
    }

    /**
     * 如果想自定义线程池，请传pool。不自定义的话，就走默认的COMMON_POOL
     * @param timeout 超时时间
     * @param executorService 线程池
     * @param workerWrapper 要执行的组合体
     */
    public static boolean beginWork(long timeout, ExecutorService executorService, WorkerWrapper... workerWrapper) throws ExecutionException, InterruptedException {
        List<WorkerWrapper> workerWrappers = Arrays.stream(workerWrapper).collect(Collectors.toList());
        return beginWork(timeout, executorService, workerWrappers);
    }

    /**
     * 真正执行入口
     * @param timeout 超时时间
     * @param executorService 线程池
     * @param workerWrappers 要执行的组合体集合
     */
    public static boolean beginWork(long timeout, ExecutorService executorService, List<WorkerWrapper> workerWrappers) throws ExecutionException, InterruptedException {
        if (timeout < 0 || workerWrappers == null || workerWrappers.isEmpty() || workerWrappers.stream().anyMatch(Objects::isNull)) {
            return false;
        }
        // 保存线程池变量
        Async.executorService = executorService;
        // 定义一个map，存放所有的wrapper，key为wrapper的唯一id，value是该wrapper，可以从value中获取wrapper的result
        Map<String, WorkerWrapper> forParamUseWrappers = new ConcurrentHashMap<>();
        CompletableFuture[] futures = new CompletableFuture[workerWrappers.size()];
        for (int i = 0; i < workerWrappers.size(); i++) {
            WorkerWrapper wrapper = workerWrappers.get(i);
            futures[i] = CompletableFuture.runAsync(() -> wrapper.work(executorService, timeout, forParamUseWrappers), executorService);
        }
        try {
            // 如果指定时间内没有执行完抛出超时异常
            CompletableFuture.allOf(futures).get(timeout, TimeUnit.MILLISECONDS);
            return true;
        } catch (TimeoutException e) {
            Set<WorkerWrapper> set = new HashSet<>();
            // 计算所有的执行单元
            totalWorkers(workerWrappers, set);
            // 初始化和正在执行的执行单元立即停止不运行
            for (WorkerWrapper wrapper : set) {
                wrapper.stopNow();
            }
            return false;
        }
    }

    public static void beginWorkAsync(long timeout, IGroupCallback groupCallback, WorkerWrapper... workerWrapper) {
        executorService = COMMON_POOL;
        beginWorkAsync(timeout, executorService, groupCallback, workerWrapper);
    }

    /**
     * 异步执行,直到所有都完成,或失败后，发起回调
     */
    public static void beginWorkAsync(long timeout, ExecutorService executorService, IGroupCallback groupCallback, WorkerWrapper... workerWrapper) {
        if (groupCallback == null) {
            groupCallback = new DefaultGroupCallback();
        }
        IGroupCallback finalGroupCallback = groupCallback;
        executorService.submit(() -> {
            try {
                boolean success = beginWork(timeout, executorService, workerWrapper);
                if (success) {
                    finalGroupCallback.success(Arrays.asList(workerWrapper));
                } else {
                    finalGroupCallback.failure(Arrays.asList(workerWrapper), new TimeoutException());
                }
            } catch (ExecutionException | InterruptedException e) {
                logger.error("beginWorkAsync failed, error message : ", e);;
                finalGroupCallback.failure(Arrays.asList(workerWrapper), e);
            }
        });
    }

    /**
     * 总共多少个执行单元
     */
    @SuppressWarnings("unchecked")
    private static void totalWorkers(List<WorkerWrapper> workerWrappers, Set<WorkerWrapper> set) {
        set.addAll(workerWrappers);
        for (WorkerWrapper wrapper : workerWrappers) {
            if (wrapper.getNextWrappers() == null) {
                continue;
            }
            List<WorkerWrapper> wrappers = wrapper.getNextWrappers();
            totalWorkers(wrappers, set);
        }
    }

    /**
     * 关闭线程池
     */
    public static void shutDown() {
        executorService.shutdown();
    }

    public static String getThreadCount() {
        return "activeCount=" + COMMON_POOL.getActiveCount() +
                "  completedCount " + COMMON_POOL.getCompletedTaskCount() +
                "  largestCount " + COMMON_POOL.getLargestPoolSize();
    }
}
