package com.gzczy.concurrent.heima.d;

import sun.misc.Unsafe;

/**
 * @Description 实现原子类测试
 * @Author chenzhengyu
 * @Date 2020-11-28 19:25
 */
public class AtomicData implements Account {

    private volatile int data;
    static final Unsafe unsafe;
    static final long DATA_OFFSET;

    static {
        unsafe = UnsafeAccessor.getUnsafe();
        try {
            // data 属性在 DataContainer 对象中的偏移量，用于 Unsafe 直接访问该属性
            DATA_OFFSET = unsafe.objectFieldOffset(AtomicData.class.getDeclaredField("data"));
        } catch (NoSuchFieldException e) {
            throw new Error(e);
        }
    }

    public AtomicData(int data) {
        this.data = data;
    }

    public int getData() {
        return data;
    }

    public void decrease(int amount) {
        int oldValue;
        while (true){
            oldValue = data;
            if (unsafe.compareAndSwapInt(this,DATA_OFFSET ,oldValue ,oldValue-amount)){
                return;
            }
        }
    }

    @Override
    public Integer getBalance() {
        return getData();
    }

    @Override
    public void withdraw(Integer amount) {
        decrease(amount);
    }
}

class TestMyAtomicData{

    public static void main(String[] args) {
        Account.demo(new AtomicData(10000));
    }
}
