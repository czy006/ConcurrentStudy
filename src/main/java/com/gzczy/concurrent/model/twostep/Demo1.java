package com.gzczy.concurrent.model.twostep;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @Description 二阶段终止-利用 isInterrupted
 * @Author chenzhengyu
 * @Date 2020-12-21 09:52
 */
@Slf4j(topic = "c.Demo1")
public class Demo1 {

    private Thread thread;

    public static void main(String[] args) throws InterruptedException {
        Demo1 demo1 = new Demo1();
        demo1.start();
        TimeUnit.SECONDS.sleep(5);
        demo1.stop();
    }

    public void start(){
        thread = new Thread(()->{
            while (true) {
                Thread thread = Thread.currentThread();
                if (thread.isInterrupted()) {
                    log.info("料理后事");
                    break;
                }
                log.info("监控结果保存");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    thread.interrupt();
                }
            }
        },"t1");
        thread.start();
    }

    public void stop(){
        thread.interrupt();
    }
}
