package cn.whiteg.rpgArmour.entityWrapper;


import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;

public abstract class DisplayWrapper extends EntityWrapper {
    static DataWatcherObject<ItemStack> ITEM;
//todo 等待Mapping
//    private static final DataWatcherObject<Integer> q;
//    private static final DataWatcherObject<Integer> r;
//    private static final DataWatcherObject<Vector3f> s;
//    private static final DataWatcherObject<Vector3f> t;
//    private static final DataWatcherObject<Quaternionf> u;
//    private static final DataWatcherObject<Quaternionf> aC;
//    private static final DataWatcherObject<Byte> aD;
//    private static final DataWatcherObject<Integer> aE;
//    private static final DataWatcherObject<Float> aF;
//    private static final DataWatcherObject<Float> aG;
//    private static final DataWatcherObject<Float> aH;
//    private static final DataWatcherObject<Float> aI;
//    private static final DataWatcherObject<Float> aJ;
//    private static final DataWatcherObject<Integer> aK;
    static {
//        try{

    }

    private ItemStack nmsItem;

    public DisplayWrapper(EntityTypes<? extends Entity> entityType) {
        super(entityType);
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
