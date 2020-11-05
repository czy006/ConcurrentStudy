package com.gzczy.concurrent.model.guarded;

import lombok.extern.slf4j.Slf4j;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @Description 多租户版本
 * @Author chenzhengyu
 * @Date 2020-11-05 20:59
 */
@Slf4j(topic = "c.GuardedObjectV3")
public class GuardedObjectV3 {

    private int id;

    public GuardedObjectV3(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    private Object response;

    public Object get(long timeout) {
        synchronized (this) {
            // 开始时间 15:00:00
            long begin = System.currentTimeMillis();
            // 经历的时间
            long passedTime = 0;
            while (response == null) {
                // 这一轮循环应该等待的时间
                long waitTime = timeout - passedTime;
                // 经历的时间超过了最大等待时间时，退出循环
                if (timeout - passedTime <= 0) {
                    break;
                }
                try {
                    this.wait(waitTime); // 虚假唤醒 15:00:01
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 求得经历时间
                passedTime = System.currentTimeMillis() - begin; // 15:00:02 1s
            }
            return response;
        }
    }

    public void complete(Object response) {
        synchronized (this) {
            // 给结果成员变量赋值
            this.response = response;
            this.notifyAll();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 3; i++) {
            new Thread(new People(),"t1").start();
        }
        TimeUnit.SECONDS.sleep(1);
        for (Integer id : MailBox.getIds()) {
            new Thread(new PostMan(id, "内容" + id),"t2").start();
        }
    }


}

@Slf4j(topic = "c.GuardedObjectV3")
class People implements Runnable {

    @Override
    public void run() {
        GuardedObjectV3 guardedObject = MailBox.createGuardedObject();
        log.debug("开始收信 id:{}", guardedObject.getId());
        Object mail = guardedObject.get(5000);
        log.debug("收到信 id:{}, 内容:{}", guardedObject.getId(), mail);
    }
}

@Slf4j(topic = "c.GuardedObjectV3")
class PostMan implements Runnable {

    private int id;
    private String mailStr;

    public PostMan(int id, String mailStr) {
        this.id = id;
        this.mailStr = mailStr;
    }

    @Override
    public void run() {
        GuardedObjectV3 guardedObject = MailBox.getGuardedObject(id);
        log.debug("送信 id:{}, 内容:{}", id, mailStr);
        guardedObject.complete(mailStr);
    }
}

@Slf4j(topic = "c.GuardedObjectV3")
class MailBox {

    private static Map<Integer, GuardedObjectV3> boxes = new Hashtable<>();

    private static int id = 1;

    // 产生唯一 id
    private static synchronized int generateId() {
        return id++;
    }

    public static GuardedObjectV3 getGuardedObject(int id) {
        return boxes.remove(id);
    }

    public static GuardedObjectV3 createGuardedObject() {
        GuardedObjectV3 go = new GuardedObjectV3(generateId());
        boxes.put(go.getId(), go);
        return go;
    }

    public static Set<Integer> getIds() {
        return boxes.keySet();
    }
}

