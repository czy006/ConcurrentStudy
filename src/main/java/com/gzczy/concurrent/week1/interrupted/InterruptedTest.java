package com.gzczy.concurrent.week1.interrupted;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.LockSupport;

import static java.lang.Thread.sleep;

/**
 * 演示interrupted打断的标记符
 */
@Slf4j(topic = "c.InterruptedTest")
public class InterruptedTest {

    public static void main(String[] args) throws Exception {
        InterruptedTest.test1();
        InterruptedTest.test2();
        InterruptedTest.test3();
        InterruptedTest.test4();
    }

    /**
     * 打断 sleep 的线程, 会清空打断状态，以 sleep 为例
     *
     * @throws InterruptedException
     */
    private static void test1() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            try {
                sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t1");
        t1.start();
        sleep(1000);
        t1.interrupt();
        log.debug(" 打断状态: {}", t1.isInterrupted());
    }

    /**
     * 打断正常运行的线程, 不会清空打断状态
     *
     * @throws InterruptedException
     */
    private static void test2() throws InterruptedException {
        Thread t2 = new Thread(() -> {
            while (true) {
                Thread current = Thread.currentThread();
                boolean interrupted = current.isInterrupted();
                if (interrupted) {
                    log.debug(" 打断状态: {}", interrupted);
                    break;
                }
            }
        }, "t2");
        t2.start();
        sleep(2000);
        t2.interrupt();
    }

    /**
     * 打断 park 线程, 不会清空打断状态
     * @throws InterruptedException
     */
    private static void test3() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            log.debug("park...");
            LockSupport.park();
            log.debug("unpark...");
            log.debug("打断状态：{}",Thread.currentThread().isInterrupted());
        },"t1");
        t1.start();
        sleep(2000);
        t1.interrupt();
    }

    /**
     * 打断标记已经是 true, 则 park 会失效
     * @throws InterruptedException
     */
    private static void test4() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            while (true) {
                log.debug("park...");
                LockSupport.park();
                log.debug("打断状态：{}", Thread.currentThread().isInterrupted());
                // 可以使用 Thread.interrupted() 清除打断状态
//                Thread.interrupted();
//                log.debug("打断状态：{}", Thread.currentThread().isInterrupted());
            }
        });
        t1.start();
        sleep(2000);
        t1.interrupt();

    }
}
