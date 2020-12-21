package com.gzczy.concurrent.heima.b.wait;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @Description Step2
 * 需求描述：小南需要等到有烟才能开始工作，其他人可以立刻开始干活
 * 缺点：解决了其他人需要等待才能开始干活效率低下问题，但是这样如果有其它线程也在等待条件呢？
 * Step3 我们加多一个小女送外买的
 * @Author chenzhengyu
 * @Date 2020-11-22 21:50
 */
@Slf4j(topic = "c.StepDemo")
public class Step2 {

    static final Object room = new Object();
    static boolean hasCigarette = false;

    public static void main(String[] args) throws Exception {
        new Thread(() -> {
            synchronized (room) {
                log.debug("有烟没？[{}]", hasCigarette);
                if (!hasCigarette) {
                    log.debug("没烟，先歇会！");
                    try {
                        room.wait(2000);
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
            synchronized (room) {
                hasCigarette = true;
                log.debug("烟到了噢！");
                room.notify();
            }
        }, "送烟的").start();
    }
}
