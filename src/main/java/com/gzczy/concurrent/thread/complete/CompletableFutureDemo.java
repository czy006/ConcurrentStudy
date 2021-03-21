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
        //m2();
        //m3();
        //m4();
        //m5();
        //m6();
    }

    /**
     * 合并计算结果
     */
    private static void m6() {
        CompletableFuture.supplyAsync(() -> {
            return 10;
        }).thenCombine(CompletableFuture.supplyAsync(()->{
            return 20;
        }),(r1,r2)->{
            return r1+r2;
        }).join();
    }

    /**
     * applyToEither: 哪个先计算完成就选用哪个
     */
    private static void m5() {
        System.out.println(CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 1;
        }).applyToEither(CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 2;
        }), r -> {
            return r;
        }).join());
    }

    private static void m4() {
        //thenAccept内部是void 的 消费型接口
        CompletableFuture.supplyAsync(() -> {
            return 1;
        }).thenApply(x -> {
            return x + 1;
        }).thenAccept(System.out::println).join();

        System.out.println(CompletableFuture.supplyAsync(() -> "resultA").thenRun(() -> {
        }).join());
        System.out.println(CompletableFuture.supplyAsync(() -> "resultA").thenAccept(resultA -> {
        }).join());
        System.out.println(CompletableFuture.supplyAsync(() -> "resultA").thenApply(resultA -> resultA + "resultB").join());
    }

    /**
     * handle 使用方法：即使出现异常仍然能够继续往下走
     */
    private static void m3() {
        System.out.println(CompletableFuture.supplyAsync(() -> {
            return 1;
        }).handle((f, e) -> {
            System.out.println("======1======");
            int i = 10 / 0;
            return f + 2;
        }).handle((f, e) -> {
            System.out.println("======2======");
            return f + 3;
        }).whenComplete((v, e) -> {
            if (e == null) {
                System.out.println("result" + v);
            }
        }).exceptionally(e -> {
            e.printStackTrace();
            return null;
        }).join());
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
