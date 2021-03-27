package com.gzczy.concurrent.cas;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * @Description ABA问题测试
 * @Author chenzhengyu
 * @Date 2020-11-27 20:11
 */
@Slf4j(topic = "c.ABATest")
public class ABATest {

    static AtomicReference<String> ref = new AtomicReference<>("A");

    public static void main(String[] args) throws Exception {
        log.debug("main start...");
        String prev = ref.get();
        other();
        TimeUnit.SECONDS.sleep(1);
        // 尝试改为 C
        log.debug("change A->C {}", ref.compareAndSet(prev, "C"));
    }

    public static void other() throws Exception{
        new Thread(() -> {
            log.debug("change A->B {}", ref.compareAndSet(ref.get(), "B"));
        }, "t1").start();

        TimeUnit.MILLISECONDS.sleep(500);

        new Thread(() -> {
            log.debug("change B->A {}", ref.compareAndSet(ref.get(), "A"));
        }, "t2").start();
    }
}

/**
 * AtomicStampedReference 解决ABA问题,通过类似Git的版本号进行解决
 */
@Slf4j(topic = "c.ABATest")
class AtomicStampedReferenceTest{

    static AtomicStampedReference<String> ref = new AtomicStampedReference<>("A", 0);

    public static void main(String[] args) throws Exception {
        log.debug("main start...");
        String prev = ref.getReference();
        other();
        TimeUnit.SECONDS.sleep(1);
        // 尝试改为 C，对比版本号是否为0 如果是则更改并且改为1
        log.debug("change A->C {}", ref.compareAndSet(prev, "C",0,1));
    }

    public static void other() throws Exception{
        new Thread(() -> {
            log.debug("change A->B {}", ref.compareAndSet(ref.getReference(), "B",0,1));
        }, "t1").start();

        TimeUnit.MILLISECONDS.sleep(500);

        new Thread(() -> {
            log.debug("change B->A {}", ref.compareAndSet(ref.getReference(), "A",1,2));
        }, "t2").start();
    }
}