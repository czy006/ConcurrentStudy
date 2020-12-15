package com.gzczy.concurrent.heima.f;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @Description CountdownLatch 基本使用
 * @Author chenzhengyu
 * @Date 2020-12-15 20:14
 */
@Slf4j(topic = "c.CountdownLatchTest")
public class CountdownLatchTest {

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(3);
        Demo1(latch);
    }

    private static void Demo1(CountDownLatch latch) throws InterruptedException {
        new Thread(() -> {
            log.debug("begin...");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            latch.countDown();
            log.debug("end...{}", latch.getCount());
        },"t1").start();

        new Thread(() -> {
            log.debug("begin...");
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            latch.countDown();
            log.debug("end...{}", latch.getCount());
        },"t2").start();

        new Thread(() -> {
            log.debug("begin...");
            try {
                TimeUnit.MILLISECONDS.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            latch.countDown();
            log.debug("end...{}", latch.getCount());
        },"t3").start();

        log.debug("waiting...");
        latch.await();
        log.debug("continue...");
    }
}
