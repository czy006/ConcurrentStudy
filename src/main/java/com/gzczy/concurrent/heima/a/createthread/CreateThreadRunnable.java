package com.gzczy.concurrent.heima.a.createthread;

import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "c.createThreadRunnable")
public class CreateThreadRunnable {

    public static void main(String[] args) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                log.debug("runnable create thread ...");
            }
        };

        Thread t1 = new Thread(runnable,"t1");
        t1.start();
    }
}
