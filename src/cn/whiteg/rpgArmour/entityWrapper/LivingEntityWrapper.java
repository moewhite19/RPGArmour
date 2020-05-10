package cn.whiteg.rpgArmour.entityWrapper;

import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public abstract class LivingEntityWrapper extends EntityWrapper {
    float headRotation = 0;

    public LivingEntityWrapper(EntityTypes<? extends Entity> entityType) {
        super(entityType);
    }

    @Override
    public Packet<?> createPacketSpawnEntity(int id,UUID uuid,Location loc,EntityTypes<? extends Entity> type) {
        try{
            Class<?> packetClass = PacketPlayOutSpawnEntity.class;
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
            fields[0].set(packet,id);
            fields[1].set(packet,uuid);
            fields[2].set(packet,IRegistry.ENTITY_TYPE.a(type));
            fields[3].set(packet,loc.getX());
            fields[4].set(packet,loc.getY());
            fields[5].set(packet,loc.getZ());
            Vector mot = getVector();
            fields[6].set(packet,MathHelper.a(mot.getX(),-3.9D,3.9D) * 8000);
            fields[7].set(packet,MathHelper.a(mot.getY(),-3.9D,3.9D) * 8000);
            fields[8].set(packet,MathHelper.a(mot.getZ(),-3.9D,3.9D) * 8000);
            fields[8].set(packet,(int) (loc.getYaw() * 256.0F / 360.0F));
            fields[9].set(packet,(int) (loc.getPitch() * 256.0F / 360.0F));
            fields[11].set(packet,(int) (headRotation * 256.0F / 360.0F));
            return (PacketPlayOutSpawnEntity) packet;
        }catch (NoSuchMethodException | NoSuchFieldException | IllegalAccessException | InvocationTargetException | InstantiationException e){
            e.printStackTrace();
//            plugin.getLogger().severe("Failed to create packet to spawn entity!");
//            plugin.debug("Failed to create packet to spawn entity!");
//            plugin.debug(e);
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
