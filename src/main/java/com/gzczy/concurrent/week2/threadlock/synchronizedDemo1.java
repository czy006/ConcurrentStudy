package com.gzczy.concurrent.week2.threadlock;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description 线程8锁案例演示
 * 答案： 1s 后输出1 2，或输出2 等待1s后输出 1 ，因为需要阻塞等待锁的释放
 * 锁住的是同一对象 锁住的是this对象，所以产生互斥的效果
 * @Author chenzhengyu
 * @Date 2020-10-28 18:47
 */
@Slf4j(topic = "c.synchronizedDemo")
public class synchronizedDemo1 {

    public static void main(String[] args) {
        Number1 n1 = new Number1();
        new Thread(() -> {
            n1.a();
        }).start();
        new Thread(() -> {
            n1.b();
        }).start();
    }
}

@Slf4j(topic = "c.synchronizedDemo")
class Number1 {

    public synchronized void a() {
        log.debug("1");
    }

    public synchronized void b() {
        log.debug("2");
    }
}
