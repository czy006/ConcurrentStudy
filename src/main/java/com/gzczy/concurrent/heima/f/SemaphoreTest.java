package com.gzczy.concurrent.heima.f;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Semaphore;

/**
 * @Description Semaphore基础使用
 * @Author chenzhengyu
 * @Date 2020-12-14 21:03
 */
@Slf4j(topic = "c.semaphore")
public class SemaphoreTest {

    public static void main(String[] args) {
        Semaphore sem = new Semaphore(3);
        // 2. 10个线程同时运行
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                // 3. 获取许可
                try {
                    sem.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    log.debug("running...");
                    Thread.sleep(1000);
                    log.debug("end...");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    // 4. 释放许可
                    sem.release();
                }
            }).start();
        }
    }
}
