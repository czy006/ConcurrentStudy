package com.gzczy.concurrent.heima.b.monitor.activity.reentrant;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description 锁公平
 * @Author chenzhengyu
 * @Date 2020-11-16 09:01
 */
public class ReentrantLockFair {

    public static void main(String[] args) throws Exception {
        //true 为公平锁 开启后 强行插入 running... 总是在最后输出
        ReentrantLock lock = new ReentrantLock(true);
        lock.lock();
        for (int i = 0; i < 500; i++) {
            new Thread(() -> {
                lock.lock();
                try {
                    System.out.println(Thread.currentThread().getName() + " running...");
                } finally {
                    lock.unlock();
                }
            }, "t" + i).start();
        }
        // 1s 之后去争抢锁
        Thread.sleep(900);
        new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " start...");
            lock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + " running...");
            } finally {
                lock.unlock();
            }
        }, "强行插入").start();
        lock.unlock();
    }
}
