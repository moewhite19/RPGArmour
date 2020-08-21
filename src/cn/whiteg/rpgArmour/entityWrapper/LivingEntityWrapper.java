package cn.whiteg.rpgArmour.entityWrapper;

import net.minecraft.server.v1_16_R2.*;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public abstract class LivingEntityWrapper extends EntityWrapper {
    float headRotation = 0;
    EntityTypes<?> types;

    public LivingEntityWrapper(EntityTypes<? extends Entity> entityType) {
        super(entityType);
    }

    @Override
    public Packet<?> createPacketSpawnEntity() {
        try{
            Class<?> packetClass = PacketPlayOutSpawnEntityLiving.class;
            Object packet = packetClass.getConstructor().newInstance();
            Field[] fields = new Field[]{
                    packetClass.getDeclaredField("a"), // ID
                    packetClass.getDeclaredField("b"), // UUID (Only 1.9+)
                    packetClass.getDeclaredField("c"), // Type
                    packetClass.getDeclaredField("d"),// Loc X
                    packetClass.getDeclaredField("e"),// Loc Y
                    packetClass.getDeclaredField("f"),// Loc Z
                    packetClass.getDeclaredField("g"),// Mot X
                    packetClass.getDeclaredField("h"),// Mot Y
                    packetClass.getDeclaredField("i"),// Mot Z
                    packetClass.getDeclaredField("j"),// Yaw
                    packetClass.getDeclaredField("k"), // Pitch
                    packetClass.getDeclaredField("l") // Head Rotation
            };
            for (Field field : fields) {
                field.setAccessible(true);
            }
            fields[0].set(packet,entityId);
            fields[1].set(packet,uuid);
            fields[2].set(packet,IRegistry.ENTITY_TYPE.a(types));
            fields[3].set(packet,location.getX());
            fields[4].set(packet,location.getY());
            fields[5].set(packet,location.getZ());
            Vector mot = getVector();
            if (mot != null){
                fields[6].set(packet,(int) MathHelper.a(mot.getX(),-3.9D,3.9D) * 8000);
                fields[7].set(packet,(int) MathHelper.a(mot.getY(),-3.9D,3.9D) * 8000);
                fields[8].set(packet,(int) MathHelper.a(mot.getZ(),-3.9D,3.9D) * 8000);
            } else {
                fields[6].set(packet,0);
                fields[7].set(packet,0);
                fields[8].set(packet,0);
            }
            fields[9].set(packet,((byte) ((int) (location.getYaw() * 256.0F / 360.0F))));
            fields[10].set(packet,(byte) ((int) (location.getPitch() * 256.0F / 360.0F)));
            fields[11].set(packet,(byte) ((int) (headRotation * 256.0F / 360.0F)));
            return (Packet<?>) packet;
        }catch (NoSuchMethodException | NoSuchFieldException | IllegalAccessException | InvocationTargetException | InstantiationException e){
            e.printStackTrace();
        }
        return null;
    }

    public float getHeadRotation() {
        return headRotation;
    }

    public void setHeadRotation(float headRotation) {
        this.headRotation = headRotation;
    }
}
