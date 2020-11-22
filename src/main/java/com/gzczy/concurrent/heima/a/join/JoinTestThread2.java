package com.gzczy.concurrent.heima.a.join;

import lombok.extern.slf4j.Slf4j;

import static java.lang.Thread.sleep;

/**
 分析如下
 第一个 join：等待 t1 时, t2 并没有停止, 而在运行
 第二个 join：1s 后, 执行到此, t2 也运行了 1s, 因此也只需再等待 1s
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
        //尝试一下两个顺序交换
        t2.start();
        t1.start();
        t1.join();
        t2.join();
        long end = System.currentTimeMillis();
        log.debug("r1: {} r2: {} cost: {}", r1, r2, end - start);
    }
}
