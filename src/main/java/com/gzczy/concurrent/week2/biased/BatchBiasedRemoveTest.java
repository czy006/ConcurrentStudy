package com.gzczy.concurrent.week2.biased;

import lombok.extern.slf4j.Slf4j;
import org.openjdk.jol.info.ClassLayout;

import java.util.Vector;
import java.util.concurrent.locks.LockSupport;

/**
 * @Description 批量偏向锁撤销测试
 * -XX:BiasedLockingStartupDelay=0
 * @Author chenzhengyu
 * @Date 2020-11-03 22:25
 */
@Slf4j(topic = "c.BatchBiasedRemoveTest")
public class BatchBiasedRemoveTest {

    static Thread t1, t2, t3;

    public static void main(String[] args) throws InterruptedException {

        Vector<Dog> list = new Vector<>();
        //定义39次 为了演示第40次时候不再进行偏向
        int lookNumber = 39;
        t1 = new Thread(() -> {
            for (int i = 0; i < lookNumber; i++) {
                Dog d = new Dog();
                list.add(d);
                synchronized (d) {
                    log.debug("加锁：" + i + "\t" + ClassLayout.parseInstance(d).toPrintable());
                }
            }
            LockSupport.unpark(t2);
        }, "t1");
        t1.start();

        t2 = new Thread(() -> {
            LockSupport.park();
            log.debug("==================================> t2 start...");
            for (int i = 0; i < lookNumber; i++) {
                Dog d = list.get(i);
                synchronized (d) {
                    log.debug("加锁：" + i + "\t" + ClassLayout.parseInstance(d).toPrintable());
                }
            }
            LockSupport.unpark(t3);
        }, "t2");
        t2.start();

        t3 = new Thread(() -> {
            //t2到t3 需要先撤销才能偏向 从19开始再次执行撤销操作
            LockSupport.park();
            log.debug("==================================> t3 start...");
            for (int i = 0; i < lookNumber; i++) {
                Dog d = list.get(i);
                synchronized (d) {
                    log.debug("加锁：" + i + "\t" + ClassLayout.parseInstance(d).toPrintable());
                }
                log.debug("解锁：" + i + "\t" + ClassLayout.parseInstance(d).toPrintable());
            }
        }, "t3");
        t3.start();

        t3.join();
        //到达预值40次 整个类不可偏向了 不再是101 而是001了
        log.debug("第四十个对象："+ClassLayout.parseInstance(new Dog()).toPrintable());
    }
}
