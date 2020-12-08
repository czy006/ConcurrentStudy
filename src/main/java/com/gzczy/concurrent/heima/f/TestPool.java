package com.gzczy.concurrent.heima.f;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description 线程测试类
 * @Author chenzhengyu
 * @Date 2020-12-08 17:48
 */
@Slf4j(topic = "c.ThreadPoolTest")
public class TestPool {

    public static void main(String[] args) {
        ThreadPool threadPool = new ThreadPool(2, 2, 1, TimeUnit.SECONDS,
                /**
                 * 拒绝策略
                 * 1) 死等 queue.put(task);
                 * 2) 带超时等待 queue.offer(task, 1500, TimeUnit.MILLISECONDS);
                 * 3) 让调用者放弃任务执行 log.debug("放弃{}", task);
                 * 4) 让调用者抛出异常 throw new RuntimeException("任务执行失败 " + task);
                 * 5) 让调用者自己执行任务 task.run();
                 */
                (queue, task) -> {
                    log.info("=====执行拒绝策略=====> {}", task);
                    queue.offer(task, 1500, TimeUnit.MILLISECONDS);
                });
        for (int i = 0; i < 5; i++) {
            int j = i;
            threadPool.execute(() -> {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.debug("{}", j);
            });
        }
    }
}

@Slf4j(topic = "c.ThreadPoolTest")
class ThreadPool {

    // 任务队列
    private BlockingQueue<Runnable> taskQueue;

    // 线程集合
    private final HashSet<Worker> workers = new HashSet<>();

    // 核心线程数
    private int coreSize;

    // 获取任务时的超时时间
    private long timeout;

    private TimeUnit timeUnit;

    private RejectPolicy<Runnable> rejectPolicy;

    public ThreadPool(int coreSize, int queueCapacity, long timeout, TimeUnit timeUnit, RejectPolicy<Runnable> rejectPolicy) {
        this.taskQueue = new BlockingQueue<>(queueCapacity);
        this.coreSize = coreSize;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
        this.rejectPolicy = rejectPolicy;
    }

    public void execute(Runnable task) {
        synchronized (workers) {
            //当任务数没有超过 coreSize 时，直接交给 worker 对象执行,如果任务数超过 coreSize 时，加入任务队列暂存
            if (workers.size() < coreSize) {
                Worker worker = new Worker(task);
                log.info("新增 worker{}, {}", worker, task);
                workers.add(worker);
                worker.start();
            } else {
                //taskQueue.put(task);
                taskQueue.tryPut(rejectPolicy,task);
            }
        }
    }

    /**
     * 包装类 用于实现Thread的增强
     */
    class Worker extends Thread {

        private Runnable task;

        public Worker(Runnable task) {
            this.task = task;
        }

        @Override
        public void run() {
            // 执行任务
            // 1) 当 task 不为空，执行任务
            // 2) 当 task 执行完毕，再接着从任务队列获取任务并执行
            //while (task != null || (task = taskQueue.task()) != null) {
            while (task != null || (task = taskQueue.poll(timeout, timeUnit)) != null) {
                try {
                    log.info("正在执行...{}", task);
                    task.run();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    task = null;
                }
            }
            //当无任务时候移除当前工作线程
            synchronized (workers) {
                log.info("worker 被移除{}", this);
                workers.remove(this);
            }
        }
    }
}

/**
 * 实现自定义的拒绝策略
 *
 * @param <T>
 */
@FunctionalInterface
interface RejectPolicy<T> {
    void reject(BlockingQueue<T> queue, T task);
}

@Slf4j(topic = "c.ThreadPoolTest")
class BlockingQueue<E> {

    /**
     * 队列容量大小
     */
    private int capacity;

    /**
     * 任务队列
     */
    private Deque<E> queue = new ArrayDeque<>();

    /**
     * ReentrantLock
     */
    private ReentrantLock lock = new ReentrantLock();

    /**
     * 生产者条件变量
     */
    private Condition fullWaitCondition = lock.newCondition();

    /**
     * 消费者条件变量
     */
    private Condition emptyCondition = lock.newCondition();

    public BlockingQueue(int capacity) {
        this.capacity = capacity;
    }

    /**
     * 带超时的阻塞获取
     *
     * @param time
     * @param timeUnit
     * @return
     */
    public E poll(long time, TimeUnit timeUnit) {
        lock.lock();
        try {
            long nanos = timeUnit.toNanos(time);
            //循环检测队列是否为空，如果为空则通知emptyWaitSet已经为空并且移除第一个返回
            while (queue.isEmpty()) {
                try {
                    //避免虚假唤醒和等待,返回值是剩余时间
                    if (nanos <= 0) {
                        return null;
                    }
                    nanos = emptyCondition.awaitNanos(nanos);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            E e = queue.removeFirst();
            fullWaitCondition.signal();
            log.info("取出任务队列 - > {}", e);
            return e;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 阻塞获取
     *
     * @return
     */
    public E task() {
        lock.lock();
        try {
            //循环检测队列是否为空，如果为空则通知emptyWaitSet已经为空并且移除第一个返回
            while (queue.isEmpty()) {
                try {
                    emptyCondition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            E e = queue.removeFirst();
            fullWaitCondition.signal();
            log.info("取出任务队列 - > {}", e);
            return e;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 放入任务
     *
     * @param task Runnable
     */
    public void put(E task) {
        lock.lock();
        try {
            while (queue.size() == capacity) {
                try {
                    log.info("等待加入任务队列 {} ...", task);
                    fullWaitCondition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            log.info("加入任务队列 - > {}", task);
            queue.addLast(task);
            emptyCondition.signal();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 尝试放入 按照拒绝策略进行执行
     * @param rejectPolicy
     * @param task
     */
    public void tryPut(RejectPolicy<E> rejectPolicy,E task){
        lock.lock();
        try {
            // 判断队列是否满
            if(queue.size() == capacity) {
                rejectPolicy.reject(this, task);
            } else {  // 有空闲
                log.debug("加入任务队列 {}", task);
                queue.addLast(task);
                emptyCondition.signal();
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * 带超时 时间的阻塞
     * @param task
     * @param timeout
     * @param timeUnit
     * @return
     */
    public boolean offer(E task, long timeout, TimeUnit timeUnit) {
        lock.lock();
        try {
            long nanos = timeUnit.toNanos(timeout);
            while (queue.size() == capacity) {
                try {
                    if(nanos <= 0) {
                        return false;
                    }
                    log.debug("等待加入任务队列 {} ...", task);
                    nanos = fullWaitCondition.awaitNanos(nanos);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            log.debug("加入任务队列 {}", task);
            queue.addLast(task);
            emptyCondition.signal();
            return true;
        }finally {
            lock.unlock();
        }
    }

    /**
     * 获取当前队列大小
     *
     * @return
     */
    public int size() {
        lock.lock();
        try {
            return queue.size();
        } finally {
            lock.unlock();
        }
    }

}

