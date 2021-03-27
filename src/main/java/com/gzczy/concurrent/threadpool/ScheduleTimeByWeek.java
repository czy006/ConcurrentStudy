package com.gzczy.concurrent.threadpool;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Description 每周四 18:00:00 定时执行任务
 * @Author chenzhengyu
 * @Date 2020-12-09 16:23
 */
public class ScheduleTimeByWeek {

    public static void main(String[] args) {
        //获取当前时间
        LocalDateTime now = LocalDateTime.now();
        //获取本周四 18点这个时间点
        LocalDateTime thursday = now.with(DayOfWeek.THURSDAY)
                .withHour(18).withMinute(0).withSecond(0).withNano(0);
        //如果当前时间已经超过 本周四 18:00:00.000， 那么找下周四 18:00:00.000
        if (now.compareTo(thursday) >= 0){
            thursday = thursday.plusWeeks(1);
        }
        //计算时间差，延时执行时间 = thursday - now
        long initialDelay = Duration.between(now, thursday).toMillis();
        long week =  7 * 24 * 3600 * 1000;

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
        System.out.println("开始时间：" + Calendar.getInstance().getTime());
        executor.scheduleAtFixedRate(() -> {
            System.out.println("执行时间：" + Calendar.getInstance().getTime());
        }, initialDelay, week, TimeUnit.MILLISECONDS);
    }
}
