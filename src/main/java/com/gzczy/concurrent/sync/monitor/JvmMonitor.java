package com.gzczy.concurrent.sync.monitor;

/**
 * @Description 观察关键字不同的使用方式-编译后的字节码
 * 字节码编译  javap -verbose JvmMonitor.class
 * @Author chenzhengyu
 * @Date 2021-03-18 13:12
 */
public class JvmMonitor {

    final Object obj = new Object();

    public static void main(String[] args) {
        JvmMonitor jvm = new JvmMonitor();
        jvm.m1();
        jvm.m2();
        jvm.m3();
        jvm.m4();
    }

    public void m1(){
        synchronized (obj){
            System.out.println("=====Test sync by throw ======");
        }
    }

    public void m2(){
        synchronized (obj){
            System.out.println("=====Test sync by throw ======");
            throw new RuntimeException("抛出异常！！！！");
        }
    }

    public synchronized void m3(){
        System.out.println("=====Test sync method======");
    }

    public static synchronized void m4(){
        System.out.println("=====Test static syn method ======");
    }
}
