package cn.whiteg.rpgArmour.entityWrapper;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.inventory.CraftItemStack;

import java.lang.reflect.Field;

public abstract class ThrowableItemWrapper extends EntityWrapper {
    private static final EntityDataAccessor<ItemStack> DATA_ITEM_STACK;

    static {
        try{
            final Field field = ThrowableItemProjectile.class.getDeclaredField("DATA_ITEM_STACK");
            field.setAccessible(true);
            //noinspection unchecked
            DATA_ITEM_STACK = (EntityDataAccessor<ItemStack>) field.get(null);
        }catch (NoSuchFieldException | IllegalAccessException e){
            throw new RuntimeException(e);
        }

    }

    public ThrowableItemWrapper(EntityType<? extends Entity> entityType) {
        super(entityType);
        getDataWatcherBuilder().define(DATA_ITEM_STACK,ItemStack.EMPTY);
    }


    public org.bukkit.inventory.ItemStack getItemStack() {
        return getDataWatcher().get(DATA_ITEM_STACK).asBukkitCopy();
    }

    public void setItemStack(org.bukkit.inventory.ItemStack itemStack) {
        getDataWatcher().set(DATA_ITEM_STACK,CraftItemStack.asNMSCopy(itemStack));
    }
}
