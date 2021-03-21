package com.gzczy.concurrent.thread.interrupted;

import java.util.concurrent.TimeUnit;

/**
 * @Description 线程中断demo
 * @Author chenzhengyu
 * @Date 2021-03-21 15:49
 */
public class InterruptErrorDemo {

    public static void main(String[] args) throws Exception{
        Thread t1 = new Thread(() -> {
            CanNotStop();
        }, "t1");
        t1.start();
        TimeUnit.SECONDS.sleep(3);
        Thread t2 = new Thread(()->{
            t1.interrupt();
        },"t2");
        t2.start();
    }

    /**
     * 抛出异常后仍然 无法正常打断（原因请点击进入API查看，sleep wait join方法打断会清除打断标识）
     * 在异常部分需要再次打断 重置标志位
     */
    public static void CanNotStop(){
        while (!Thread.currentThread().isInterrupted()) {
            System.out.println("Running.....");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                //我们在刚刚catch代码块再次打断，复位标志位，使其能够正确的中断线程
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }
    }
}
