package com.gzczy.concurrent.heima.b.threadlock;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @Description 线程8锁案例演示
 * 答案： 3 等待1s 输出1 和 2 ；2 3 输出后 等待 1s 输出1 ； 3 2 输出后等待 1s 输出1
 * c也是由n1对象前往调用，但是没有添加锁关键字，所以更不不会考虑互斥，会并行的执行
 * @Author chenzhengyu
 * @Date 2020-10-28 18:47
 */
@Slf4j(topic = "c.synchronizedDemo")
public class synchronizedDemo3 {

    public static void main(String[] args) {
        Number3 n1 = new Number3();
        new Thread(() -> {
            n1.a();
        }).start();
        new Thread(() -> {
            n1.b();
        }).start();
        new Thread(() -> {
            n1.c();
        }).start();
    }
}

@Slf4j(topic = "c.synchronizedDemo")
class Number3 {

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

    public void c(){
        log.debug("3");
    }
}
