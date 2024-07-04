package cn.whiteg.rpgArmour.entityWrapper;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.ItemStack;
import org.yaml.snakeyaml.events.Event;

import java.lang.reflect.Field;

public abstract class ArrowWrapper extends ThrowableItemWrapper {
    public static final EntityDataAccessor<Integer> ID_EFFECT_COLOR;
    private static final EntityDataAccessor<Byte> ID_FLAGS;
    private static final EntityDataAccessor<Byte> PIERCE_LEVEL;

    static {
        try{
            Field field = Arrow.class.getDeclaredField("ID_EFFECT_COLOR");
            field.setAccessible(true);
            //noinspection unchecked
            ID_EFFECT_COLOR = (EntityDataAccessor<Integer>) field.get(null);

            field = AbstractArrow.class.getDeclaredField("ID_FLAGS");
            field.setAccessible(true);
            //noinspection unchecked
            ID_FLAGS = (EntityDataAccessor<Byte>) field.get(null);

            field = AbstractArrow.class.getDeclaredField("PIERCE_LEVEL");
            field.setAccessible(true);
            //noinspection unchecked
            PIERCE_LEVEL = (EntityDataAccessor<Byte>) field.get(null);
        }catch (NoSuchFieldException | IllegalAccessException e){
            throw new RuntimeException(e);
        }

    }

    public ArrowWrapper(EntityType<? extends Entity> entityType) {
        super(entityType);
        getDataWatcherBuilder().define(ID_EFFECT_COLOR,-1)
                .define(ID_FLAGS,(byte) 0)
                .define(PIERCE_LEVEL,(byte) 0);
    }
}
