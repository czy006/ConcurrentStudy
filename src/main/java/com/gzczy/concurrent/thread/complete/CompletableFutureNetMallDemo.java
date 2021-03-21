package com.gzczy.concurrent.thread.complete;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Description 模拟电商多平台比价案例
 * @Author chenzhengyu
 * @Date 2021-03-21 10:19
 */
public class CompletableFutureNetMallDemo {

    static List<NetMall> list = Arrays.asList(
            new NetMall("jd"),
            new NetMall("pdd"),
            new NetMall("tb"));

    /**
     * Step by Step 一步步执行
     * @param netMalls
     * @param productName
     * @return
     */
    public static List<String> getPriceByStep(List<NetMall> netMalls, String productName) {
        return netMalls.stream()
                .map(f -> String.format(productName + "in %s price is %.2f",
                        f.getMallName(),
                        f.getPrice(productName)))
                .collect(Collectors.toList());
    }

    /**
     * 同时异步多线程并发 查询后通过Join 进行等待后汇总
     * @param netMalls
     * @param productName
     * @return
     */
    public static List<String> getPriceByAsync(List<NetMall> netMalls, String productName) {
        return netMalls.stream().map(f -> CompletableFuture.supplyAsync(()->
            String.format(productName + "in %s price is %.2f",
                    f.getMallName(),
                    f.getPrice(productName)
        ))).collect(Collectors.toList()).stream().map(CompletableFuture::join).collect(Collectors.toList());
    }

    public static void main(String[] args) {
        getPriceByStep(list,"TCP/IP 第三卷");
        //实际生产使用 getPriceByAsync
        getPriceByAsync(list,"深入理解计算机");
    }
}

@AllArgsConstructor
class NetMall {

    @Getter
    @Setter
    private String mallName;

    public double getPrice(String name) {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ThreadLocalRandom.current().nextDouble() * 2 + name.charAt(0);
    }
}
