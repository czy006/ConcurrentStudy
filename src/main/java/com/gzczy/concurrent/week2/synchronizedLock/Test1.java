package com.gzczy.concurrent.week2.synchronizedLock;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description 面向对象的加锁优化
 * @Author chenzhengyu
 * @Date 2020-10-28 18:35
 */

@Slf4j(topic = "c.Test")
public class Test1 {

    public static void main(String[] args) throws InterruptedException {
        Room room = new Room();
        Thread t1 = new Thread(() -> {
            for (int j = 0; j < 5000; j++) {
                room.increment();
            }
        }, "t1");
        Thread t2 = new Thread(() -> {
            for (int j = 0; j < 5000; j++) {
                room.decrement();
            }
        }, "t2");
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        log.debug("count: {}", room.get());
    }
}

class Room {

    int value = 0;
    //不加 synchronzied 的方法就好比不遵守规则的人，不去老实排队（好比翻窗户进去的）
    public void increment() {
        synchronized (this) {
            value++;
        }
    }

    public synchronized void decrement() {
        // synchronized 移动到方法上是等效作用的
        value--;
    }

    public int get() {
        synchronized (this) {
            return value;
        }
    }
}