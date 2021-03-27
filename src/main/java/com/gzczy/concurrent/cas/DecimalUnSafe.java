package com.gzczy.concurrent.cas;

import java.math.BigDecimal;

/**
 * @Description 不安全的测试
 * @Author chenzhengyu
 * @Date 2020-11-26 14:31
 */
public class DecimalUnSafe implements DecimalAccount{

    public static void main(String[] args) {
        DecimalAccount.demo(new DecimalUnSafe(new BigDecimal("10000")));
    }

    BigDecimal balance;

    public DecimalUnSafe(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public BigDecimal getBalance() {
        return balance;
    }

    @Override
    public void withdraw(BigDecimal amount) {
        BigDecimal balance = this.getBalance();
        this.balance = balance.subtract(amount);
    }
}
