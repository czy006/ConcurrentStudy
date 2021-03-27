package com.gzczy.concurrent.model.twostep;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @Description 二阶段终止-利用 volatile 可见性
 * @Author chenzhengyu
 * @Date 2020-12-21 09:52
 */
@Slf4j(topic = "c.Demo2")
public class Demo2 {

    private Thread thread;

    private Boolean isStop = false;

    public static void main(String[] args) throws InterruptedException {
        Demo2 demo2 = new Demo2();
        demo2.start();
        TimeUnit.SECONDS.sleep(5);
        demo2.stop();
    }

    public void start(){
        thread = new Thread(()->{
            while (true) {
                if (isStop) {
                    log.info("料理后事");
                    break;
                }
                log.info("监控结果保存");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    isStop = true;
                    thread.interrupt();
                }
            }
        },"t1");
        thread.start();
    }

    public void stop(){
        isStop = true;
        thread.interrupt();
    }
}
