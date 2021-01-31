package com.gzczy.concurrent.heima.c;


import static java.lang.Thread.sleep;

/**
 * @Description 无法退出的循环
 * @Author chenzhengyu
 * @Date 2020-11-17 18:33
 */
public class Test1 {

    //使用volatile 进行修饰后线程可见
    static boolean run = true;

    public static void main(String[] args) throws Exception {
        Thread t = new Thread(()->{
            while(run){
                System.out.println(run);
            }
        });
        t.start();
        sleep(1000);
        System.out.println("Thread Stopping...");
        run = false; // 线程t不会如预想的停下来
    }
}
