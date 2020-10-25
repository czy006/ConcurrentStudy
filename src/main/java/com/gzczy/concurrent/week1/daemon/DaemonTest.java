package com.gzczy.concurrent.week1.daemon;

import lombok.extern.slf4j.Slf4j;

import static java.lang.Thread.sleep;

/**
 * 默认情况下，Java 进程需要等待所有线程都运行结束，才会结束。有一种特殊的线程叫做守护线程，只要其它非守
 * 护线程运行结束了，即使守护线程的代码没有执行完，也会强制结束
 */
@Slf4j(topic = "c.DaemonTest")
public class DaemonTest {

    public static void main(String[] args) throws InterruptedException {
        log.debug("开始运行...");
        Thread t1 = new Thread(() -> {
            log.debug("开始运行...");
            try {
                sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("运行结束...");
        }, "daemon");
        // 设置该线程为守护线程
        t1.setDaemon(true);
        t1.start();
        sleep(2000);
        log.debug("运行结束...");
    }
}
