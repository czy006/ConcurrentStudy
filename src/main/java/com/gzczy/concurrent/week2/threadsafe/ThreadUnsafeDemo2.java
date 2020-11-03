package com.gzczy.concurrent.week2.threadsafe;

import java.util.ArrayList;

/**
 * @Description 线程不安全
 * 多线程操作add remove 有可能导致数组下标异常无法移除
 * @Author chenzhengyu
 * @Date 2020-10-28 19:53
 */
public class ThreadUnsafeDemo2 {

    static final int THREAD_NUMBER = 2;
    static final int LOOP_NUMBER = 200;

    public static void main(String[] args) {

        ThreadUnsafeDemo2 test = new ThreadUnsafeDemo2();
        for (int i = 0; i < THREAD_NUMBER; i++) {
            new Thread(() -> {
                test.method1(LOOP_NUMBER);
            }, "Thread" + i).start();
        }

    }

    ArrayList<String> list = new ArrayList<>();

    public void method1(int loopNumber) {
        for (int i = 0; i < loopNumber; i++) {
    // { 临界区, 会产生竞态条件
            method2();
            method3();
    // } 临界区
        }
    }

    private void method2() {
        list.add("1");
    }

    private void method3() {
        list.remove(0);
    }


}
