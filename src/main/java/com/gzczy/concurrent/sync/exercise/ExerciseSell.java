package com.gzczy.concurrent.sync.exercise;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

/**
 * @Description 卖票练习
 * @Author chenzhengyu
 * @Date 2020-12-21 12:10
 */
@Slf4j(topic = "c.ExerciseSell")
public class ExerciseSell {

    // Random 为线程安全
    static Random random = new Random();

    // 随机 1~5
    public static int random(int amount) {
        return random.nextInt(amount) + 1;
    }

    public static void main(String[] args) throws InterruptedException {
        //初始化 2000张票
        TicketWindow ticketWindow = new TicketWindow(1000);
        List<Thread> threads = new ArrayList<>();
        // 用来存储买出去多少张票（Vector）线程安全 可以使用CAS类替代
        List<Integer> sellCount = new Vector<>();
        for (int i = 0; i < 2000; i++) {
            Thread t = new Thread(() -> {
                int sell = ticketWindow.sell(random(5));
                sellCount.add(sell);
            });
            threads.add(t);
            t.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        // 买出去的票求和
        log.debug("卖出的票:{}", sellCount.stream().mapToInt(c -> c).sum());
        // 剩余票数
        log.debug("剩余的票:{}", ticketWindow.getCount());
    }
}

@Data
class TicketWindow {

    private int count;

    public TicketWindow(int count) {
        this.count = count;
    }

    /**
     * 保护当前对象资源
     * @param amount
     * @return
     */
    public synchronized int sell(int amount) {
        if (this.count >= amount) {
            this.count -= amount;
            return amount;
        } else {
            return 0;
        }
    }
}
