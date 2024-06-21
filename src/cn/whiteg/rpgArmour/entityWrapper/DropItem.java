package cn.whiteg.rpgArmour.entityWrapper;


import cn.whiteg.mmocore.reflection.ReflectUtil;
import cn.whiteg.mmocore.util.NMSUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Location;
import org.bukkit.craftbukkit.inventory.CraftItemStack;

import java.lang.reflect.Field;

public class DropItem extends EntityWrapper {
    static EntityDataAccessor<ItemStack> DATA_ITEM;
    static EntityType<ItemEntity> ITEM_TYPE;

    static {
        try{
            Field f = ReflectUtil.getFieldFormType(ItemEntity.class,EntityDataAccessor.class);
            f.setAccessible(true);
            //noinspection unchecked
            DATA_ITEM = (EntityDataAccessor<ItemStack>) f.get(null);
            ITEM_TYPE = NMSUtils.getEntityType(ItemEntity.class);
        }catch (IllegalAccessException | NoSuchFieldException e){
            e.printStackTrace();
        }
    }

    private ItemStack nmsItem;

    public DropItem(Location location,org.bukkit.inventory.ItemStack item) {
        super(ITEM_TYPE);
        this.location = location;
        nmsItem = CraftItemStack.asNMSCopy(item);
        getDataWatcherBuilder().define(DATA_ITEM,ItemStack.EMPTY);
        initDataWatcher();
//        setItemStack(item);
    }

    @Override
    public void initDataWatcher() {
        super.initDataWatcher();
        dataWatcher.set(DATA_ITEM,nmsItem);
    }

    public org.bukkit.inventory.ItemStack getItemStack() {
        return CraftItemStack.asBukkitCopy(nmsItem);
    }

    public void setItemStack(org.bukkit.inventory.ItemStack item) {
        nmsItem = CraftItemStack.asNMSCopy(item);
        dataWatcher.set(DATA_ITEM,nmsItem);
    }
}
