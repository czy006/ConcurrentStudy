package com.gzczy.concurrent.threadlocal;

import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @Description 时间安全 类测试
 * @Author chenzhengyu
 * @Date 2020-11-30 22:34
 */
@Slf4j(topic = "c.SimpleDateFormatUnSafeTest")
public class DateTimeSafeTest {

    public static void main(String[] args) {
        demo1();
        demo2();
    }

    /**
     * 线程不安全演示：有很大几率出现 java.lang.NumberFormatException 或者出现不正确的日期解析结果
     * java.lang.NumberFormatException
     */
    private static void demo1() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0;i<10;i++){
            new Thread(()->{
                try {
                    log.debug("{}", sdf.parse("2020-11-30"));
                } catch (Exception e) {
                    log.error("{}", e);
                }
            },"ThreadId-"+i).start();
        }
    }

    /**
     * DateTimeFormatter JDK8 提供的线程安全时间转换
     * 不可变对象，实际是另一种避免竞争的方式
     */
    private static void demo2() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (int i = 0;i<10;i++){
            new Thread(()->{
                LocalDate date = dtf.parse("2020-11-30", temporal -> LocalDate.from(temporal));
                log.info("{}",date);
            },"ThreadId-"+i).start();
        }
    }
}
