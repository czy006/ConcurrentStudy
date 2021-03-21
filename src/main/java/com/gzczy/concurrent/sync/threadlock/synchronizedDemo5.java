package com.gzczy.concurrent.sync.threadlock;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

/**
 * @Description 线程8锁案例演示
 * 答案： 先输出2 ，1秒后 输出1
 * 它们锁住的对象是不一样的，所以是没有互斥的
 * @Author chenzhengyu
 * @Date 2020-10-28 18:47
 */
@Slf4j(topic = "c.synchronizedDemo")
public class synchronizedDemo5 {

    public static void main(String[] args) {
        Number5 n1 = new Number5();
        new Thread(()->{ n1.a(); }).start();
        new Thread(()->{ n1.b(); }).start();
    }
}

@Slf4j(topic = "c.synchronizedDemo")
class Number5 {

    /**
     * 添加了static，所以锁住的是类的对象
     */
    public static synchronized void a() {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.debug("1");
    }

    /**
     * 锁住的对象是this对象
     */
    public synchronized void b() {
        log.debug("2");
    }

}
