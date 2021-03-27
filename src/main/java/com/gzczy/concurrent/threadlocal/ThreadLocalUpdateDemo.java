package com.gzczy.concurrent.threadlocal;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description 线程隔离Demo（改造后）
 * @Author chenzhengyu
 * @Date 2020-12-28 16:00
 */
@Slf4j(topic = "c.ThreadLocalDemo")
public class ThreadLocalUpdateDemo {

    private String getContent() {
        return local.get();
    }

    private void setContent(String content) {
        local.set(content);
    }

    static final ThreadLocal<String> local = new ThreadLocal<>();

    public static void main(String[] args) {
        ThreadLocalUpdateDemo demo = new ThreadLocalUpdateDemo();
        for (int i = 0; i < 5; i++){
            Thread t1 = new Thread(()->{
                demo.setContent("线程："+Thread.currentThread().getName() + "的数据");
                System.out.println("-----------------");
                System.out.println(Thread.currentThread().getName() + "--->" + demo.getContent());
            },"t"+i);
            t1.start();
        }
    }
}
