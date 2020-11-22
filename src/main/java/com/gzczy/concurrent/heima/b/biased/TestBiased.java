package com.gzczy.concurrent.heima.b.biased;

import lombok.extern.slf4j.Slf4j;
import org.openjdk.jol.info.ClassLayout;

import java.util.concurrent.TimeUnit;

/**
 * @Description 测试偏向锁
 * 参考链接：https://blog.csdn.net/q13145241q/article/details/108127174
 *
 * @Author chenzhengyu
 * @Date 2020-11-02 19:23
 */
@Slf4j(topic = "c.TestBiased")
public class TestBiased {

    public static void main(String[] args) throws Exception {
        printInstanceObjectHeader();
        printInstanceObjectHeadBySyc();
        printInstanceObjectHeadBySycHashCode();
        printInstanceObjectHeadByThread();
    }

    /**
     * 无添加JVM参数验证 偏向锁
     * 仔细观察VALUE 状态码，在开启延迟和不开启延迟的情况下
     * JVM参数： -XX:+UseBiasedLocking  默认开启偏向锁
     * -XX:BiasedLockingStartupDelay=0  偏向延迟，默认为4秒。偏向延迟设置之后
     * 任何New出来的对象时的对象头的锁信息仍然是都是偏向锁
     * @throws InterruptedException
     */
    public static void printInstanceObjectHeader() throws InterruptedException {
        log.debug("printInstanceObjectHeader...");
        log.debug(ClassLayout.parseInstance(new Dog()).toPrintable());
        log.debug("Thread Sleeping...");
        TimeUnit.SECONDS.sleep(6);
        log.debug(ClassLayout.parseInstance(new Dog()).toPrintable());
    }

    /**
     * 测试加偏量锁后的MarkHead变化
     * 需要打开JVM参数 -XX:BiasedLockingStartupDelay=0 使得偏向立刻生效
     */
    public static void printInstanceObjectHeadBySyc() {
        log.debug("printInstanceObjectHeadBySyc...");
        Dog dog = new Dog();
        //加锁前： 对象头末端显示101 证明启用了偏向锁，剩下54位是线程ID
        log.debug(ClassLayout.parseInstance(dog).toPrintable());
        synchronized (dog) {
            log.debug(ClassLayout.parseInstance(dog).toPrintable());
        }
        //线程ID 直接偏向此线程，处于偏向锁对象解锁后，线程ID仍热存储在对象头当中
        log.debug(ClassLayout.parseInstance(dog).toPrintable());
    }

    /**
     * 测试加偏量锁后并且调用Hashcode的MarkHead变化
     * 需要打开JVM参数 -XX:BiasedLockingStartupDelay=0 使得偏向立刻生效
     * 因为加锁进程是Main，没有产生锁的竞争，所以对象头信息仍然是偏向锁
     */
    public static void printInstanceObjectHeadBySycHashCode() {
        log.debug("printInstanceObjectHeadBySycHashCode...");
        Dog dog = new Dog();
        //调用HashCode会禁用这个对象的偏向锁，观察对象头已经给填充
        log.debug(dog.toString());
        log.debug(ClassLayout.parseInstance(dog).toPrintable());
        synchronized (dog) {
            log.debug(ClassLayout.parseInstance(dog).toPrintable());
        }
        //线程ID 直接偏向此线程 线程ID进行了存储
        log.debug(ClassLayout.parseInstance(dog).toPrintable());
    }

    /**
     * 验证线程加锁 2个线程访问的时间错开
     */
    public static void printInstanceObjectHeadByThread(){
        log.debug("printInstanceObjectHeadByThread...");
        Dog dog = new Dog();

        Thread t1 = new Thread(() -> {
            log.debug(ClassLayout.parseInstance(dog).toPrintable());
            synchronized (dog) {
                log.debug("线程ID添加至对象头..."+ClassLayout.parseInstance(dog).toPrintable());
            }
            log.debug("解锁，线程ID保存在对象头中..."+ClassLayout.parseInstance(dog).toPrintable());
            synchronized (TestBiased.class){
                //唤醒
                TestBiased.class.notify();
            }
        }, "t1");

        Thread t2 = new Thread(() -> {
            //t2一上来并没有往下运行，而是先等待。结束等待是等t1结束后再通知t2线程
            synchronized (TestBiased.class){
                try {
                    TestBiased.class.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            log.debug("t2线程中，偏向锁失效，进入轻量级锁的状态..."+ClassLayout.parseInstance(dog).toPrintable());
            synchronized (dog) {
                log.debug(ClassLayout.parseInstance(dog).toPrintable());
            }
            log.debug(ClassLayout.parseInstance(dog).toPrintable());
        }, "t2");

        t1.start();
        t2.start();
    }
}

class Dog {
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Dog-");
        sb.append("HashCode:").append(this.hashCode());
        return sb.toString();
    }
}
