package cn.whiteg.rpgArmour.utils;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

public class ReflectUtil {
//    笨办法x 需要添加启动参数
//    需要添加启动参数"--add-exports java.base/jdk.internal.reflect=ALL-UNNAMED"
//    private static final ReflectionFactory reflection = AccessController.doPrivileged(
//            new ReflectionFactory.GetReflectionFactoryAction());
//    private static MethodAccessor newFieldAccessorMethod;
//    static {
//        try{
//            var clazz = ClassLoader.getSystemClassLoader().loadClass("jdk.internal.reflect.UnsafeFieldAccessorFactory");
//            var method = clazz.getDeclaredMethod("newFieldAccessor",Field.class,boolean.class);
//            newFieldAccessorMethod = reflection.newMethodAccessor(method);
//        }catch (ClassNotFoundException | NoSuchMethodException e){
//            e.printStackTrace();
//        }
//    }

    /**
     * * 检查所有的修饰符，是否是 public static final
     * * @param modify
     */
    public static void checkModifier(int modify) {
        System.out.println("当前的 modify : " + modify);
        System.out.println(" public : " + Modifier.isPublic(modify));
        System.out.println(" static : " + Modifier.isStatic(modify));
        System.out.println(" final : " + Modifier.isFinal(modify));
    }


    //反射获取Field
    public static Field getFieldAndAccessible(Class<?> c,String name) throws NoSuchFieldException {
        Field f = c.getDeclaredField(name);
        f.setAccessible(true);
        return f;
    }

    //构建私有对象
    public static <T> T newInstance(Class<? extends T> clazz,Object... values) throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        Class<?>[] types = new Class[values.length];
        for (int i = 0; i < values.length; i++) {
            types[i] = values[i].getClass();
        }
        Constructor<? extends T> con = clazz.getDeclaredConstructor(types);
        con.setAccessible(true);
        return con.newInstance(values);
    }

//    //获取Field访问器，同时无视final
//    public static FieldAccessor getFieldAccessible(Field f) throws InvocationTargetException {
//        return (FieldAccessor) newFieldAccessorMethod.invoke(null,new Object[]{f,false});
////        return reflection.newFieldAccessor(f,false);
//    }


    /**
     * 检查类组是否有继承
     *
     * @param shr   源类组
     * @param type2 被检查的组
     * @return 是否继承
     */
    public static boolean hasTypes(Class<?>[] shr,Class<?>[] type2) {
        if (shr.length == type2.length){
            for (int i = 0; i < shr.length; i++) if (!shr[i].isAssignableFrom(type2[i])) return false;
            return true;
        }
        return false;
    }
}
