package com.gzczy.concurrent.heima.e;

import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.Random;
import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * @Description 模拟数据库多线程连接池(演示享元模式)
 * @Author chenzhengyu
 * @Date 2020-12-01 12:59
 */
@Slf4j(topic = "c.pool")
public class Pool {

    //测试主方法
    public static void main(String[] args) {
        Pool pool = new Pool(2);
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                Connection conn = pool.borrow();
                try {
                    Thread.sleep(new Random().nextInt(1000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                pool.returnConnection(conn);
            }).start();
        }
    }

    //连接池大小
    private int poolSize;

    //线程数组
    private Connection connections[];

    // 连接状态数组 0 表示空闲， 1 表示繁忙
    private AtomicIntegerArray states;

    //构造方法初始化
    public Pool(int poolSize) {
        this.poolSize = poolSize;
        connections = new Connection[poolSize];
        states = new AtomicIntegerArray(poolSize);
        for (int i = 0; i < poolSize; i++) {
            connections[i] = new MockConnection("ConnectionName - " + i);
        }
    }


    public Connection borrow() {
        while (true) {
            for (int i = 0; i < poolSize; i++) {
                if (states.get(i) == 0) {
                    if (states.compareAndSet(i, 0, 1)) {
                        log.debug("borrow {}", connections[i]);
                        return connections[i];
                    }
                }
            }
            // 如果没有空闲连接，当前线程进入等待
            synchronized (this) {
                try {
                    log.debug("wait...");
                    this.wait(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void returnConnection(Connection con) {
        for (int i = 0; i < poolSize; i++) {
            if (connections[i] == con) {
                states.set(i,0);
                //归还后立刻唤醒正在等待的线程
                synchronized (this) {
                    log.debug("free {}", con);
                    this.notifyAll();
                }
                break;
            }
        }
    }
}
