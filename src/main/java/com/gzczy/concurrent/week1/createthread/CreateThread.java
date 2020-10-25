package com.gzczy.concurrent.week1.createthread;

import lombok.extern.slf4j.Slf4j;

/**
 * Thread创建线程的方式
 * 1. 定义Thread类的子类，并重写该类的run方法，该run方法的方法体就代表了线程要完成的任务。因此把run()方法称为执行体。
 * 2.创建Thread子类的实例，即创建了线程对象。
 * 3.调用线程对象的start()方法来启动该线程。
 */
@Slf4j(topic = "c.CreateThread")
public class CreateThread extends Thread {

    @Override
    public void run() {
        log.debug("I am thread start ...");
    }

    public static void main(String[] args) {
        CreateThread createThread = new CreateThread();
        createThread.setName("t1");
        createThread.start();
    }
}
