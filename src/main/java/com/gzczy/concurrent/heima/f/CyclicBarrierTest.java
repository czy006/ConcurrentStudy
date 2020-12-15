package com.gzczy.concurrent.heima.f;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * @Description CyclicBarrier
 * @Author chenzhengyu
 * CyclicBarrier还提供一个更高级的构造函数
 * CyclicBarrier(int parties, Runnable barrierAction)，用于在线程到达屏障时，
 * 优先执行barrierAction，方便处理更复杂的业务场景。
 * @Date 2020-12-15 21:59
 */
@Slf4j(topic = "c.CyclicBarrierTest")
public class CyclicBarrierTest {

    public static void main(String[] args) throws BrokenBarrierException, InterruptedException {
        //定义count数量要和线程数量一致在这个demo中,主线程和子线程会永远等待，
        // 因为没有第三个线程执行await方法，即没有第三个线程到达屏障，所以之前到达屏障的两个线程都不会继续执行
        CyclicBarrier cb = new CyclicBarrier(2, new Runnable() {
            @Override
            public void run() {
                log.info("task is finish by this time！");
            }
        });

        for (int i = 0; i <= 3; i++) {
            new Thread(()->{
                log.info("线程1开始..");
                try {
                    cb.await(); // 当个数不足时，等待
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
                log.info("线程1继续向下运行...");
            },"t1").start();
            new Thread(()->{
                log.info("线程2开始..");
                try { Thread.sleep(2000); } catch (InterruptedException e) { }
                try {
                    cb.await(); // 2 秒后，线程个数够2，继续运行
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
                log.info("线程2继续向下运行...");
            },"t2").start();
            cb.reset();
        }
    }
}
