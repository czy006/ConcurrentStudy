package com.gzczy.concurrent.week2.biased;

import lombok.extern.slf4j.Slf4j;
import org.openjdk.jol.info.ClassLayout;

import java.util.concurrent.TimeUnit;

/**
 * @Description 测试偏向锁
 * 参考链接：http://www.360doc.com/content/20/1015/12/835902_940569768.shtml
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
        log.debug(dog.toString());
        //加锁前： 对象头末端显示101 证明启用了偏向锁，剩下54位是线程ID
        log.debug(ClassLayout.parseInstance(dog).toPrintable());
        synchronized (dog) {
            log.debug(ClassLayout.parseInstance(dog).toPrintable());
        }
        //线程ID 直接偏向此线程 线程ID进行了存储
        log.debug(ClassLayout.parseInstance(dog).toPrintable());
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
