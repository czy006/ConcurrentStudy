package com.gzczy.concurrent.model.produce;

import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

/**
 * @Description 消息
 * @Author chenzhengyu
 * @Date 2020-12-22 10:45
 */
public class Message {
    private int id;
    private Object message;

    public Message(int id, Object message) {
        this.id = id;
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public Object getMessage() {
        return message;
    }
}

@Slf4j(topic = "c.MessageQueue")
class MessageQueue {

    private LinkedList<Message> queue;

    private int capacity;

    public MessageQueue(int capacity) {
        queue = new LinkedList<Message>();
        this.capacity = capacity;
    }

    public int getCapacity() {
        return capacity;
    }

    public void put(Message msg) {
        synchronized (queue) {
            log.debug("库存已达上限, wait");
            while (queue.size() == capacity) {
                try {
                    queue.wait();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            queue.addLast(msg);
            queue.notifyAll();
        }
    }

    public Message get() {
        synchronized (queue) {
            log.debug("没货了, wait");
            while (queue.isEmpty()) {
                try {
                    queue.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Message message = queue.removeFirst();
            queue.notifyAll();
            return message;
        }
    }
}

@Slf4j(topic = "c.TestQueue")
class TestQueue {

    public static void main(String[] args) {
        MessageQueue messageQueue = new MessageQueue(2);

        //4个生产者线程
        for (int i = 0; i < 4; i++) {
            int temp = i;
            new Thread(() -> {
                log.debug("download...");
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.debug("try put message({})", temp);
                messageQueue.put(new Message(temp, "response" + temp));
            }, "product" + i).start();
        }

        //1个消费者线程
        new Thread(() -> {
            while (messageQueue.getCapacity() != 0) {
                Message message = messageQueue.get();
                log.debug("take message({}): [{}] lines", message.getId(), message.getMessage());
            }

        }, "consumer").start();
    }
}
