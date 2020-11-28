package com.gzczy.concurrent.heima.d;

import lombok.Data;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * @Description UnSafe反射调用测试
 * @Author chenzhengyu
 * @Date 2020-11-28 18:59
 */
public class UnsafeAccessor {

    static Unsafe unsafe;

    static {
        try {
            //通过反射得到Unsafe对象
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            unsafe = (Unsafe) theUnsafe.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    static Unsafe getUnsafe() {
        return unsafe;
    }

    public static void main(String[] args) throws Exception {
        Unsafe unsafe = getUnsafe();
        Field id = Student.class.getDeclaredField("id");
        Field name = Student.class.getDeclaredField("name");
        // 获得成员变量的偏移量
        long idOffset = UnsafeAccessor.unsafe.objectFieldOffset(id);
        long nameOffset = UnsafeAccessor.unsafe.objectFieldOffset(name);
        Student student = new Student();
        System.out.println(student.toString());
        //通过unsafe底层类 找到内存的地址进行数据替换
        unsafe.compareAndSwapInt(student, idOffset,0,1);
        unsafe.compareAndSwapObject(student,nameOffset,null, "czy");
        System.out.println(student.toString());
    }
}

@Data
class Student {
    volatile int id;
    volatile String name;
}
