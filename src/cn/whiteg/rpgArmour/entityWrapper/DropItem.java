package cn.whiteg.rpgArmour.entityWrapper;

import net.minecraft.server.v1_15_R1.DataWatcherObject;
import net.minecraft.server.v1_15_R1.EntityItem;
import net.minecraft.server.v1_15_R1.EntityTypes;
import net.minecraft.server.v1_15_R1.ItemStack;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;

import java.lang.reflect.Field;

public class DropItem extends EntityWrapper {


    final static Field handleField;
    static DataWatcherObject<ItemStack> ITEM;

    static {
        Field handleField1;
        try{
            Field f = EntityItem.class.getDeclaredField("ITEM");
            f.setAccessible(true);
            ITEM = (DataWatcherObject<ItemStack>) f.get(null);
        }catch (NoSuchFieldException | IllegalAccessException e){
            e.printStackTrace();
        }
        try{
            handleField1 = CraftItemStack.class.getDeclaredField("handle");
        }catch (NoSuchFieldException e){
            handleField1 = null;
            e.printStackTrace();
        }
        handleField = handleField1;
        if (handleField != null){
            handleField.setAccessible(true);
        }
    }

    public ItemStack itemstack;

    public DropItem(Location location,org.bukkit.inventory.ItemStack item) {
        super(EntityTypes.ITEM);
        this.location = location;
        CraftItemStack craftItemStack = (CraftItemStack) item;
        try{
            itemstack = (ItemStack) handleField.get(craftItemStack);
        }catch (IllegalAccessException e){
            e.printStackTrace();
        }
        initDataWatcher();
    }


    @Override
    EntityType getEntityType() {
        return null;
    }

    /**
     * Create a NMS data watcher object to send via a {@code PacketPlayOutEntityMetadata} packet.
     * Gravity will be disabled and the custom name will be displayed if available.
     */
    @Override
    public void initDataWatcher() {
        super.initDataWatcher();
        dataWatcher.register(ITEM,itemstack);
    }

}
