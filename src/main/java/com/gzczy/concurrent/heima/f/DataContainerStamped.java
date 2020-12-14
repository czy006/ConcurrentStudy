package com.gzczy.concurrent.heima.f;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.StampedLock;

/**
 * @Description 提供一个 数据容器类 内部分别使用读锁保护数据的 read() 方法，写锁保护数据的 write() 方法
 * StampedLock
 * @Author chenzhengyu
 * @Date 2020-12-14 09:39
 */
@Slf4j(topic = "c.stampedLock")
public class DataContainerStamped {

    private int data;

    private final StampedLock stampedLock = new StampedLock();

    public DataContainerStamped(int data) {
        this.data = data;
    }

    /**
     * 读取数据
     * @param readTime 传入等待时间（仅仅是测试）
     * @return
     * @throws InterruptedException
     */
    public int read(int readTime) throws InterruptedException {
        long stamp = stampedLock.tryOptimisticRead();
        log.debug("optimistic read locking...{}", stamp);
        TimeUnit.SECONDS.sleep(readTime);
        //首先先尝试使用乐观读 去读取 对比戳是否一致
        if (stampedLock.validate(stamp)) {
            log.debug("read finish...{}, data:{}", stamp, data);
            return data;
        }
        //锁升级 - 读锁
        log.debug("updating to read lock... {}", stamp);
        try {
            stamp = stampedLock.readLock();
            log.debug("read lock {}", stamp);
            TimeUnit.SECONDS.sleep(readTime);;
            log.debug("read finish...{}, data:{}", stamp, data);
            return data;
        } finally {
            log.debug("read unlock {}", stamp);
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
        log.debug("write lock {}", stamp);
        try {
            TimeUnit.SECONDS.sleep(2);
            this.data = newData;
        } finally {
            log.debug("write unlock {}", stamp);
            stampedLock.unlockWrite(stamp);
        }
    }
}

class TestStampedLock{

    public static void main(String[] args) throws Exception {
        DataContainerStamped dataContainer = new DataContainerStamped(1);
        //读-读模式 基于乐观读
        ReadToRead(dataContainer);
        //测试 读-写 时优化读补加读锁
        TimeUnit.SECONDS.sleep(5);
        System.out.println("=============WriteToRead=============");
        WriteToRead(dataContainer);
    }

    private static void WriteToRead(DataContainerStamped dataContainer) throws InterruptedException {
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
                dataContainer.write(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t2").start();
    }

    private static void ReadToRead(DataContainerStamped dataContainer) throws InterruptedException {
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
