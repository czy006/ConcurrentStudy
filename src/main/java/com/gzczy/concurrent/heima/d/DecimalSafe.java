package com.gzczy.concurrent.heima.d;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @Description 安全的测试 使用安全的引用
 * @Author chenzhengyu
 * @Date 2020-11-26 14:31
 */
public class DecimalSafe implements DecimalAccount {

    AtomicReference<BigDecimal> ref;

    public static void main(String[] args) {
        DecimalAccount.demo(new DecimalSafe(new BigDecimal("10000")));
    }

    public DecimalSafe(BigDecimal balance) {
        ref = new AtomicReference<>(balance);
    }

    @Override
    public BigDecimal getBalance() {
        return ref.get();
    }

    @Override
    public void withdraw(BigDecimal amount) {
        while (true){
            BigDecimal decimal = ref.get();
            BigDecimal next = decimal.subtract(amount);
            if (ref.compareAndSet(decimal,next)) {
                break;
            }
        }
    }
}
