package com.gzczy.concurrent.cas;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description CAS锁机制模拟测试（请勿用于生产环境）
 * @Author chenzhengyu
 * @Date 2020-11-28 10:25
 */
@Slf4j(topic = "c.CasLockTest")
public class CasLockTest {

    private AtomicInteger state = new AtomicInteger(0);

    public static void main(String[] args) {
        CasLockTest lock = new CasLockTest();
        new Thread(() -> {
            log.debug("begin...");
            lock.lock();
            try {
                log.debug("lock...");
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }).start();
        new Thread(() -> {
            log.debug("begin...");
            lock.lock();
            try {
                log.debug("lock...");
            } finally {
                lock.unlock();
            }
        }).start();
    }

    public void lock(){
        while (true) {
            if (state.compareAndSet(0, 1)) {
                break;
            }
        }
    }

    public void unlock(){
        log.debug("unlock...");
        state.set(0);
    }
}
