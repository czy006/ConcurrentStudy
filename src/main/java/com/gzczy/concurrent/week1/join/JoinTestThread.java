package com.gzczy.concurrent.week1.join;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 实验结论：
 *  join
 *  sleep
 */
@Slf4j(topic = "c.JoinTestThread")
public class JoinTestThread {

    static int r = 0;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("start....");
        Thread t1 = new Thread(()->{
            log.debug("start by thread ...");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("end by thread");
            r = 10;
        });
        t1.start();
        //观察对比加入join后的r输出结果和没加入时候的结果
//        t1.join();
//        TimeUnit.SECONDS.sleep(2);
        log.debug("r result is {}",r);
        log.debug("end...");
    }
}
