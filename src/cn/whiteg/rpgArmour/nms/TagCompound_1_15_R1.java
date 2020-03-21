package cn.whiteg.rpgArmour.nms;

import net.minecraft.server.v1_15_R1.NBTTagCompound;

import java.util.Set;

public class TagCompound_1_15_R1 implements TagCompound {
    private NBTTagCompound tag;

    public TagCompound_1_15_R1(NBTTagCompound tagCompound) {
        tag = tagCompound;
    }

    @Override
    public boolean hasTag() {
        return tag!=null;
    }

    @Override
    public void craftTag() {
        tag = new NBTTagCompound();
    }

    @Override
    public String getString(String key) {
        return tag.getString(key);
    }

    @Override
    public boolean getBoolean(String key) {
        return tag.getBoolean(key);
    }

    @Override
    public int getInt(String key) {
        return tag.getInt(key);
    }

    @Override
    public double getDouble(String key) {
        return tag.getDouble(key);
    }

    @Override
    public Set<String> getKeys() {
        return tag.getKeys();
    }

    @Override
    public TagCompound getCompound(String key){
        return new TagCompound_1_15_R1(tag.getCompound(key));
    }
    @Override
    public void setString(String key , String str) {
        tag.setString(key, str);
    }

    @Override
    public void setBoolean(String key ,boolean b) {
        tag.setBoolean(key , b);
    }

    @Override
    public void setInt(String key , int i) {
        tag.setInt(key , i);
    }

    @Override
    public void setDouble(String key , double d) {
        tag.setDouble(key,d);
    }

    @Override
    public TagCompound clone(String key){
        return new TagCompound_1_15_R1(tag.clone());
    }

    @Override
    public NBTTagCompound getHander(){
        return tag;
    }
}
