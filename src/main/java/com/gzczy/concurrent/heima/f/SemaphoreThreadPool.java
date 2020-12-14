package com.gzczy.concurrent.heima.f;

import com.gzczy.concurrent.heima.e.MockConnection;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * @Description Semaphore信号量控制资源数
 * 基于#{@link com.gzczy.concurrent.heima.e.Pool} 进行升级改造 去除wait notifyAll方式
 * @Author chenzhengyu
 * @Date 2020-12-14 20:37
 */
@Slf4j(topic = "c.Semaphore")
public class SemaphoreThreadPool {

    //测试主方法
    public static void main(String[] args) {
        SemaphoreThreadPool pool = new SemaphoreThreadPool(2);
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                Connection conn = pool.borrow();
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                pool.returnConnection(conn);
            },"t"+i).start();
        }
    }

    //连接池大小
    private int poolSize;

    //线程数组
    private Connection connections[];

    // 连接状态数组 0 表示空闲， 1 表示繁忙
    private AtomicIntegerArray states;

    //信号量
    private Semaphore semaphore;

    //构造方法初始化
    public SemaphoreThreadPool(int poolSize) {
        this.poolSize = poolSize;
        //初始化时候把池子大小和资源控制一一对应
        semaphore = new Semaphore(poolSize);
        connections = new Connection[poolSize];
        states = new AtomicIntegerArray(poolSize);
        for (int i = 0; i < poolSize; i++) {
            connections[i] = new MockConnection("ConnectionName - " + i);
        }
    }


    public Connection borrow() {
        //信号量 +1 借走1个线程
        try {
            semaphore.acquire();// 没有许可的线程，在此等待
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < poolSize; i++) {
            if (states.get(i) == 0) {
                //通过CAS获取空闲链接
                if (states.compareAndSet(i, 0, 1)) {
                    log.info("borrow Connection {}", connections[i]);
                    return connections[i];
                }
            }
        }
        //不会走到这一步，这里写返回null只是为了符合语法规范
        return null;
    }

    /**
     * 归还链接
     * @param con
     */
    public void returnConnection(Connection con) {
        for (int i = 0; i < poolSize; i++) {
            if (connections[i] == con) {
                states.set(i, 0);
                //归还后立刻唤醒正在等待的线程
                log.info("return Connection {}",con);
                semaphore.release();
                break;
            }
        }
    }
}
