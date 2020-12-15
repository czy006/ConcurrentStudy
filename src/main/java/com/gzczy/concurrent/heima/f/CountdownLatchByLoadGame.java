package com.gzczy.concurrent.heima.f;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Description 模拟王者荣耀加载
 * @Author chenzhengyu
 * @Date 2020-12-15 20:24
 */
@Slf4j(topic = "c.CountdownLatchByLoadGame")
public class CountdownLatchByLoadGame {

    /**
     * 游戏人数
     */
    private final static int gameCount = 10;

    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(gameCount);
        CountDownLatch latch = new CountDownLatch(gameCount);
        log.info("等待游戏加载中....");
        String[] per = new String[gameCount];
        for (int i = 0; i <= gameCount; i++) {
            int temp = i;
            pool.submit(new Runnable() {
                @SneakyThrows
                @Override
                public void run() {
                    Random r = new Random();
                    for (int j = 0; j <= 100; j++) {
                        Thread.sleep(r.nextInt(150));
                        per[temp] = j + "%";
                        System.out.print("\r" + Arrays.toString(per));
                    }
                    latch.countDown();
                }
            }, "t" + i);
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println();
        log.info("进度加载完毕，进入游戏！");
        pool.shutdown();
    }
}
