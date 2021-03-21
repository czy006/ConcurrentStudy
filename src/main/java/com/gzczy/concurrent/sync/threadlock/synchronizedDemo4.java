package com.gzczy.concurrent.sync.threadlock;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @Description 线程8锁案例演示
 * 答案： 先输出2 1秒后 输出1
 * n1 n2 锁住不是同一对象，不是互斥 所以没有相互影响 2总是会被先打印出来
 * @Author chenzhengyu
 * @Date 2020-10-28 18:47
 */
@Slf4j(topic = "c.synchronizedDemo")
public class synchronizedDemo4 {

    public static void main(String[] args) {
        Number4 n1 = new Number4();
        Number4 n2 = new Number4();
        new Thread(()->{ n1.a(); }).start();
        new Thread(()->{ n2.b(); }).start();
    }
}

@Slf4j(topic = "c.synchronizedDemo")
class Number4 {

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
