package com.gzczy.concurrent.sync.wait;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @Description Step5
 * 这里演示外卖送到 wait就终止往下走，但是烟没送到一直等待中
 *
 * 缺点：
 * 用 notifyAll 仅解决某个线程的唤醒问题，但使用 if + wait 判断仅有一次机会
 * 一旦条件不成立，就没有重新判断的机会了
 * 解决方法：用 while + wait，当条件不成立，再次 wait
 *
 * @Author chenzhengyu
 * @Date 2020-11-22 21:50
 */
@Slf4j(topic = "c.StepDemo")
public class Step5 {

    static final Object room = new Object();
    static boolean hasCigarette = false;
    static boolean hasTakeout = false;

    public static void main(String[] args) throws Exception {

        new Thread(() -> {
            synchronized (room) {
                log.debug("有烟没？[{}]", hasCigarette);
                //真正解决虚假唤醒的问题
                while (!hasCigarette) {
                    log.debug("没烟，先歇会！");
                    try {
                        //会一直停住，不会像Step4那样
                        room.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("有烟没？[{}]", hasCigarette);
                if (hasCigarette) {
                    log.debug("可以开始干活了");
                } else {
                    log.debug("没干成活...");
                }
            }
        }, "小南").start();

        //送外卖的
        new Thread(() -> {
            synchronized (room) {
                log.debug("外卖送到没？[{}]", hasTakeout);
                while (!hasTakeout) {
                    log.debug("没外卖，先歇会！");
                    try {
                        room.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("外卖送到没？[{}]", hasTakeout);
                if (hasTakeout) {
                    log.debug("可以开始干活了");
                } else {
                    log.debug("没干成活...");
                }
            }
        }, "小女").start();


        TimeUnit.SECONDS.sleep(1);
        new Thread(() -> {
            synchronized (room) {
                hasTakeout = true;
                log.debug("外卖到了噢！");
                //唤醒全部线程
                room.notifyAll();
            }
        }, "送外卖的").start();
    }
}
