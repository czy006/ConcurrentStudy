package com.gzczy.concurrent.sync.threadlock;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @Description 线程8锁案例演示
 * 答案： 1s 后 输出 1 2， 或 输出2 的1s后 再输出1
 * 都是对类对象Number6.class 加锁，类对象整个内存只有一份，具有互斥阻塞等待
 * @Author chenzhengyu
 * @Date 2020-10-28 18:47
 */
@Slf4j(topic = "c.synchronizedDemo")
public class synchronizedDemo6 {

    public static void main(String[] args) {
        Number6 n1 = new Number6();
        new Thread(()->{ n1.a(); }).start();
        new Thread(()->{ n1.b(); }).start();
    }
}

@Slf4j(topic = "c.synchronizedDemo")
class Number6 {

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
