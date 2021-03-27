package com.gzczy.concurrent.cas;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;


/**
 * 实现自定义Lock
 * @Author chenzhengyu
 * @Date 2020-12-10 18:45
 */
class MyLock implements Lock {

    /**
     * @Description 自定义同步器
     * @Author chenzhengyu
     * @Date 2020-12-10 16:45
     */
    class MySync extends AbstractQueuedSynchronizer {

        @Override
        protected boolean tryAcquire(int acquire) {

            //通过cas对比
            if (compareAndSetState(0, 1)) {
                //给当前线程加锁
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }

            return false;
        }

        @Override
        protected boolean tryRelease(int acquire) {
            setExclusiveOwnerThread(null);
            setState(0);
            return true;
        }

        public Condition newCondition() {
            return new ConditionObject();
        }

        @Override
        protected boolean isHeldExclusively() {
            return getState() == 1;
        }
    }

    private MySync sync = new MySync();

    /**
     * 尝试加锁 加锁失败 则进入等待队列
     */
    @Override
    public void lock() {
        sync.acquire(1);
    }

    /**
     * 尝试，不成功，进入等待队列，可打断
     *
     * @throws InterruptedException
     */
    @Override
    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }

    /**
     * 尝试一次，不成功返回，不进入队列
     *
     * @return
     */
    @Override
    public boolean tryLock() {
        return sync.tryAcquire(1);
    }

    /**
     * 尝试，不成功，进入等待队列，有时限
     *
     * @param time
     * @param unit
     * @return
     * @throws InterruptedException
     */
    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireNanos(1, unit.toNanos(time));
    }

    /**
     * 释放锁
     */
    @Override
    public void unlock() {
        sync.release(1);
    }

    /**
     * 生成变量条件
     *
     * @return
     */
    @Override
    public Condition newCondition() {
        return sync.newCondition();
    }
}

@Slf4j(topic = "c.TestMySync")
public class TestMySync {

    public static void main(String[] args) {

        MyLock lock = new MyLock();
        new Thread(() -> {
            //锁不可重入 如果不解锁 会一直阻塞等待
            lock.lock();
            log.debug("locking...");
            lock.lock();
            try {
                log.debug("locking...");
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } finally {
                log.debug("unlocking...");
                lock.unlock();
            }
        }, "t1").start();

        new Thread(() -> {
            lock.lock();
            try {
                log.debug("locking...");
            } finally {
                log.debug("unlocking...");
                lock.unlock();
            }
        }, "t2").start();
    }
}