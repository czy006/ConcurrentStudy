package com.gzczy.concurrent.heima.b.threadsafe;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description 线程不安全案例
 * 两个线程对初始值为 0 的静态变量一个做自增，一个做自减，各做 5000 次，结果是 0 吗？
 * @Author chenzhengyu
 * @Date 2020-10-28 17:56
 */
@Slf4j(topic = "c.ThreadUnSafeDemo1")
public class ThreadUnSafeDemo1 {

    static int counter = 0;

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 5000; i++) {
                // 临界区
                counter++;
            }
        }, "t1");

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 5000; i++) {
                // 多个线程在临界区内执行，由于代码的执行序列不同而导致结果无法预测，称之为发生了竞态条件
                counter--;
            }
        }, "t2");
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        log.debug("{}",counter);
    }
}
