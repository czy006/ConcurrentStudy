package com.gzczy.concurrent.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @Description 源自ReentrantReadWriteLock 源代码文档提供Demo
 * @Author chenzhengyu
 * @Date 2020-12-13 19:00
 */
@Slf4j(topic = "c.CachedData")
public class CachedData {

    public static void main(String[] args) {
        CachedData cachedData  = new CachedData();
        new Thread(()->{cachedData.processCachedData();},"t1").start();
        new Thread(()->{cachedData.processCachedData();},"t2").start();
    }

    Object data;
    //是否有效缓存
    volatile boolean cacheValid;
    final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();

    void processCachedData() {
        log.info("第一次读锁 上锁....{}", data);
        rwl.readLock().lock();
        if (!cacheValid) {
            // 获取写锁前必须释放读锁，不支持锁升级
            log.info("第一次读锁 解锁....{}", data);
            rwl.readLock().unlock();
            log.info("第一次写锁 上锁....{}", data);
            rwl.writeLock().lock();
            try {
                //判断其他线程是否已经获得写锁，去更新缓存，避免重复更新 双重检验
                log.info("双重检查..{}", cacheValid);
                if (!cacheValid) {
                    data = new Object();
                    cacheValid = true;
                    log.info("更新缓存....{}", data);
                }
                // 降级为读锁，释放写锁，这样能够让其他线程读取缓存
                rwl.readLock().lock();
                log.info("第二次读锁 上锁....{}", data);
            } finally {
                log.info("第一次写锁 解锁....{}", data);
                //读锁释放的瞬间可以让其他线程过来读
                rwl.writeLock().unlock();
            }
        }
        //自己用完数据 然后释放读锁
        try {
            //假设我们用了1s去执行这个任务
            try {
                log.info("读取数据中....{}",data);
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } finally {
            log.info("第二次读锁 解锁....{}", data);
            rwl.readLock().unlock();
        }
    }
}
