package com.gzczy.concurrent.heima.d;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description 原子类 多线程取款
 * @Author chenzhengyu
 * @Date 2020-11-24 10:01
 */
public interface Account {

    // 获取余额
    Integer getBalance();

    // 取款
    void withdraw(Integer amount);

    /**
     * 方法内会启动 1000 个线程，每个线程做 -10 元 的操作
     * 如果初始余额为 10000 那么正确的结果应当是 0
     */
    static void demo(Account account) {
        List<Thread> ts = new ArrayList<>();
        long start = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            ts.add(new Thread(() -> {
                account.withdraw(10);
            }));
        }
        ts.forEach(Thread::start);
        ts.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        long end = System.nanoTime();
        System.out.println(account.getBalance()
                + " cost: " + (end - start) / 1000_000 + " ms");
    }

}

class AccountUnsafe implements Account{

    public static void main(String[] args) {
        // unsafe 减少完后我们发现余额不是为0 按照以前的方法 我们只需要在临界区添加synchronized
        Account.demo(new AccountUnsafe(10000));
    }

    private Integer balance;

    public AccountUnsafe(Integer balance) {
        this.balance = balance;
    }

    @Override
    public Integer getBalance() {
        return balance;
    }

    @Override
    public void withdraw(Integer amount) {
        balance -= amount;
    }

}

/**
 * 基于原子类的AQS实现
 */
class AccountSafeAtomic implements Account{

    public static void main(String[] args) {
        Account.demo(new AccountSafeAtomic(10000));
    }

    private AtomicInteger balance;

    public AccountSafeAtomic(Integer balance) {
        this.balance = new AtomicInteger(balance);
    }

    @Override
    public Integer getBalance() {
        return balance.get();
    }

    @Override
    public void withdraw(Integer amount) {
        //没使用API用法
        //while (true){
        //    //获取当前金额
        //    int prev = balance.get();
        //    //当前金额 - amount
        //    int temp = prev -amount;
        //    // 进行对比 返回boolean 值 有可能失败
        //    if (balance.compareAndSet(prev, temp)){
        //        break;
        //    }
        //}

        //使用API做法 Unsafe.class 做法可以看看 实现原理和上面while true一样
        balance.addAndGet(-1 * amount);
    }
}
