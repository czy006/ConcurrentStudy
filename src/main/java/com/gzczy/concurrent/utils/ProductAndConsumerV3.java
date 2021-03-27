package com.gzczy.concurrent.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 生产者消费者  阻塞队列版 3.0
 * 使用：volatile、CAS、atomicInteger、BlockQueue、线程交互、原子引用
 * @Author chenzhengyu
 * @Date 2021-02-16 09:06
 */
@Slf4j(topic = "c.Resource")
public class ProductAndConsumerV3 {

    // 默认开启，进行生产消费
    // 这里用到了volatile是为了保持数据的可见性，也就是当TLAG修改时，要马上通知其它线程进行修改
    private volatile boolean FLAG = true;

    // 使用原子包装类，而不用number++
    private AtomicInteger adder = new AtomicInteger();

    // 这里不能为了满足条件，而实例化一个具体的SynchronousBlockingQueue
    BlockingQueue<String> queue = null;

    //依赖注入 构造方法传入
    public ProductAndConsumerV3(BlockingQueue<String> queue) {
        this.queue = queue;
        log.info("Loading in ..." + queue.getClass().getName());
    }

    /**
     * 生产者
     *
     * @throws Exception
     */
    public void producer() throws Exception {
        String data = null;
        boolean isAdd;
        // 多线程环境的判断，一定要使用while进行，防止出现虚假唤醒
        // 当FLAG为true的时候，开始生产
        while (FLAG) {
            data = adder.incrementAndGet() +"";
            isAdd = queue.offer(data, 2, TimeUnit.SECONDS);
            if (isAdd) {
                log.info("插入成功");
            } else {
                log.info("插入失败");
            }

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        log.info("生产者退出");
    }

    /**
     * 消费
     *
     * @throws Exception
     */
    public void consumer() throws Exception {
        String retValue;
        // 多线程环境的判断，一定要使用while进行，防止出现虚假唤醒
        // 当FLAG为true的时候，开始生产
        while (FLAG) {
            // 2秒存入1个data
            retValue = queue.poll(2L, TimeUnit.SECONDS);
            if (retValue != null && !"".equals(retValue)) {
                log.info("消费队列:" + retValue + "成功");
            } else {
                FLAG = false;
                log.info("消费失败，队列中已为空，退出");
                // 退出消费队列
                return;
            }
        }
    }

    public void stop() {
        this.FLAG = false;
    }

    public static void main(String[] args) {
        // 传入具体的实现类， ArrayBlockingQueue
        ProductAndConsumerV3 productAndConsumerV3 = new ProductAndConsumerV3(new ArrayBlockingQueue<String>(10));

        new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + "\t 生产线程启动");
            System.out.println("");
            System.out.println("");
            try {
                productAndConsumerV3.producer();
                System.out.println("");
                System.out.println("");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "prod").start();


        new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + "\t 消费线程启动");

            try {
                productAndConsumerV3.consumer();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "consumer").start();

        // 5秒后，停止生产和消费
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("");
        System.out.println("");
        System.out.println("5秒中后，生产和消费线程停止，线程结束");
        productAndConsumerV3.stop();
    }
}
