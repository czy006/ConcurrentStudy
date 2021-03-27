package com.gzczy.concurrent.threadpool;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

/**
 * @Description 线程池测试类
 * @Author chenzhengyu
 * @Date 2020-12-08 10:31
 */
@Slf4j(topic = "c.ExecutorPoolTest")
public class ExecutorPoolTest {

    public static void main(String[] args) throws Exception {
        //ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        //extractedCachedPool(cachedThreadPool);

        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(2);
        extractedFixedPool(fixedThreadPool);
        exceptionTest(fixedThreadPool);

    }

    /**
     * 线程池处理异常的2种方法
     * 1）主动try catch 捕获异常
     * 2）Future 返回异常
     * @param fixedThreadPool
     * @throws InterruptedException
     * @throws ExecutionException
     */
    private static void exceptionTest(ExecutorService fixedThreadPool) throws InterruptedException, ExecutionException {
        //使用 Future接收错误并且输出
        Future<Boolean> f = fixedThreadPool.submit(() -> {
            log.debug("task1");
            int i = 1 / 0;
            return true;
        });
        log.debug("result:{}", f.get());
    }

    private static void extractedFixedPool(ExecutorService fixedThreadPool) throws InterruptedException, ExecutionException {
        fixedThreadPool.submit(() -> {
            log.info("start 1");
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("end 1");
        });

        fixedThreadPool.invokeAny(Arrays.asList(
                () -> {
                    log.info("start 2");
                    TimeUnit.MILLISECONDS.sleep(500);
                    return "2";
                },
                () -> {
                    log.info("start 3");
                    TimeUnit.MILLISECONDS.sleep(1500);
                    return "3";
                }
        ));

        //内部实现直接打断
        fixedThreadPool.shutdownNow();
        log.info("is shutDown now ? {}", fixedThreadPool.isShutdown());
    }

    /**
     * 测试 cachedThreadPool
     *
     * @param cachedThreadPool
     * @throws InterruptedException
     */
    private static void extractedCachedPool(ExecutorService cachedThreadPool) throws InterruptedException {
        List<Future<Object>> resultList = cachedThreadPool.invokeAll(Arrays.asList(
                () -> {
                    log.info("start 1");
                    TimeUnit.SECONDS.sleep(1);
                    return "1";
                },
                () -> {
                    log.info("start 2");
                    TimeUnit.MILLISECONDS.sleep(500);
                    return "2";
                },
                () -> {
                    log.info("start 3");
                    TimeUnit.MILLISECONDS.sleep(1500);
                    return "3";
                }
        ));

        // 异步遍历get 数据
        resultList.forEach(x -> {
            try {
                log.info("result - > {}", x.get().toString());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });

        cachedThreadPool.shutdown();
        //shutdown后再提交触发拒绝策略
        cachedThreadPool.submit(() -> {
            TimeUnit.SECONDS.sleep(1);
            return "after shutdown test";
        });
    }
}
