package com.gzczy.concurrent.week1.exercise;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 二阶段提交中断 【后面使用wait实现，这里使用现在学的知识先】
 */
@Slf4j(topic = "c.TwoStepInterrupted")
public class TwoStepInterrupted {

    private Thread monitor;

    public static void main(String[] args) throws InterruptedException {
        TwoStepInterrupted t1 = new TwoStepInterrupted();
        t1.start();
        TimeUnit.SECONDS.sleep(30);
        t1.stop();
    }

    /**
     * 启动
     */
    public void start(){
        monitor = new Thread(()->{
            while (true){
                Thread currentThread = Thread.currentThread();
                if (currentThread.isInterrupted()){
                    log.debug("中断状态："+ currentThread.isInterrupted());
                    break;
                }
                try {
                    TimeUnit.SECONDS.sleep(2);
                    log.debug("执行监控中，推送监控数据至kafka...");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    //重新设置打断标记
                    currentThread.interrupt();
                }
            }
        },"monitor");
        monitor.start();
    }

    public void stop(){
        monitor.interrupt();
    }
}
