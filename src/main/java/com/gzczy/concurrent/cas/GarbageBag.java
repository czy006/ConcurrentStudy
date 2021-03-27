package com.gzczy.concurrent.cas;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicMarkableReference;

/**
 * @Description 垃圾袋，倒垃圾问题
 * @Author chenzhengyu
 * @Date 2020-11-27 20:48
 */
public class GarbageBag {

    String desc;

    public GarbageBag(String desc) {
        this.desc = desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "GarbageBag{" +
                "desc='" + desc + '\'' +
                '}';
    }
}

@Slf4j(topic = "c.TestABAAtomicMarkAbleReference")
class TestAtomicMarkAbleReference{

    public static void main(String[] args) throws Exception {
        GarbageBag bag = new GarbageBag("装满了垃圾");
        AtomicMarkableReference<GarbageBag> atomicBag = new AtomicMarkableReference<>(bag, true);

        log.debug("主线程 start...");
        GarbageBag prev = atomicBag.getReference();
        log.debug(prev.toString());
        //新启动线程 去倒掉垃圾
        new Thread(()->{
            log.debug("打扫卫生的线程 start...");
            bag.setDesc("空垃圾袋");
            while (!atomicBag.compareAndSet(bag, bag, true, false)) {}
            log.debug(bag.toString());
        },"t1").start();

        Thread.sleep(1000);
        log.debug("主线程想换一只新垃圾袋？");
        // 主线程尝试把垃圾倒掉 更换垃圾袋
        boolean success = atomicBag.compareAndSet(prev, new GarbageBag("空垃圾袋"), true, false);
        log.debug("换了么？" + success);
        log.debug(atomicBag.getReference().toString());
    }
}