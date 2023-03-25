package cn.whiteg.rpgArmour.entityWrapper;

import cn.whiteg.rpgArmour.utils.NMSUtils;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import net.minecraft.world.entity.item.EntityItem;
import org.bukkit.Location;

import java.util.Optional;

public class HoloText extends LivingEntityWrapper {

    static DataWatcherObject<Byte> marker;
    private static EntityTypes<EntityItem> ITEM_TYPE;

    static {
        try{
            //noinspection unchecked
            marker = (DataWatcherObject<Byte>) NMSUtils.getFieldFormType(EntityArmorStand.class,"net.minecraft.network.syncher.DataWatcherObject<java.lang.Byte>").get(null);
            ITEM_TYPE = NMSUtils.getEntityType(EntityArmorStand.class);
        }catch (IllegalAccessException | NoSuchFieldException e){
            e.printStackTrace();
        }
    }

    public HoloText(Location location,String customName) {
        super(ITEM_TYPE);
        this.location = location;
        this.customName = customName;
        initDataWatcher();
    }

    public void setMarker(boolean flag) {
        final byte by = (byte) (flag ? 16 : 0);
        dataWatcher.b(marker,by);
        sendUpdate();
    }

    /**
     * Create a NMS data watcher object to send via a {@code PacketPlayOutEntityMetadata} packet.
     * Gravity will be disabled and the custom name will be displayed if available.
     */
    @Override
    public void initDataWatcher() {
        super.initDataWatcher();
        if (customName != null){
            IChatBaseComponent ibc = IChatBaseComponent.a(customName);
            dataWatcher.b(displayName,Optional.of(ibc));
        }
        dataWatcher.a(marker,(byte) 16); // marker
    }
}
