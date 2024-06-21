package cn.whiteg.rpgArmour.entityWrapper;

import org.bukkit.Location;

public class HoloText extends ArmorStandWrapper {

    public HoloText(Location location,String customName) {
        super(location,customName);
        this.location = location;
        this.customName = customName;
    }

    /**
     * Create a NMS data watcher object to send via a {@code ClientboundSetEntityDataPacket} packet.
     * Gravity will be disabled and the custom name will be displayed if available.
     */
    @Override
    public void initDataWatcher() {
        super.initDataWatcher();
        setInvisible(true);
        setCustomName(customName);
        setMarker(true);
        setCustomNameVisible(true);
    }
}
