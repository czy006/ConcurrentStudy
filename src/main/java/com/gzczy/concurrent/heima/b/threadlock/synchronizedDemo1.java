package com.gzczy.concurrent.heima.b.threadlock;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description 线程8锁案例演示
 * 答案： 瞬间输出1 2 或者 2 1 ，因为需要阻塞等待锁的释放

 * @Author chenzhengyu
 * @Date 2020-11-22 20:47
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
