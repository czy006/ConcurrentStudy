package com.gzczy.concurrent.heima.a.join;

import lombok.extern.slf4j.Slf4j;

import static java.lang.Thread.sleep;

/**
 * 有时效的join测试
 */
@Slf4j(topic = "c.JoinTestThread")
public class TimeByJoin {

    static int r1 = 0;
    static int r2 = 0;
    public static void main(String[] args) throws InterruptedException {
        test3();
    }
    public static void test3() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            try {
//                sleep(1);
                //当修改为sleep的时间为2s的时候没有等够足够时间，则直接join无效
                sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //还没来的及赋值 已经超时了
            r1 = 10;
        });
        long start = System.currentTimeMillis();
        t1.start();
        // 线程执行结束会导致 join 结束
        t1.join(1500);
        long end = System.currentTimeMillis();
        log.debug("r1: {} r2: {} cost: {}", r1, r2, end - start);
    }
}
