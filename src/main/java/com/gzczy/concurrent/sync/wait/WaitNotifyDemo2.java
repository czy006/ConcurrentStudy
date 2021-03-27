package com.gzczy.concurrent.sync.wait;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @Description wait notify 测试阻塞
 * @Author chenzhengyu
 * @Date 2021年01月31日 14:12:08
 */
@Slf4j(topic = "c.WaitNotifyDemo")
public class WaitNotifyDemo2 {

    final static Object obj = new Object();

    public static void main(String[] args) throws Exception {

        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (obj) {
                log.debug("执行....");
                try {
                    obj.wait(); // 让线程在obj上一直等待下去
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.debug("其它代码....");
            }
        },"t1").start();
        new Thread(() -> {
            synchronized (obj) {
                log.debug("执行....");
                obj.notify(); // 让线程在obj上一直等待下去
                log.debug("其它代码....");
            }
        },"t2").start();
    }
}
