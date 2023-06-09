package cn.whiteg.rpgArmour.mapper;

import cn.whiteg.mmocore.reflection.ReflectUtil;
import cn.whiteg.mmocore.util.NMSUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.World;

import java.lang.reflect.Field;

public class EntityMapper {
    static Field worldField;

    static {
        try{
            worldField = ReflectUtil.getFieldFormType(Entity.class,World.class);
            worldField.setAccessible(true);
        }catch (NoSuchFieldException e){
            throw new RuntimeException(e);
        }
    }

    final Entity entity;

    public EntityMapper(org.bukkit.entity.Entity entity) {
        this.entity = getNmsEntity(entity);
    }

    public EntityMapper(Entity entity) {
        this.entity = entity;
    }

    public static Entity getNmsEntity(org.bukkit.entity.Entity bukkitEntity) {
        return NMSUtils.getNmsEntity(bukkitEntity);
    }

    public Entity getEntity() {
        return entity;
    }

    public World getWorld() {
        try{
            return (World) worldField.get(entity);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
