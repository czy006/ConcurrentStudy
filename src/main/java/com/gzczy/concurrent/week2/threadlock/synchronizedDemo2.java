package com.gzczy.concurrent.week2.threadlock;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @Description 线程8锁案例演示
 * 答案： 1 2 或者 2 1
 * 如果先执行a 就先获得锁 等待1s后输出1 然后输出2；如果cpu先调度线程2，1s后再输出1
 * @Author chenzhengyu
 * @Date 2020-10-28 18:47
 */
@Slf4j(topic = "c.synchronizedDemo")
public class synchronizedDemo2 {

    public static void main(String[] args) {
        Number2 n1 = new Number2();
        new Thread(() -> {
            n1.a();
        }).start();
        new Thread(() -> {
            n1.b();
        }).start();
    }
}

@Slf4j(topic = "c.synchronizedDemo")
class Number2 {

    public synchronized void a() {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.debug("1");
    }

    public synchronized void b() {
        log.debug("2");
    }
}
