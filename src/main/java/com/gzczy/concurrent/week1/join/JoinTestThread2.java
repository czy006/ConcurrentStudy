package com.gzczy.concurrent.week1.join;

import lombok.extern.slf4j.Slf4j;

import static java.lang.Thread.sleep;

/**
 * 实验结论：
 *  join
 *  sleep
 */
@Slf4j(topic = "c.JoinTestThread")
public class JoinTestThread2 {

    static int r1 = 0;
    static int r2 = 0;
    public static void main(String[] args) throws InterruptedException {
        test2();
    }
    private static void test2() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            try {
                sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            r1 = 10;
        });
        Thread t2 = new Thread(() -> {
            try {
                sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            r2 = 20;
        });
        long start = System.currentTimeMillis();
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        long end = System.currentTimeMillis();
        log.debug("r1: {} r2: {} cost: {}", r1, r2, end - start);
    }
}
