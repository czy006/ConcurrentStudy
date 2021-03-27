package com.gzczy.concurrent.threadpool;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Description 定时任务调度池任务测试
 * @Author chenzhengyu
 * @Date 2020-12-09 14:59
 */
@Slf4j(topic = "c.ScheduleTimerTest")
public class ScheduleTimerTest {

    public static void main(String[] args) {
        //当corePoolSize设置为1时候，任务执行为串行，当设置>2时候为并行
        ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(1);

        demo1(scheduledThreadPool);
        demo2(scheduledThreadPool);
        demo3(scheduledThreadPool);
    }

    /**
     * 定时任务调度，2s后执行第一次，之后每隔3s执行一次。如果中间执行时间 > 定时时间 ，则执行完后需要再等待3s才会再次执行
     * @param scheduledThreadPool
     */
    private static void demo3(ScheduledExecutorService scheduledThreadPool) {
        log.info("scheduleAtFixedRate beginning ...");
        scheduledThreadPool.scheduleWithFixedDelay(()->{
            log.info("scheduledThreadPool scheduleAtFixedRate run task ...");
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, 2, 3, TimeUnit.SECONDS);
    }

    /**
     * 定时任务调度，2s后执行第一次，之后每隔3s执行一次,如果中间执行时间 > 定时时间 ，则执行完后再会执行
     * @param scheduledThreadPool
     */
    private static void demo2(ScheduledExecutorService scheduledThreadPool) {
        log.info("scheduleAtFixedRate beginning ...");
        scheduledThreadPool.scheduleAtFixedRate(()->{
            log.info("scheduledThreadPool scheduleAtFixedRate run task ...");
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, 2, 3, TimeUnit.SECONDS);
    }

    /**
     * 按照指定时间后执行任务
     * @param scheduledThreadPool
     */
    private static void demo1(ScheduledExecutorService scheduledThreadPool) {
        scheduledThreadPool.schedule(()->{
            log.info("scheduledThreadPool run task ...");
        }, 1, TimeUnit.SECONDS);
    }


}
