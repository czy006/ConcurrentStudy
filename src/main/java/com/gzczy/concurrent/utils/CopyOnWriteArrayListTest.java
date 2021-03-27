package com.gzczy.concurrent.utils;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

/**
 * @Description 数组List复制
 * @Author chenzhengyu
 * @Date 2020-12-17 15:07
 */
public class CopyOnWriteArrayListTest {

    public static void main(String[] args) throws InterruptedException {
        CopyOnWriteArrayList<Integer> list = new CopyOnWriteArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        Iterator<Integer> iter = list.iterator();
        //弱一致性体现
        new Thread(() -> {
            list.remove(0);
            System.out.println(list);
        }).start();
        TimeUnit.SECONDS.sleep(1);
        while (iter.hasNext()) {
            System.out.println(iter.next());
        }
    }
}
