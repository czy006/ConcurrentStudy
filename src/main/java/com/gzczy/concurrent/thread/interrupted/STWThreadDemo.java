package com.gzczy.concurrent.thread.interrupted;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Description 停止线程三种方法
 * @Author chenzhengyu
 * @Date 2021-03-21 15:22
 */
public class STWThreadDemo {

    private static volatile boolean flag = true;

    private static AtomicBoolean atomicBoolean = new AtomicBoolean(true);

    public static void main(String[] args) throws Exception{
        Thread t1 = new Thread(() -> {
            //demo1();
            //demo2();
            demo3();
        },"t1");
        t1.start();
        TimeUnit.SECONDS.sleep(5);
        new Thread(()->{
            //flag = false;
            //atomicBoolean.compareAndSet(true,false);
            t1.interrupt();
        }).start();
    }

    /**
     * 第一种：使用volatile关键字进行停止
     */

    public static void demo1(){
        while (flag){
            System.out.println("正在运行");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 第二种：通过原子类
     */
    public static void demo2(){
        while (atomicBoolean.get()){
            System.out.println("正在运行");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 第三种：通过interrupt方法进行打断
     */
    public static void demo3(){
        while (!Thread.currentThread().isInterrupted()){
            System.out.println("正在运行");
        }
    }
}
