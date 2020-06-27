package cn.whiteg.rpgArmour.entityWrapper;

import net.minecraft.server.v1_16_R1.DataWatcherObject;
import net.minecraft.server.v1_16_R1.EntityItem;
import net.minecraft.server.v1_16_R1.EntityTypes;
import net.minecraft.server.v1_16_R1.ItemStack;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R1.inventory.CraftItemStack;

import java.lang.reflect.Field;

public class DropItem extends EntityWrapper {
    static DataWatcherObject<ItemStack> ITEM;
    private ItemStack nmsItem;

    static {
        try{
            Field f = EntityItem.class.getDeclaredField("ITEM");
            f.setAccessible(true);
            ITEM = (DataWatcherObject<ItemStack>) f.get(null);
        }catch (NoSuchFieldException | IllegalAccessException e){
            e.printStackTrace();
        }
    }
    public DropItem(Location location,org.bukkit.inventory.ItemStack item) {
        super(EntityTypes.ITEM);
        this.location = location;
        nmsItem = CraftItemStack.asNMSCopy(item);
        initDataWatcher();
//        setItemStack(item);
    }

    @Override
    public void initDataWatcher() {
        super.initDataWatcher();
        dataWatcher.register(ITEM,nmsItem);
    }

    public org.bukkit.inventory.ItemStack getItemStack() {
        return CraftItemStack.asBukkitCopy(nmsItem);
    }

    public void setItemStack(org.bukkit.inventory.ItemStack item) {
        nmsItem = CraftItemStack.asNMSCopy(item);
        dataWatcher.set(ITEM,nmsItem);
    }
}
