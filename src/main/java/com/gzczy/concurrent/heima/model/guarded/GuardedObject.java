package com.gzczy.concurrent.heima.model.guarded;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Description 同步模式之保护暂停（等待结果返回后再继续执行）
 * @Author chenzhengyu
 * @Date 2020-11-04 14:09
 */
@Slf4j(topic = "c.GuardedObject")
public class GuardedObject {

    private Object response;
    private final Object lock = new Object();

    public static void main(String[] args) {
        GuardedObject guardedObject = new GuardedObject();
        new Thread(() -> {
            try {
                log.debug("start download ...");
                // 子线程执行下载 模拟文件正在下载 线程休眠3秒
                List<String> response = new ArrayList<>();
                response.add("download files...");
                TimeUnit.SECONDS.sleep(3);
                log.debug("download complete...");
                guardedObject.complete(response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        },"t1").start();
        log.debug("waiting...");
        // 主线程阻塞等待下载结果
        Object response = guardedObject.get();
        log.debug("get response: [{}] lines", ((List<String>) response).size());
    }

    public Object get() {
        synchronized (lock) {
            // 条件不满足则等待
            while (response == null) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return response;
        }
    }

    public void complete(Object response) {
        synchronized (lock) {
            // 条件满足，通知等待线程
            this.response = response;
            lock.notifyAll();
        }
    }
}
