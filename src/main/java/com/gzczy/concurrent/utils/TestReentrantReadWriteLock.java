package com.gzczy.concurrent.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @Description 测试读写锁
 * @Author chenzhengyu
 * @Date 2020-12-13 15:22
 */
@Slf4j(topic = "c.TestReentrantReadWriteLock")
public class TestReentrantReadWriteLock {

    public static void main(String[] args) throws InterruptedException {
        DataContainer container = new DataContainer();
        // read - read 模式
        log.info("==========read - read 模式===========");
        readLockTest(container);
        TimeUnit.SECONDS.sleep(5);
        log.info("==========write - write 模式===========");
        // write - write 模式
        writeLockTest(container);
        TimeUnit.SECONDS.sleep(5);
        // read - write 模式
        log.info("==========read - write 模式===========");
        readWriteLock(container);
        TimeUnit.SECONDS.sleep(5);
        // write - read 模式
        log.info("==========write - read 模式===========");
        writeReadLock(container);
    }

    private static void writeReadLock(DataContainer container) throws InterruptedException {
        new Thread(container::write,"t5").start();
        TimeUnit.MILLISECONDS.sleep(100);
        new Thread(container::read,"t6").start();
    }

    private static void readWriteLock(DataContainer container) throws InterruptedException {
        new Thread(container::read,"t5").start();
        TimeUnit.MILLISECONDS.sleep(500);
        new Thread(container::write,"t6").start();
    }

    private static void writeLockTest(DataContainer container) {
        new Thread(container::write,"t3").start();
        new Thread(container::write,"t4").start();
    }

    /**
     * 读取操作并不会阻塞 大家同时读
     * @param container
     */
    private static void readLockTest(DataContainer container) {
        new Thread(container::read,"t1").start();
        new Thread(container::read,"t2").start();
    }
}

@Slf4j(topic = "c.TestReentrantReadWriteLock")
class DataContainer{

    private Object data;

    private ReentrantReadWriteLock rw = new ReentrantReadWriteLock();
    private ReentrantReadWriteLock.ReadLock r = rw.readLock();
    ReentrantReadWriteLock.WriteLock w = rw.writeLock();

    public Object read(){
        log.info("获取读锁");
        r.lock();
        try {
            log.info("reading...." );
            TimeUnit.SECONDS.sleep(1);
            return data;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } finally {
            log.info("释放读锁");
            r.unlock();
        }
    }

    public void write(){
        log.info("写入数据,加入写锁");
        w.lock();
        try {
            log.info("writing...." );
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            log.info("释放写锁");
            w.unlock();
        }
    }
}