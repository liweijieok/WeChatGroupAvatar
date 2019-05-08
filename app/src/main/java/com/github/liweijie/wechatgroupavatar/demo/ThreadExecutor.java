package com.github.liweijie.wechatgroupavatar.demo;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Project Name: lalaclient_mobile
 *
 * @author vj
 * @date : 2018-09-14 09:46
 * email:liweijieok@qq.com
 * desc: hll的线程池
 * lastModify:
 */
public class ThreadExecutor {

    private static final class HllExecutorHolder {
        private static final ThreadExecutor INSTANCE = new ThreadExecutor();
    }

    public static ThreadExecutor getInstance() {
        return HllExecutorHolder.INSTANCE;
    }

    private ThreadExecutor() {
        init();
    }

    private ThreadPoolExecutor execute;

    private void init() {
        if (execute == null) {
            BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(256);
            RejectedExecutionHandler policy = new ThreadPoolExecutor.DiscardOldestPolicy();
            ThreadFactory factory = new ThreadFactory() {
                private final AtomicInteger mCount = new AtomicInteger(1);

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "ThreadExecutor-" + mCount.getAndIncrement());
                }
            };
            int processors = Runtime.getRuntime().availableProcessors() + 1;
            execute = new ThreadPoolExecutor(processors, processors, 60 * 1000, TimeUnit.SECONDS, queue, factory, policy);
        }
    }

    public void execute(Runnable task) {
        execute.execute(task);
    }

    public Future<?> submit(Runnable task) {
        return execute.submit(task);
    }

    public <T> Future<T> submit(Callable<T> task) {
        return execute.submit(task);
    }

    public void shutdown() {
        execute.shutdown();
    }

    public List<Runnable> shutdownNow() {
        return execute.shutdownNow();
    }

    public void remove(Runnable task) {
        execute.getQueue().remove(task);
    }

}
