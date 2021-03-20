package com.gzczy.concurrent.thread.compare;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 对比start 和 run 的区别
 * 直接调用 run 是在主线程中执行了 run，没有启动新的线程
 * 使用 start 是启动新的线程，通过新的线程间接执行 run 中的代码
 */
@Slf4j(topic = "c.CreateFutureTask")
public class ThreadCompareRun {

    public static void main(String[] args) {
        ThreadCompareRun threadCompareRun = new ThreadCompareRun();
        threadCompareRun.runTest1();
        try {
            // 议用 TimeUnit 的 sleep 代替 Thread 的 sleep 来获得更好的可读性
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        threadCompareRun.runTest2();
    }

    public void runTest1(){
        Thread t1 = new Thread(()->{
            log.debug("观察运行的线程是main线程还是t1线程...");
        },"t1");

        t1.run();
    }

    public void runTest2(){
        Thread t1 = new Thread(()->{
            log.debug("观察运行的线程是main线程还是t1线程...");
        },"t1");

        t1.start();
    }
}
