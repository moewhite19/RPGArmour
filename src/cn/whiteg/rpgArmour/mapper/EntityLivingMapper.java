package cn.whiteg.rpgArmour.mapper;

import org.bukkit.entity.Entity;

public class EntityLivingMapper extends EntityMapper {
    public EntityLivingMapper(Entity entity) {
        super(entity);
    }

    public EntityLivingMapper(net.minecraft.world.entity.Entity entity) {
        super(entity);
    }


}
