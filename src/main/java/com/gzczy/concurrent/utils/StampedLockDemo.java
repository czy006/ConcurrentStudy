package com.gzczy.concurrent.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.StampedLock;

/**
 * @Description StampedLock
 *
 * ReentrantReadWriteLock允许多个线程同时读，但是只允许一个线程写，在线程获取到写锁的时候，
 * 其他写操作和读操作都会处于阻塞状态，读锁和写锁也是互斥的，所以在读的时候是不允许写的，读写锁比传统的synchronized速度要快很多，
 * 原因就是在于ReentrantReadWriteLock支持读并发
 *
 * StampedLock横空出世ReentrantReadWriteLock的读锁被占用的时候，其他线程尝试获取写锁的时候会被阻塞。
 * 但是，StampedLock采取乐观获取锁后，其他线程尝试获取写锁时不会被阻塞，这其实是对读锁的优化
 * 所以，在获取乐观读锁后，还需要对结果进行校验。
 * 它是JDK1.8新增的一个读写锁，我们通常称为邮戳锁（票据锁），通过Stamp（戳记）去代表了锁的状态，当stamp返回零的时候，
 * 标识线程获取锁失败。
 *
 * @Author chenzhengyu
 * @Date 2021年04月02日00:18:02
 */
@Slf4j(topic = "c.stampedLock")
public class StampedLockDemo {

    private int data;

    private final StampedLock stampedLock = new StampedLock();

    public StampedLockDemo(int data) {
        this.data = data;
    }

    public int read(int readTime) throws InterruptedException {
        long stamp = stampedLock.readLock();
        log.debug("尝试使用悲观读...{}", stamp);
        try {
            TimeUnit.SECONDS.sleep(readTime);;
            log.debug("悲观读完成...{}, data:{}", stamp, data);
            return data;
        }finally {
            log.debug("读锁解锁 {}", stamp);
            stampedLock.unlockRead(stamp);
        }
    }

    /**
     * 读取数据（先乐观读，读取失败升级悲观读）
     * @param readTime 传入等待时间（仅仅是测试）
     * @return
     * @throws InterruptedException
     */
    public int tryOptimisticRead(int readTime) throws InterruptedException {
        long stamp = stampedLock.tryOptimisticRead();
        log.debug("尝试使用乐观读...{}", stamp);
        TimeUnit.SECONDS.sleep(readTime);
        //首先先尝试使用乐观读 去读取 对比戳是否一致 如果一致直接返回
        if (stampedLock.validate(stamp)) {
            log.debug("乐观读完成...{}, data:{}", stamp, data);
            return data;
        }
        //锁升级 - 读锁（悲观策略）
        log.debug("触发锁升级策略 ... {}", stamp);
        stamp = stampedLock.readLock();
        try {
            log.debug("读锁重新获取票据，Stamp: {}", stamp);
            TimeUnit.SECONDS.sleep(readTime);;
            log.debug("读锁完成，Stamp:{}, data:{}", stamp, data);
            return data;
        } finally {
            log.debug("读锁解锁 {}", stamp);
            stampedLock.unlockRead(stamp);
        }
    }

    /**
     * 写入数据
     * @param newData
     * @throws InterruptedException
     */
    public void write(int newData) throws InterruptedException {
        long stamp = stampedLock.writeLock();
        log.debug("写锁，Stamp: {} ，=====写线程准备修改", stamp);
        try {
            TimeUnit.SECONDS.sleep(2);
            this.data = newData;
        } finally {
            log.debug("写锁解锁,Stamp: {} ，=====写线程结束修改", stamp);
            stampedLock.unlockWrite(stamp);
        }
    }
}

class TestStampedLock{

    public static void main(String[] args) throws Exception {
        StampedLockDemo dataContainer = new StampedLockDemo(1);
        System.out.println("=============悲观读，成功演示=============");
        //悲观读
        PessimisticRead(dataContainer);
        TimeUnit.SECONDS.sleep(5);
        System.out.println("=============乐观读，成功演示=============");
        //读-读 模式 基于乐观读
        TimeUnit.SECONDS.sleep(5);
        tryOptimisticReadTrue(dataContainer);
        //测试 读-写 时优化读补加读锁
        TimeUnit.SECONDS.sleep(5);
        System.out.println("=============乐观读，失败，重新转为悲观读，重读数据一次演示=============");
        tryOptimisticReadFalse(dataContainer);
    }

    /**
     * 乐观读，失败，重新转为悲观读，重读数据一次
     * @param dataContainer
     * @throws InterruptedException
     */
    private static void tryOptimisticReadFalse(StampedLockDemo dataContainer) throws InterruptedException {
        new Thread(() -> {
            try {
                dataContainer.tryOptimisticRead(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t1").start();
        TimeUnit.MILLISECONDS.sleep(300);
        new Thread(() -> {
            try {
                //写入数据为100
                dataContainer.write(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t2").start();
    }

    /**
     * 乐观读，成功
     * @param dataContainer
     * @throws InterruptedException
     */
    private static void tryOptimisticReadTrue(StampedLockDemo dataContainer) throws InterruptedException {
        new Thread(() -> {
            try {
                dataContainer.tryOptimisticRead(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t1").start();
        TimeUnit.MILLISECONDS.sleep(500);
        new Thread(() -> {
            try {
                dataContainer.tryOptimisticRead(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t2").start();
    }

    /**
     * 悲观读
     * @param dataContainer
     * @throws InterruptedException
     */
    private static void PessimisticRead(StampedLockDemo dataContainer) throws InterruptedException {
        new Thread(() -> {
            try {
                dataContainer.read(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t1").start();
        TimeUnit.MILLISECONDS.sleep(500);
        new Thread(() -> {
            try {
                dataContainer.read(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t2").start();
    }
}
