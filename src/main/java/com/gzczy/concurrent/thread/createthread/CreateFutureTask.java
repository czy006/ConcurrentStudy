package com.gzczy.concurrent.thread.createthread;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * {@link FutureTask} 使用案例演示
 */
@Slf4j(topic = "c.CreateFutureTask")
public class CreateFutureTask {

    public static void main(String[] args) {
        FutureTask<Integer> futureTask = new FutureTask<Integer>(()->{
            log.debug("future task create thread...");
            return 100;
        });

        new Thread(futureTask,"futureTask").start();
        //Integer result = m1(futureTask);
        Integer resultDone = m2(futureTask);
        log.debug("result:" + resultDone);
    }

    private static Integer m1(FutureTask<Integer> futureTask) {
        Integer result = null;
        try {
            //只要出现get方法 不管是否计算完成都会阻塞等待完成？问题：什么可以替代阻塞？
            // 工作中一般无法用到 都是用升级的功能更加强大的CompletableFuture 就是get带超时的方法
            result = futureTask.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static Integer m2(FutureTask<Integer> futureTask) {
        Integer temp = null;
        try {
            // 工作中不要阻塞了 但是我们有个思想叫做cas，使用轮询替代阻塞
            while (!futureTask.isDone()){
                temp = futureTask.get();
                break;
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return temp;
    }
}
