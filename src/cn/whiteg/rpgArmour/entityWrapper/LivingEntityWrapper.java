package cn.whiteg.rpgArmour.entityWrapper;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketListenerPlayOut;
import net.minecraft.network.protocol.game.PacketPlayOutAttachEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;

public abstract class LivingEntityWrapper extends EntityWrapper {
    float headRotation = 0;
    EntityTypes<?> types;

    public LivingEntityWrapper(EntityTypes<? extends Entity> entityType) {
        super(entityType);
    }

    public Packet<PacketListenerPlayOut> createPacketLeashHolder(int targetId) {
        var data = createDataSerializer();
        data.writeInt(this.getEntityId()).writeInt(targetId);
        return new PacketPlayOutAttachEntity(data);

    }

    public float getHeadRotation() {
        return headRotation;
    }

    public void setHeadRotation(float headRotation) {
        this.headRotation = headRotation;
    }

}
