package com.gzczy.concurrent.thread.compare;

/**
 * 测试结论
 * DEMO1:yield会让出系统资源时间片给其他资源使用
 * DEMO2:线程优先度越高，获取竞争资源越多（仅供参考，本质还是系统资源调度器起的作用）
 */
public class ThreadCompareYield {

    public static void main(String[] args) {
        ThreadCompareYield t1 = new ThreadCompareYield();
        //只能执行其中1个 请备注 请自行结束运行
        //t1.demo1();
        t1.demo2();
    }

    /**
     * 通过观察demo1 我们可以发现 task1 输出数字结果明显多于task2
     */
    public void demo1() {
        Runnable task1 = new Runnable() {
            int count = 0;

            @Override
            public void run() {
                while (true) {
                    System.out.println("countTask1====>" + count);
                    count++;
                }
            }
        };

        Runnable task2 = new Runnable() {
            int count = 0;

            @Override
            public void run() {
                while (true) {
                    //让出时间片
                    Thread.yield();
                    System.out.println("countTask2====>" + count);
                    count++;
                }
            }
        };

        Thread t1 = new Thread(task1, "task1");
        Thread t2 = new Thread(task2, "task2");
        t1.start();
        t2.start();
    }

    /**
     * 运行一阵子 我们观察到 t1线程task1 由于优先级别较高，获取的竞争资源更多
     * 所以 统计出来的结果会更大
     */
    public void demo2() {
        Runnable task1 = new Runnable() {
            int count = 0;

            @Override
            public void run() {
                while (true) {
                    System.out.println("countTask1====>" + count);
                    count++;
                }
            }
        };

        Runnable task2 = new Runnable() {
            int count = 0;

            @Override
            public void run() {
                while (true) {
                    System.out.println("countTask2====>" + count);
                    count++;
                }
            }
        };

        Thread t1 = new Thread(task1, "task1");
        Thread t2 = new Thread(task2, "task2");
        //设置线程优先度
        t1.setPriority(Thread.MAX_PRIORITY);
        t2.setPriority(Thread.MIN_PRIORITY);
        t1.start();
        t2.start();
    }

}
