package cn.whiteg.rpgArmour.nms;

import java.util.Set;

public interface TagCompound<T> {
    boolean hasTag();

    void craftTag();

    String getString(String key);

    boolean getBoolean(String key);

    void setString(String key,String str);

    void setBoolean(String key,boolean b);

    int getInt(String key);

    void setInt(String key,int i);

    double getDouble(String key);

    void setDouble(String key,double d);

    Set<String> getKeys();

    TagCompound getCompound(String key);

    TagCompound clone(String key);

    T getHander();
}
