package cn.whiteg.rpgArmour.entityWrapper;

import cn.whiteg.mmocore.reflection.ReflectUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Rotations;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("unchecked")
public abstract class LivingEntityWrapper extends EntityWrapper {
    float headRotation = 0;
    static final EntityDataAccessor<Byte> DATA_LIVING_ENTITY_FLAGS;
    static final EntityDataAccessor<Float> DATA_HEALTH_ID;
    static final EntityDataAccessor<List<ParticleOptions>> DATA_EFFECT_PARTICLES;
    static final EntityDataAccessor<Boolean> DATA_EFFECT_AMBIENCE_ID;
    static final EntityDataAccessor<Integer> DATA_ARROW_COUNT_ID;
    static final EntityDataAccessor<Integer> DATA_STINGER_COUNT_ID;
    static final EntityDataAccessor<Optional<BlockPos>> SLEEPING_POS_ID;


    static {
        try{
            // 反射获取数据
            DATA_HEALTH_ID = (EntityDataAccessor<Float>) ReflectUtil.getFieldAndAccessible(LivingEntity.class,"DATA_HEALTH_ID").get(null);
            DATA_EFFECT_PARTICLES = (EntityDataAccessor<List<ParticleOptions>>) ReflectUtil.getFieldAndAccessible(LivingEntity.class,"DATA_EFFECT_PARTICLES").get(null);
            DATA_EFFECT_AMBIENCE_ID = (EntityDataAccessor<Boolean>) ReflectUtil.getFieldAndAccessible(LivingEntity.class,"DATA_EFFECT_AMBIENCE_ID").get(null);
            DATA_ARROW_COUNT_ID = (EntityDataAccessor<Integer>) ReflectUtil.getFieldAndAccessible(LivingEntity.class,"DATA_ARROW_COUNT_ID").get(null);
            DATA_STINGER_COUNT_ID = (EntityDataAccessor<Integer>) ReflectUtil.getFieldAndAccessible(LivingEntity.class,"DATA_STINGER_COUNT_ID").get(null);
            SLEEPING_POS_ID = (EntityDataAccessor<Optional<BlockPos>>) ReflectUtil.getFieldAndAccessible(LivingEntity.class,"SLEEPING_POS_ID").get(null);
            DATA_LIVING_ENTITY_FLAGS = (EntityDataAccessor<Byte>) ReflectUtil.getFieldAndAccessible(LivingEntity.class,"DATA_LIVING_ENTITY_FLAGS").get(null);
        }catch (IllegalAccessException | NoSuchFieldException e){
            throw new RuntimeException(e);
        }
    }

    public LivingEntityWrapper(EntityType<? extends Entity> entityType) {
        super(entityType);
        getDataWatcherBuilder()
                .define(DATA_LIVING_ENTITY_FLAGS,(byte) 0)
                .define(DATA_EFFECT_PARTICLES,List.of())
                .define(DATA_EFFECT_AMBIENCE_ID,false)
                .define(DATA_ARROW_COUNT_ID,0)
                .define(DATA_STINGER_COUNT_ID,0)
                .define(DATA_HEALTH_ID,1.0F)
                .define(SLEEPING_POS_ID,Optional.empty());
    }

    public Packet<ClientGamePacketListener> createPacketLeashHolder(int targetId) {
        FriendlyByteBuf buff = createDataSerializer();
        buff.writeInt(this.getEntityId()).writeInt(targetId);
        return ClientboundSetEntityLinkPacket.STREAM_CODEC.decode(buff);

    }

    public float getHeadRotation() {
        return headRotation;
    }

    public void setHeadRotation(float headRotation) {
        this.headRotation = headRotation;
    }

}
