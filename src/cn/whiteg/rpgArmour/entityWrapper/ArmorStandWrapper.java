package cn.whiteg.rpgArmour.entityWrapper;

import cn.whiteg.mmocore.reflection.ReflectUtil;
import cn.whiteg.mmocore.util.NMSUtils;
import net.minecraft.core.Rotations;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.bukkit.Location;

@SuppressWarnings("unchecked")
public class ArmorStandWrapper extends LivingEntityWrapper {
    static EntityDataAccessor<Byte> DATA_CLIENT_FLAGS;
    static EntityDataAccessor<Rotations> DATA_HEAD_POSE;
    static EntityDataAccessor<Rotations> DATA_BODY_POSE;
    static EntityDataAccessor<Rotations> DATA_LEFT_ARM_POSE;
    static EntityDataAccessor<Rotations> DATA_RIGHT_ARM_POSE;
    static EntityDataAccessor<Rotations> DATA_LEFT_LEG_POSE;
    static EntityDataAccessor<Rotations> DATA_RIGHT_LEG_POSE;
    static final Rotations DEFAULT_HEAD_POSE;
    static final Rotations DEFAULT_BODY_POSE;
    static final Rotations DEFAULT_LEFT_ARM_POSE;
    static final Rotations DEFAULT_RIGHT_ARM_POSE;
    static final Rotations DEFAULT_LEFT_LEG_POSE;
    static final Rotations DEFAULT_RIGHT_LEG_POSE;

    static {


        try{
            DATA_CLIENT_FLAGS = (EntityDataAccessor<Byte>) ReflectUtil.getFieldAndAccessible(ArmorStand.class,"DATA_CLIENT_FLAGS").get(null);
            DATA_HEAD_POSE = (EntityDataAccessor<Rotations>) ReflectUtil.getFieldAndAccessible(ArmorStand.class,"DATA_HEAD_POSE").get(null);
            DATA_BODY_POSE = (EntityDataAccessor<Rotations>) ReflectUtil.getFieldAndAccessible(ArmorStand.class,"DATA_BODY_POSE").get(null);
            DATA_LEFT_ARM_POSE = (EntityDataAccessor<Rotations>) ReflectUtil.getFieldAndAccessible(ArmorStand.class,"DATA_LEFT_ARM_POSE").get(null);
            DATA_RIGHT_ARM_POSE = (EntityDataAccessor<Rotations>) ReflectUtil.getFieldAndAccessible(ArmorStand.class,"DATA_RIGHT_ARM_POSE").get(null);
            DATA_LEFT_LEG_POSE = (EntityDataAccessor<Rotations>) ReflectUtil.getFieldAndAccessible(ArmorStand.class,"DATA_LEFT_LEG_POSE").get(null);
            DATA_RIGHT_LEG_POSE = (EntityDataAccessor<Rotations>) ReflectUtil.getFieldAndAccessible(ArmorStand.class,"DATA_RIGHT_LEG_POSE").get(null);
            DEFAULT_HEAD_POSE = (Rotations) ReflectUtil.getFieldAndAccessible(ArmorStand.class,"DEFAULT_HEAD_POSE").get(null);
            DEFAULT_BODY_POSE = (Rotations) ReflectUtil.getFieldAndAccessible(ArmorStand.class,"DEFAULT_BODY_POSE").get(null);
            DEFAULT_LEFT_ARM_POSE = (Rotations) ReflectUtil.getFieldAndAccessible(ArmorStand.class,"DEFAULT_LEFT_ARM_POSE").get(null);
            DEFAULT_RIGHT_ARM_POSE = (Rotations) ReflectUtil.getFieldAndAccessible(ArmorStand.class,"DEFAULT_RIGHT_ARM_POSE").get(null);
            DEFAULT_LEFT_LEG_POSE = (Rotations) ReflectUtil.getFieldAndAccessible(ArmorStand.class,"DEFAULT_LEFT_LEG_POSE").get(null);
            DEFAULT_RIGHT_LEG_POSE = (Rotations) ReflectUtil.getFieldAndAccessible(ArmorStand.class,"DEFAULT_RIGHT_LEG_POSE").get(null);
        }catch (IllegalAccessException | NoSuchFieldException e){
            throw new RuntimeException(e);
        }

    }

    public ArmorStandWrapper(Location location,String customName) {
        super(NMSUtils.getEntityType(ArmorStand.class));
        this.location = location;
        this.customName = customName;
        getDataWatcherBuilder()
                .define(DATA_CLIENT_FLAGS,(byte) 0)
                .define(DATA_HEAD_POSE,DEFAULT_HEAD_POSE)
                .define(DATA_BODY_POSE,DEFAULT_BODY_POSE)
                .define(DATA_LEFT_ARM_POSE,DEFAULT_LEFT_ARM_POSE)
                .define(DATA_RIGHT_ARM_POSE,DEFAULT_RIGHT_ARM_POSE)
                .define(DATA_LEFT_LEG_POSE,DEFAULT_LEFT_LEG_POSE)
                .define(DATA_RIGHT_LEG_POSE,DEFAULT_RIGHT_LEG_POSE);
        initDataWatcher();
    }


    public void setMarker(boolean marker) {
        super.getDataWatcher().set(DATA_CLIENT_FLAGS,this.setBit(super.getDataWatcher().get(DATA_CLIENT_FLAGS),16,marker));
    }

    public byte setBit(byte value,int bitField,boolean set) {
        if (set){
            value = (byte) (value | bitField);
        } else {
            value = (byte) (value & ~bitField);
        }

        return value;
    }


    @Override
    public void initDataWatcher() {
        super.initDataWatcher();
    }
}
