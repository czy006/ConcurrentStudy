package com.gzczy.concurrent.heima.d;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.LongAdder;

/**
 * @Description 自旋锁自定义实现
 * 尚硅谷-大厂面试第二季 自旋锁原理Demo
 * https://www.bilibili.com/video/BV18b411M7xz?p=29
 * @Author chenzhengyu
 * @Date 2021-02-04 13:31
 */
@Slf4j(topic = "c.SpinLock")
public class SpinLockDemo {

    private AtomicReference<Thread> atomic = new AtomicReference<>();

    private LongAdder count = new LongAdder();

    public void lock(){
        Thread t = Thread.currentThread();
        while (!atomic.compareAndSet(null,t) && count.intValue() < 10){
            log.info("SpinLock try in ... count ====>" + count.intValue());
            count.increment();
        }
    }

    public void unlock(){
        Thread t = Thread.currentThread();
        if (atomic.compareAndSet(t,null)){
            count.reset();
        }
        log.info("unlock");
    }


    public static void main(String[] args) {
        SpinLockDemo lock = new SpinLockDemo();
        Thread t1 = new Thread(()->{
            lock.lock();
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            lock.unlock();
        },"t1");
        t1.start();

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Thread t2 = new Thread(()->{
            lock.lock();
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            lock.unlock();
        },"t2");
        t2.start();

    }
}
