package cn.whiteg.rpgArmour.reflection;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class ReflectionFactory {
    public static final Unsafe UNSAFE;
    public static Object INTERNAL_UNSAFE;


    static {
        UNSAFE = AccessController.doPrivileged((PrivilegedAction<Unsafe>) () -> {
            try{
                var f = Unsafe.class.getDeclaredField("theUnsafe");
                f.setAccessible(true);
                return (Unsafe) f.get(null);
            }catch (NoSuchFieldException | IllegalAccessException e){
                e.printStackTrace();
                return null;
            }
        });

        try{
            var fa = new FieldAccessor<>(Unsafe.class.getDeclaredField("theInternalUnsafe"));
            INTERNAL_UNSAFE = fa.get(null);
        }catch (NoSuchFieldException e){
            e.printStackTrace();
        }

    }

    //
    public static FieldAccessor<?> createFieldAccessor(Field field){
        return new FieldAccessor<>(field);
    }


    //构建一个对象但不调用构建方法
    @SuppressWarnings("unchecked")
    public <T> T allocateInstance(Class<T> clazz) throws InstantiationException {
        return (T) UNSAFE.allocateInstance(clazz);
    }

    public long objectFieldOffset(Class<?> clazz,String name) {
//        INTERNAL_UNSAFE.getClass().g
//        INTERNAL_UNSAFE.objectFieldOffset(clazz,name);
        return 0;
    }
}
