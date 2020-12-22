package com.gzczy.concurrent.heima.model.balking;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @Description Balking （犹豫）模式用在一个线程发现另一个线程或本线程已经做了某一件相同的事，那么本线程就无需再做
 * 了，直接结束返回
 * @Author chenzhengyu
 * @Date 2020-12-22 20:43
 */
@Slf4j(topic = "c.MonitorService")
public class MonitorService {

    private volatile boolean starting;

    public void start() {
        log.info("尝试启动监控线程...");
        synchronized (this) {
            if (starting) {
                log.warn("监控线程已经启动了，无需再次启动...");
                return;
            }
            starting = true;
        }
        while (starting){
            log.info("监控中....");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop(){
        log.info("尝试关闭监控线程...");
        synchronized (this) {
            if (starting) {
                starting = false;
                log.info("关闭监控完毕！");
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        MonitorService monitorService = new MonitorService();
        new Thread(()->monitorService.start(),"t1").start();
        new Thread(()->monitorService.start(),"t2").start();
        TimeUnit.SECONDS.sleep(5);
        monitorService.stop();
    }
}

