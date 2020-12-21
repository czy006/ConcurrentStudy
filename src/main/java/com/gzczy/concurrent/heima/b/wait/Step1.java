package com.gzczy.concurrent.heima.b.wait;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @Description Step1
 * 需求描述：小南需要等到有烟才能开始工作，其他人可以立刻开始干活
 * 缺点：很明显要等小南拿到烟，其他人才能开始干活 效率低下
 * @Author chenzhengyu
 * @Date 2020-11-22 21:50
 */
@Slf4j(topic = "c.StepDemo")
public class Step1 {

    static final Object room = new Object();
    static boolean hasCigarette = false;

    public static void main(String[] args) throws Exception {
        new Thread(() -> {
            synchronized (room) {
                log.debug("有烟没？[{}]", hasCigarette);
                if (!hasCigarette) {
                    log.debug("没烟，先歇会！");
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("有烟没？[{}]", hasCigarette);
                if (hasCigarette) {
                    log.debug("可以开始干活了");
                }
            }
        }, "小南").start();
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                synchronized (room) {
                    log.debug("可以开始干活了");
                }
            }, "其它人").start();
        }
        TimeUnit.SECONDS.sleep(1);
        new Thread(() -> {
        // 这里能不能加 synchronized (room)？
            hasCigarette = true;
            log.debug("烟到了噢！");
        }, "送烟的").start();
    }
}
