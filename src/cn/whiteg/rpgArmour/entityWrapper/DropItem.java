package cn.whiteg.rpgArmour.entityWrapper;


import cn.whiteg.rpgArmour.utils.NMSUtils;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack;

import java.lang.reflect.Field;

public class DropItem extends EntityWrapper {
    static DataWatcherObject<ItemStack> ITEM;
    static EntityTypes<EntityItem> ITEM_TYPE;

    static {
        try{
            Field f = NMSUtils.getFieldFormType(EntityItem.class,DataWatcherObject.class);
            f.setAccessible(true);
            //noinspection unchecked
            ITEM = (DataWatcherObject<ItemStack>) f.get(null);
            ITEM_TYPE = NMSUtils.getEntityType(EntityItem.class);
        }catch (IllegalAccessException | NoSuchFieldException e){
            e.printStackTrace();
        }
    }

    private ItemStack nmsItem;

    public DropItem(Location location,org.bukkit.inventory.ItemStack item) {
        super(ITEM_TYPE);
        this.location = location;
        nmsItem = CraftItemStack.asNMSCopy(item);
        initDataWatcher();
//        setItemStack(item);
    }

    @Override
    public void initDataWatcher() {
        super.initDataWatcher();
        dataWatcher.a(ITEM,nmsItem);
    }

    public org.bukkit.inventory.ItemStack getItemStack() {
        return CraftItemStack.asBukkitCopy(nmsItem);
    }

    public void setItemStack(org.bukkit.inventory.ItemStack item) {
        nmsItem = CraftItemStack.asNMSCopy(item);
        dataWatcher.b(ITEM,nmsItem);
    }
}
