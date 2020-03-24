package cn.whiteg.rpgArmour.nms;

import net.minecraft.server.v1_15_R1.NBTTagCompound;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class NMSItem_1_15_R1 extends NMSItem {
    private final net.minecraft.server.v1_15_R1.ItemStack nmsItem;
    private TagCompound roottag;
    private ItemStack bukkitItem;

    public NMSItem_1_15_R1(ItemStack item) {
        this.bukkitItem = item;
        nmsItem = CraftItemStack.asNMSCopy(item);
        roottag = new TagCompound_1_15_R1(nmsItem.getTag());
    }


    @Override
    public boolean hasTag() {
        return nmsItem.hasTag();
    }

    @Override
    public TagCompound getTag() {
        return roottag;
    }

    @Override
    public void setTag(TagCompound tag) {
        roottag = tag;
        nmsItem.setTag((NBTTagCompound) roottag.getHander());
    }

    @Override
    public TagCompound craftTag() {
        NBTTagCompound nc = new NBTTagCompound();
        nmsItem.setTag(nc);
        return new TagCompound_1_15_R1(nc);
    }

    @Override
    public ItemStack update() {
        nmsItem.setTag((NBTTagCompound) roottag.getHander());
        return bukkitItem = CraftItemStack.asBukkitCopy(nmsItem);
    }

    @Override
    public ItemStack getItem() {
        return bukkitItem;
    }
}
