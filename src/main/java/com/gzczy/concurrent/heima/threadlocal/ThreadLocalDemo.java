package com.gzczy.concurrent.heima.threadlocal;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description 线程隔离Demo（无改造前）
 * @Author chenzhengyu
 * @Date 2020-12-28 16:00
 */
@Slf4j(topic = "c.ThreadLocalDemo")
public class ThreadLocalDemo {

    private String content;

    private String getContent() {
        return content;
    }

    private void setContent(String content) {
        this.content = content;
    }

    public static void main(String[] args) {
        ThreadLocalDemo demo = new ThreadLocalDemo();
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
