package com.gzczy.concurrent.heima.b.monitor.activity;

/**
 * @Description 筷子类
 * @Author chenzhengyu
 * @Date 2020-11-15 18:39
 */
public class Chopstick {

    String name;

    public Chopstick(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "筷子{" + name + '}';
    }
}
