package com.gzczy.concurrent.heima.f;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * @Description 定时任务测试
 * @Author chenzhengyu
 * @Date 2020-12-09 14:47
 */

@Slf4j(topic = "c.TimerTest")
public class TimerTest {

    public static void main(String[] args) {

        Timer timer = new Timer();

        TimerTask taskA = new TimerTask() {
            @SneakyThrows
            @Override
            public void run() {
                log.info("TaskA is Running ...");
                TimeUnit.SECONDS.sleep(5);
            }
        };

        TimerTask taskB = new TimerTask() {

            @Override
            public void run() {
                log.info("TaskB is Running ...");
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //故意出错
                int i = 1 / 0;
            }
        };

        TimerTask taskC = new TimerTask() {
            @SneakyThrows
            @Override
            public void run() {
                log.info("TaskC is Running ...");
            }
        };

        timer.schedule(taskA,1000);
        //任务是串行的，可以看到就算设置了TaskB 1s后执行，但是由于执行时间太久，所以会等待A执行完才会执行B
        timer.schedule(taskB,1000);
        //出错后 抛出异常后不处理剩余的任务不会结束
        timer.schedule(taskC,10000);

    }
}
