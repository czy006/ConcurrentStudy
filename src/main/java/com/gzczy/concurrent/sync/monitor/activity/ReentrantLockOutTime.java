package com.gzczy.concurrent.sync.monitor.activity;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description reentrantLock锁超时测试
 * @Author chenzhengyu
 * @Date 2020-11-16 08:52
 */
@Slf4j(topic = "c.ReentrantLockOutTime")
public class ReentrantLockOutTime {

    public static void main(String[] args) {
        ReentrantLock lock = new ReentrantLock();
        Thread t1 = new Thread(() -> {
            log.debug("启动...");
            //if (!lock.tryLock()) {
            //    log.debug("获取立刻失败，返回");
            //    return;
            //}
            //超时失败后返回
            try {
                if (!lock.tryLock(1, TimeUnit.SECONDS)) {
                    log.debug("获取等待 1s 后失败，返回");
                    return;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
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
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
