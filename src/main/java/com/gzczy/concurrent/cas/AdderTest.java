package com.gzczy.concurrent.cas;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @Description 原子自增性能比较
 * @Author chenzhengyu
 * @Date 2020-11-28 09:53
 */
@Slf4j(topic = "c.AdderTest")
public class AdderTest {

    public static void main(String[] args) {
        //普通自增
        for (int i=0;i<5;i++){
            demo(()->new AtomicLong(), adder->adder.getAndIncrement(),i,"AtomicLong");
        }
        for (int i=0;i<5;i++){
            demo(()->new AtomicInteger(), adder->adder.getAndIncrement(),i,"AtomicInteger");
        }
        //JDK1.8后提供的新自增类 Doug Lea 并发编程大师的类
        for (int i=0;i<5;i++){
            demo(()->new LongAdder(),adder->adder.increment(),i,"LongAdder");
        }
    }

    public static <T> void demo(Supplier<T> supplier, Consumer<T> action,int num,String threadName){
        T adder = supplier.get();
        long start = System.nanoTime();
        List<Thread> threadList = new ArrayList<>();
        for (int i = 0; i < 40; i++) {
            threadList.add(new Thread(() -> {
                for (int j = 0; j < 500000; j++) {
                    action.accept(adder);
                }
            }));
        }
        threadList.forEach(Thread::start);
        threadList.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        long end = System.nanoTime();
        log.debug("Running Name: {} , num: {} , cost Time: {}",threadName,num,(end - start)/1000_000);
    }
}
