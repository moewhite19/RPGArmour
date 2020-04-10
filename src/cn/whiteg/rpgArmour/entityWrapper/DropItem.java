package cn.whiteg.rpgArmour.entityWrapper;

import net.minecraft.server.v1_15_R1.DataWatcherObject;
import net.minecraft.server.v1_15_R1.EntityTypes;
import net.minecraft.server.v1_15_R1.ItemStack;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;

public class DropItem extends EntityWrapper {


    static DataWatcherObject<ItemStack> ITEM;

    static {

    }

    private ItemStack nmsItem;

    public DropItem(Location location,org.bukkit.inventory.ItemStack item) {
        super(EntityTypes.ITEM);
        this.location = location;
        setItemStack(item);
        initDataWatcher();
    }


    @Override
    EntityType getEntityType() {
        return null;
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
