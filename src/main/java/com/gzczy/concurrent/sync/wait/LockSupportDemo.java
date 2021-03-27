package com.gzczy.concurrent.sync.wait;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * @Description LockSupport Demo 进行线程阻塞
 * @Author chenzhengyu
 * @Date 2021-01-31 14:22
 */
@Slf4j(topic = "c.LockSupportDemo")
public class LockSupportDemo {

    public static void main(String[] args) throws InterruptedException {
        //默认是permit 0
        Thread t1 = new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("执行....是否打断--->"+Thread.currentThread().isInterrupted());
            // 调用一次park就会消费permit 由刚刚的1变为0 你继续运行下去吧
            LockSupport.park();
            log.debug("其它代码,是否打断--->"+Thread.currentThread().isInterrupted());
            //再次打住 目前没有许可了 许可为0 那就不放你继续运行啦
            LockSupport.park();
            log.debug("再次park,是否打断--->"+Thread.currentThread().isInterrupted());
            //被打断后会发现无法park住，打断标记已经为true 线程已经被打断了
            LockSupport.park();
        }, "t1");
        t1.start();
        new Thread(() -> {
            log.debug("执行....");
            //调用一次unpark就加1变成1，线程还在运行 没毛病老铁 你继续运行吧
            LockSupport.unpark(t1);
            log.debug("其它代码....");
        }, "t2").start();
        try {
            TimeUnit.SECONDS.sleep(4);
            log.debug("打断t1线程....");
            t1.interrupt();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
