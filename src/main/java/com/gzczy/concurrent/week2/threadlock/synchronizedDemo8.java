package com.gzczy.concurrent.week2.threadlock;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @Description 线程8锁案例演示
 * 答案： 1s 后 输出1 2， 或 2 1s后输出1
 * 锁的都是Number8.class的类对象
 * @Author chenzhengyu
 * @Date 2020-10-28 18:47
 */
@Slf4j(topic = "c.synchronizedDemo")
public class synchronizedDemo8 {

    public static void main(String[] args) {
        Number8 n1 = new Number8();
        Number8 n2 = new Number8();
        new Thread(()->{ n1.a(); }).start();
        new Thread(()->{ n2.b(); }).start();
    }
}

@Slf4j(topic = "c.synchronizedDemo")
class Number8 {

    public static synchronized void a() {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.debug("1");
    }

    public static synchronized void b() {
        log.debug("2");
    }

}
