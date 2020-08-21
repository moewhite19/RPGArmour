package cn.whiteg.rpgArmour.entityWrapper;

import cn.whiteg.rpgArmour.utils.JsonBuilder;
import net.minecraft.server.v1_16_R2.*;
import org.bukkit.Location;

import java.util.Optional;

public class HoloText extends EntityWrapper {

    final static DataWatcherObject<Byte> marker = EntityArmorStand.b;

    static {

    }

    public HoloText(Location location,String customName) {
        super(EntityTypes.ARMOR_STAND);
        this.location = location;
        this.customName = customName;
        initDataWatcher();
    }

    public void setMarker(boolean flag) {
        final byte by = (byte) (flag ? 16 : 0);
        dataWatcher.set(marker,by);
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
            IChatBaseComponent ibc = ChatComponentScore.ChatSerializer.a(JsonBuilder.parse(customName).toString());
            dataWatcher.set(displayName,Optional.of(ibc));
        }
        dataWatcher.register(marker,(byte) 16); // marker
    }
}
