package com.gzczy.concurrent.cas;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * @Description 原子对象字段更新器
 * @Author chenzhengyu
 * @Date 2020-11-27 21:59
 */
public class AtomicIntegerFieldUpdaterTest {

    private volatile int field;

    public static void main(String[] args) {
        AtomicIntegerFieldUpdater<AtomicIntegerFieldUpdaterTest> field =
        AtomicIntegerFieldUpdater.newUpdater(AtomicIntegerFieldUpdaterTest.class, "field");
        AtomicIntegerFieldUpdaterTest a1 = new AtomicIntegerFieldUpdaterTest();
        field.compareAndSet(a1, 0,10);
        // 修改成功 field = 10
        System.out.println(a1.field);
        // 修改成功 field = 20
        field.compareAndSet(a1, 10, 20);
        System.out.println(a1.field);
        // 修改失败 field = 20
        boolean res = field.compareAndSet(a1, 10, 30);
        System.out.println(res);
        System.out.println(a1.field);
    }
}
