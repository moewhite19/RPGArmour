package cn.whiteg.rpgArmour.entityWrapper;


import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.inventory.CraftItemStack;

public abstract class DisplayWrapper extends EntityWrapper {
    static EntityDataAccessor<ItemStack> ITEM;

    //todo 等待Mapping
//    private static final EntityDataAccessor<Integer> q;
//    private static final EntityDataAccessor<Integer> r;
//    private static final EntityDataAccessor<Vector3f> s;
//    private static final EntityDataAccessor<Vector3f> t;
//    private static final EntityDataAccessor<Quaternionf> u;
//    private static final EntityDataAccessor<Quaternionf> aC;
//    private static final EntityDataAccessor<Byte> aD;
//    private static final EntityDataAccessor<Integer> aE;
//    private static final EntityDataAccessor<Float> aF;
//    private static final EntityDataAccessor<Float> aG;
//    private static final EntityDataAccessor<Float> aH;
//    private static final EntityDataAccessor<Float> aI;
//    private static final EntityDataAccessor<Float> aJ;
//    private static final EntityDataAccessor<Integer> aK;
    static {
//        try{

    }

    private ItemStack nmsItem;

    public DisplayWrapper(EntityType<? extends Entity> entityType) {
        super(entityType);
    }

    @Override
    public void initDataWatcher() {
        super.initDataWatcher();
        dataWatcher.set(ITEM,nmsItem);
    }

    public org.bukkit.inventory.ItemStack getItemStack() {
        return CraftItemStack.asBukkitCopy(nmsItem);
    }

    public void setItemStack(org.bukkit.inventory.ItemStack item) {
        nmsItem = CraftItemStack.asNMSCopy(item);
        dataWatcher.set(ITEM,nmsItem);
    }
}
