package cn.whiteg.rpgArmour.nms;


import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public abstract class NMSItem {
    private static Constructor con;

    static {
        Class<?> nmsClass = NMSItem_1_16_R2.class;
        try{
            con = nmsClass.getConstructor(ItemStack.class);
        }catch (NoSuchMethodException e){
            e.printStackTrace();
        }
    }

    public static NMSItem asNmsItemCopy(ItemStack item) {
        try{
            return (NMSItem) con.newInstance(item);
        }catch (InstantiationException | IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
            return null;
        }
    }

    public abstract boolean hasTag();

    public abstract TagCompound getTag();

    public abstract void setTag(TagCompound tag);

    public abstract TagCompound craftTag();

    public abstract ItemStack update();

    public abstract ItemStack getItem();
}
