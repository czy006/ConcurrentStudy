package com.gzczy.concurrent.utils;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @Description 使用并发安全的Map进行读取文件
 * @Author chenzhengyu
 * @Date 2021-04-05 09:37
 */
public class ConcurrentHashMapReadFile {

    static final String ALPHA = "abcedfghijklmnopqrstuvwxyz";

    static final String FILE_PATH = "../../IdeaProjects/ConcurrentStudy/tmp/";

    public static void main(String[] args) {
        //首先创建文件
        //createFile();
        demo(
                // 创建 map 集合
                // 创建 ConcurrentHashMap 对不对？
                () -> new HashMap<String, Integer>(),
                // 进行计数
                (map, words) -> {
                    for (String word : words) {
                        Integer counter = map.get(word);
                        int newValue = counter == null ? 1 : counter + 1;
                        map.put(word, newValue);
                    }
                }
        );
        // 创建 ConcurrentHashMap 对不对？答案是不对的，因为只能保证get和put的安全并发，两个API一起使用并不是原子性的
        demo(
                () -> new ConcurrentHashMap<String, Integer>(),
                // 进行计数
                (map, words) -> {
                    for (String word : words) {
                        Integer counter = map.get(word);
                        int newValue = counter == null ? 1 : counter + 1;
                        map.put(word, newValue);
                    }
                }
        );
        // 注意不能使用 putIfAbsent，此方法返回的是上一次的 value，首次调用返回 null
        demo(
                () -> new ConcurrentHashMap<String, LongAdder>(),
                // 进行计数
                (map, words) -> {
                    for (String word : words) {
                        map.computeIfAbsent(word, (key) -> new LongAdder()).increment();
                    }
                }
        );
        //使用函数式编程
        demo(
                () -> new ConcurrentHashMap<String, Integer>(),
                // 进行计数
                (map, words) -> {
                    for (String word : words) {
                        map.merge(word,1, (a, b) -> Integer.sum(a, b));
                    }
                }
        );
    }

    public static void createFile() {
        int length = ALPHA.length();
        int count = 200;
        List<String> list = new ArrayList<>(length * count);
        for (int i = 0; i < length; i++) {
            char ch = ALPHA.charAt(i);
            for (int j = 0; j < count; j++) {
                list.add(String.valueOf(ch));
            }
        }
        Collections.shuffle(list);
        for (int i = 0; i < 26; i++) {
            try (PrintWriter out = new PrintWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(FILE_PATH + (i + 1) + ".txt")))) {
                String collect = list.subList(i * count, (i + 1) * count).stream()
                        .collect(Collectors.joining("\n"));
                out.print(collect);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private static <V> void demo(Supplier<Map<String, V>> supplier,
                                 BiConsumer<Map<String, V>, List<String>> consumer) {
        Map<String, V> counterMap = supplier.get();
        List<Thread> ts = new ArrayList<>();
        for (int i = 1; i <= 26; i++) {
            int idx = i;
            Thread thread = new Thread(() -> {
                List<String> words = readFromFile(idx);
                consumer.accept(counterMap, words);
            });
            ts.add(thread);
        }
        ts.forEach(t -> t.start());
        ts.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        System.out.println(counterMap);
    }

    public static List<String> readFromFile(int i) {
        ArrayList<String> words = new ArrayList<>();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(FILE_PATH
                + i + ".txt")))) {
            while (true) {
                String word = in.readLine();
                if (word == null) {
                    break;
                }
                words.add(word);
            }
            return words;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
