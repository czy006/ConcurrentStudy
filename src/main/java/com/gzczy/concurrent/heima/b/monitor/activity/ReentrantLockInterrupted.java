package com.gzczy.concurrent.heima.b.monitor.activity;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description 锁可打断演示
 * @Author chenzhengyu
 * @Date 2020-11-15 22:32
 */
@Slf4j(topic = "c.ReentrantLockInterrupted")
public class ReentrantLockInterrupted {

    public static void main(String[] args) {
        ReentrantLock lock = new ReentrantLock();
        Thread t1 = new Thread(() -> {

            log.debug("启动...");
            try {
                //使用可打断的API
                lock.lockInterruptibly();
                //注意如果是不可中断模式，那么即使使用了 interrupt 也不会让等待中断
                //lock.lock();
            } catch (Exception e) {
                e.printStackTrace();
                log.debug("等锁的过程中被打断");
                return;
            }
            try {
                log.debug("获得了锁");
            } finally {
                lock.unlock();
            }
        }, "t1");
        lock.lock();
        log.debug("获得了锁");
        t1.start();
        try {
            TimeUnit.SECONDS.sleep(1);
            t1.interrupt();
            log.debug("执行打断");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
