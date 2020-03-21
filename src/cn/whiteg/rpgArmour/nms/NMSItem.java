package cn.whiteg.rpgArmour.nms;


import org.bukkit.inventory.ItemStack;

interface NMSItem {
    boolean hasTag();

    TagCompound getTag();

    void setTag(TagCompound tag);

    TagCompound craftTag();

    ItemStack update();

    ItemStack getItem();
}
