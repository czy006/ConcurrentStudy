package com.gzczy.concurrent.heima.d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @Description 原子数组测试
 * @Author chenzhengyu
 * @Date 2020-11-27 21:11
 */
class AtomicIntegerArrayTest {

    public static void main(String[] args) throws Exception {
        //不安全的数组
        demo(()->new int[10],
                (array)->array.length,
                (array, index) -> array[index]++,
                array-> System.out.println(Arrays.toString(array)));
        TimeUnit.SECONDS.sleep(1);
        System.out.println("safe...");
        //安全的原子数组
        demo(() -> new AtomicIntegerArray(10),
                array -> array.length(),
                (array, index) -> array.getAndIncrement(index),
                array -> System.out.println(array));
    }

    private static <T> void demo(
            Supplier<T> arraySupplier,
            Function<T, Integer> lengthFuc,
            BiConsumer<T, Integer> putConsumer,
            Consumer<T> printConsumer
    ) {
        List<Thread> threadList = new ArrayList<>();
        //获取传入的数组
        T array = arraySupplier.get();
        //获取数组长度（通过函数调用的方法）
        int length = lengthFuc.apply(array);
        for (int i = 0; i < length; i++) {
            // 每个线程对数组作 10000 次操作
            threadList.add(new Thread(() -> {
                for (int j = 0; j < 10000; j++) {
                    //对2个泛型数据放入消费 第一个是数组，第二个是
                    putConsumer.accept(array, j % length);
                }
            }));
        }
        threadList.forEach(t -> t.start()); // 启动所有线程
        // 等所有线程结束
        threadList.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        //结果返回
        printConsumer.accept(array);
    }
}
