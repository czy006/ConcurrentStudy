package com.gzczy.concurrent.sync.wait;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @Description wait notify demo 测试
 * @Author chenzhengyu
 * @Date 2020-11-04 15:03
 */
@Slf4j(topic = "c.WaitNotifyDemo")
public class WaitNotifyDemo1 {

    final static Object obj = new Object();

    public static void main(String[] args) throws Exception {
        new Thread(() -> {
            synchronized (obj) {
                log.debug("执行....");
                try {
                    obj.wait(); // 让线程在obj上一直等待下去
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.debug("其它代码....");
            }
        }).start();
        new Thread(() -> {
            synchronized (obj) {
                log.debug("执行....");
                try {
                    obj.wait(); // 让线程在obj上一直等待下去
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.debug("其它代码....");
            }
        }).start();
        // 主线程两秒后执行
        TimeUnit.SECONDS.sleep(2);
        log.debug("唤醒 obj 上其它线程");
        synchronized (obj) {
            obj.notify(); // 唤醒obj上一个线程
            // obj.notifyAll();
            // 唤醒obj上所有等待线程
        }
    }
}
