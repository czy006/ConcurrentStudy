package com.gzczy.concurrent.threadlocal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @Description 关于线程安全的时间工具测试演示 参考阿里手册
 * @Author chenzhengyu
 * @Date 2021-03-27 18:43
 */
public class ThreadLocalDateUtilDemo {

    public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static ThreadLocal<SimpleDateFormat> sdfThreadLocal = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

    public static DateTimeFormatter dfm = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.CHINESE).withZone(ZoneId.systemDefault());;


    /**
     * 非线程安全的线程转换
     * 多线程运行下存在的问题：
     * 1、For input string ""
     * 2、empty string
     * 3、乱七八糟的编码报错
     * 4、某些事件段是正确的
     *
     * @param date
     * @return
     * @throws ParseException
     */
    public static Date parseDate(String date) throws ParseException {
        return sdf.parse(date);
    }

    /**
     * 通过ThreadLocal 进行线程隔离 然后相当于每个线程只操作自己的sdf 不会产生多线程竞争情况
     * 参考阿里手册-》并发编程 ThreadLocal的方法
     *
     * @param date
     * @return
     * @throws ParseException
     */
    public static Date parseDateLocal(String date) throws ParseException {
        return sdfThreadLocal.get().parse(date);
    }

    /**
     * 基于ZonedDateTime + DateTimeFormatter 测试
     * @param date
     * @return
     */
    public static String parseDateJDK8(ZonedDateTime date) {
        return dfm.format(date);
    }

    /**
     * dfm需要添加  withZone(ZoneId.systemDefault() 不添加会报错 {@link java.time.temporal.UnsupportedTemporalTypeException}
     * Unsupported field: YearOfEra
     * 如果没有时区，格式化程序将不知道如何将即时字段转换为人类日期时间字段，因此会抛出异常
     *
     * 底层调用 return new DateTimeFormatter(pp, locale, DecimalStyle.STANDARD,
     *                 resolverStyle, null, chrono, null); 最后一个时区参数为null
     * @param date
     * @return
     */
    public static String parseDateInstantJDK8(Instant date) {
       return dfm.format(date);
    }


    public static void main(String[] args) {
        //unsafeDemo();
        //safeDemo();
        //jdk8Demo1();
        jdk8Demo2();

    }

    private static void jdk8Demo2() {
        for (int i = 1; i <= 10; i++) {
            new Thread(() -> {
                try {
                    System.out.println(parseDateInstantJDK8(Instant.now()));
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {

                }
            }, String.valueOf(i)).start();
        }
    }

    private static void jdk8Demo1() {
        for (int i = 1; i <= 10; i++) {
            new Thread(() -> {
                try {
                    //随机获取当前时间输出打印
                    TimeUnit.SECONDS.sleep(new Random().nextInt(10));
                    System.out.println(parseDateJDK8(ZonedDateTime.now()));
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {

                }
            }, String.valueOf(i)).start();
        }
    }

    private static void safeDemo() {
        for (int i = 1; i <= 10; i++) {
            new Thread(() -> {
                try {
                    System.out.println(ThreadLocalDateUtilDemo.parseDateLocal("2011-11-11 11:11:11"));
                } catch (ParseException e) {
                    e.printStackTrace();
                } finally {
                    sdfThreadLocal.remove();
                }
            }, String.valueOf(i)).start();
        }
    }

    private static void unsafeDemo() {
        for (int i = 1; i <= 10; i++) {
            new Thread(() -> {
                try {
                    System.out.println(ThreadLocalDateUtilDemo.parseDate("2011-11-11 11:11:11"));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }, String.valueOf(i)).start();
        }
    }
}
