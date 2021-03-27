package com.gzczy.concurrent.sync.biased;

import lombok.extern.slf4j.Slf4j;
import org.openjdk.jol.info.ClassLayout;

import java.util.Vector;

/**
 * @Description 批量修改重偏向状态测试
 * @Author chenzhengyu
 * @Date 2020-11-03 20:59
 */

@Slf4j(topic = "c.BatchBiasedTest")
public class BatchBiasedTest {

    public static void main(String[] args) {
        Vector<Dog> vector = new Vector<>();
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 30; i++) {
                Dog d = new Dog();
                vector.add(d);
                synchronized (d) {
                    log.debug(i + "\t" + ClassLayout.parseInstance(d).toPrintable());
                }
            }
            synchronized (vector) {
                vector.notify();
            }
        }, "t1");
        t1.start();

        Thread t2 = new Thread(() -> {
            synchronized (vector) {
                try {
                    vector.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            log.debug("==========================");
            for (int i = 0; i < 30; i++) {
                //当i到19后，后面的都已经批量修改偏向了 后面全部是101 可以仔细观察线程ID
                Dog d = vector.get(i);
                log.debug(i + "\t" + ClassLayout.parseInstance(d).toPrintable());
                synchronized (d) {
                    //线程ID已经不是t1的线程ID了
                    log.debug(i + "\t" + ClassLayout.parseInstance(d).toPrintable());
                }
                log.debug(i + "\t" + ClassLayout.parseInstance(d).toPrintable());
            }
        }, "t2");
        t2.start();

    }
}
