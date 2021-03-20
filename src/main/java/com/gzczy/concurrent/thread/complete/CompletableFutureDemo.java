package com.gzczy.concurrent.thread.complete;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Description CompletableFuture演示案例
 * @Author chenzhengyu
 * @Date 2021-03-20 15:52
 */
@Slf4j(topic = "c.CompletableFutureDemo")
public class CompletableFutureDemo {

    public static void main(String[] args) throws Exception {
        //m1();
        m2();
    }

    /**
     * 演示supplyAsync 异步任务编排
     * 16:43:28.169 [main] c.CompletableFutureDemo - =======Main Over ==========
     * 16:43:30.169 [ForkJoinPool.commonPool-worker-1] c.CompletableFutureDemo - =====>thenApply 3
     */
    private static void m2() {
        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 1;
        }).thenApply(f -> {
            //thenApply 对于第一步完成的任务 继续完成第二步
            return 1 + 2;
        }).whenComplete((v, e) -> {
            //whenComplete 当计算完成时候 我们获得value
            log.debug("=====>thenApply " + v);
        }).exceptionally(e -> {
            // 如果在计算过程中故意制造一些异常则会走到这里 return f / 0
            log.error("计算错误！返回空值", e);
            return null;
        });

        log.info("=======Main Over ==========");
        // 主线程不要立刻结束，否则CompletableFuture使用的默认线程池会立刻关闭 我们这里暂停三秒钟就可以看到结果了
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * CompletableFuture默认使用ForkJoinPool，也可以传入自定义线程池
     * 结果：
     * 16:42:30.310 [pool-1-thread-1] c.CompletableFutureDemo - runAsync
     * 16:42:30.310 [ForkJoinPool.commonPool-worker-1] c.CompletableFutureDemo - runAsync
     * 16:42:30.311 [ForkJoinPool.commonPool-worker-2] c.CompletableFutureDemo - supplyAsync
     * 16:42:30.316 [pool-1-thread-1] c.CompletableFutureDemo - supplyAsync
     */
    private static void m1() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(1,
                4,
                30L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(10));

        CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
            log.info("runAsync");
        });

        CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {
            log.info("runAsync");
        }, executor);

        CompletableFuture<Integer> future3 = CompletableFuture.supplyAsync(() -> {
            log.info("supplyAsync");
            return -1;
        });

        CompletableFuture<Integer> future4 = CompletableFuture.supplyAsync(() -> {
            log.info("supplyAsync");
            return -1;
        }, executor);

        executor.shutdown();
    }
}
