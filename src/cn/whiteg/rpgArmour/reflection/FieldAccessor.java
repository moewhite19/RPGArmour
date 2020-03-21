package cn.whiteg.rpgArmour.reflection;

import com.google.common.collect.ImmutableMap;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

import static cn.whiteg.rpgArmour.reflection.ReflectionFactory.UNSAFE;


public class FieldAccessor<T> {
    private static final Map<Class<?>, GetAndSetter<?>> getterMap;

    static {
        ImmutableMap.Builder<Class<?>, GetAndSetter<?>> builder = ImmutableMap.builder();

        builder.put(int.class,new GetAndSetter<Integer>() {
            @Override
            public Integer get(FieldAccessor<?> fieldAccessor,Object o) {
                return fieldAccessor.getInt(o);
            }

            @Override
            public void set(FieldAccessor<?> fa,Object obj,Integer value) {
                fa.setInt(obj,value);
            }
        });
        builder.put(long.class,new GetAndSetter<Long>() {
            @Override
            public Long get(FieldAccessor<?> fieldAccessor,Object o) {
                return fieldAccessor.getLong(o);
            }

            @Override
            public void set(FieldAccessor<?> fa,Object obj,Long value) {
                fa.setLong(obj,value);
            }
        });
        builder.put(boolean.class,new GetAndSetter<Boolean>() {
            @Override
            public Boolean get(FieldAccessor<?> fieldAccessor,Object o) {
                return fieldAccessor.getBoolean(o);
            }

            @Override
            public void set(FieldAccessor<?> fa,Object obj,Boolean value) {
                fa.setBoolean(obj,value);
            }
        });
        builder.put(short.class,new GetAndSetter<Short>() {
            @Override
            public Short get(FieldAccessor<?> fa,Object obj) {
                return fa.getShort(obj);
            }

            @Override
            public void set(FieldAccessor<?> fa,Object obj,Short value) {
                fa.setShort(obj,value);
            }
        });
        builder.put(byte.class,new GetAndSetter<Byte>() {
            @Override
            public Byte get(FieldAccessor<?> fa,Object obj) {
                return fa.getByte(obj);
            }

            @Override
            public void set(FieldAccessor<?> fa,Object obj,Byte value) {
                fa.setByte(obj,value);
            }
        });
        builder.put(double.class,new GetAndSetter<Double>() {
            @Override
            public Double get(FieldAccessor<?> fa,Object obj) {
                return fa.getDouble(obj);
            }

            @Override
            public void set(FieldAccessor<?> fa,Object obj,Double value) {
                fa.setDouble(obj,value);
            }
        });
        builder.put(float.class,new GetAndSetter<Float>() {
            @Override
            public Float get(FieldAccessor<?> fa,Object obj) {
                return fa.getFloat(obj);
            }

            @Override
            public void set(FieldAccessor<?> fa,Object obj,Float value) {
                fa.setFloat(obj,value);
            }
        });
        getterMap = builder.build();
    }

    private final long offset;
    private final Class<?> base;
    private final Class<T> type;
    private final GetAndSetter<T> getter;

    @SuppressWarnings("unchecked")
    public FieldAccessor(Field field) {
        if (Modifier.isStatic(field.getModifiers())){
            offset = UNSAFE.staticFieldOffset(field);
            base = field.getDeclaringClass();
        } else {
            offset = UNSAFE.objectFieldOffset(field);
            base = null;
        }
        type = (Class<T>) field.getType();
        getter = (GetAndSetter<T>) getterMap.get(type);
    }

    public FieldAccessor(Class<?> declaring,long offset,Class<T> type) {
        this.offset = offset;
        this.base = declaring;
        this.type = type;
        //noinspection unchecked
        getter = (GetAndSetter<T>) getterMap.get(type);
    }

    public void set(Object o,T value) {
        if (getter == null) UNSAFE.getAndSetObject(getDeclaringClass(o),offset,value);
        else {
            getter.set(this,o,value);
        }
    }

    public T getAndSet(Object o,T value) {
        if (getter != null) throw new ClassCastException("当前Field类型无法转换成Object");
        //noinspection unchecked
        return (T) UNSAFE.getAndSetObject(o,offset,value);
    }

    @SuppressWarnings("unchecked")
    public T get(Object o) {
        return (T) (getter == null ? UNSAFE.getObject(getDeclaringClass(o),offset) : getter.get(this,o));
    }

    public int getInt(Object o) {
        return UNSAFE.getInt(getDeclaringClass(o),offset);
    }

    public boolean getBoolean(Object o) {
        return UNSAFE.getBoolean(getDeclaringClass(o),offset);
    }

    public short getShort(Object o) {
        return UNSAFE.getShort(getDeclaringClass(o),offset);
    }

    public byte getByte(Object o) {
        return UNSAFE.getByte(getDeclaringClass(o),offset);
    }

    public long getLong(Object o) {
        return UNSAFE.getLong(getDeclaringClass(o),offset);
    }

    public double getDouble(Object o) {
        return UNSAFE.getDoubleVolatile(getDeclaringClass(o),offset);
    }

    public float getFloat(Object o) {
        return UNSAFE.getFloatVolatile(getDeclaringClass(o),offset);
    }

    public void setBoolean(Object o,boolean value) {
        UNSAFE.putBooleanVolatile(getDeclaringClass(o),offset,value);
    }

    public void setDouble(Object o,double value) {
        UNSAFE.putDoubleVolatile(getDeclaringClass(o),offset,value);
    }

    public void setFloat(Object o,float value) {
        UNSAFE.putFloatVolatile(getDeclaringClass(o),offset,value);
    }

    public void setByte(Object o,byte value) {
        UNSAFE.putByteVolatile(getDeclaringClass(o),offset,value);
    }

    public void setShort(Object o,short value) {
        UNSAFE.putShortVolatile(getDeclaringClass(o),offset,value);
    }

    public void setInt(Object o,int newValue) {
        UNSAFE.putIntVolatile(getDeclaringClass(o),offset,newValue);
    }

    public void setLong(Object o,long newValue) {
        UNSAFE.putLongVolatile(getDeclaringClass(o),offset,newValue);
    }

    Object getDeclaringClass(Object obj) {
        if (base == null) return obj;
        if (obj == null) return base;
        throw new ClassCastException("非静态类使用null调用");
    }

    public Class<T> getType() {
        return type;
    }

    public long getOffset() {
        return offset;
    }

    public interface GetAndSetter<C> {
        C get(FieldAccessor<?> fa,Object obj);

        void set(FieldAccessor<?> fa,Object obj,C value);
    }
}
