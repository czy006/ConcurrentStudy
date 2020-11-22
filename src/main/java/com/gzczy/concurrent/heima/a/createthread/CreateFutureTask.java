package com.gzczy.concurrent.heima.a.createthread;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

@Slf4j(topic = "c.CreateFutureTask")
public class CreateFutureTask {

    public static void main(String[] args) {
        FutureTask<Integer> futureTask = new FutureTask<Integer>(()->{
            log.debug("future task create thread...");
            return 100;
        });

        new Thread(futureTask,"futureTask").start();
        Integer result = null;
        try {
            result = futureTask.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        log.debug("result:" + result);
    }
}
