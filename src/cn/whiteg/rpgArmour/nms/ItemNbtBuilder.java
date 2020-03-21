package cn.whiteg.rpgArmour.nms;

import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;

public class ItemNbtBuilder implements NMSItem {
    private static Constructor con;

    static {
        Class<?> nmsClass = nmsItem_1_15_R1.class;
        try{
            con = nmsClass.getConstructor(ItemStack.class);
        }catch (NoSuchMethodException e){
            e.printStackTrace();
        }
    }

    private NMSItem nmsobj;

    public ItemNbtBuilder(ItemStack item) {
        try{
            nmsobj = (NMSItem) con.newInstance(item);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean hasTag() {
        return nmsobj.hasTag();
    }

    @Override
    public TagCompound getTag() {
        return nmsobj.getTag();
    }

    @Override
    public void setTag(TagCompound tag) {
        nmsobj.setTag(tag);
    }

    @Override
    public TagCompound craftTag() {
        return nmsobj.craftTag();
    }

    @Override
    public ItemStack update() {
        return nmsobj.update();
    }

    @Override
    public ItemStack getItem() {
        return nmsobj.getItem();
    }
}
