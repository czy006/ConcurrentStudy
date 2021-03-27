package com.gzczy.concurrent.thread.interrupted;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description 对比打断方法中间流程
 * @Author chenzhengyu
 * @Date 2021-03-21 16:14
 */
@Slf4j(topic = "c.InterruptCompare")
public class InterruptCompare {

    public static void main(String[] args) {
        log.info("==>"+Thread.interrupted());
        log.info("==>"+Thread.interrupted());
        log.info("========================");
        Thread.currentThread().interrupt();
        log.info("========================");
        //通过观察结果 我们可以看到这里显示为true 因为刚刚打断了，与此同时会将打断状态清空，下次判断为false
        log.info("==>"+Thread.interrupted());
        // 此方法并不会重置标识位置，只是单纯的显示一下当前状态
        log.info("==>"+Thread.currentThread().isInterrupted());
        log.info("==>"+Thread.interrupted());
    }
}
