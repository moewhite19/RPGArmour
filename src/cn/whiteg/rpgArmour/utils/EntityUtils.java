package cn.whiteg.rpgArmour.utils;

import net.minecraft.server.v1_15_R1.*;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftMob;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftSnowball;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class EntityUtils {
    public final static Field jumpingField;
    public final static Field boundingBoxField;
    public final static Field sizeField;
    public final static Field goalTargetField;
    public final static Method getItemMethod;


    static {
        Field f;
        try{
            f = EntityLiving.class.getDeclaredField("jumping");
            f.setAccessible(true);
        }catch (NoSuchFieldException e){
            e.printStackTrace();
            f = null;
        }
        jumpingField = f;

        try{
            f = Entity.class.getDeclaredField("boundingBox");
            f.setAccessible(true);
        }catch (NoSuchFieldException e){
            e.printStackTrace();
            f = null;
        }
        boundingBoxField = f;

        try{
            f = Entity.class.getDeclaredField("size");
            f.setAccessible(true);
        }catch (NoSuchFieldException e){
            e.printStackTrace();
            f = null;
        }
        sizeField = f;

        try{
            f = EntityInsentient.class.getDeclaredField("goalTarget");
            f.setAccessible(true);
        }catch (NoSuchFieldException e){
            e.printStackTrace();
            f = null;
        }
        goalTargetField = f;

        Method m;
        try{
            m = EntityProjectileThrowable.class.getDeclaredMethod("getItem");
            m.setAccessible(true);
        }catch (NoSuchMethodException e){
            e.printStackTrace();
            m = null;
        }
        getItemMethod = m;
    }

    public static void setEntitySize(org.bukkit.entity.Entity entity,float width,float height,boolean update) {
        try{
            EntitySize size = new EntitySize(width,height,true);
            Entity ent = ((CraftEntity) entity).getHandle();
            sizeField.set(ent,size);
            if (update) ent.updateSize();
        }catch (IllegalAccessException e){
            e.printStackTrace();
        }
    }

    public static void setBoundingBox(org.bukkit.entity.Entity entity,BoundingBox boundingBox) {
        try{
            Entity ent = ((CraftEntity) entity).getHandle();
            AxisAlignedBB bb = new AxisAlignedBB(boundingBox.getMinX(),boundingBox.getMinY(),boundingBox.getMinZ(),boundingBox.getMaxX(),boundingBox.getMaxY(),boundingBox.getMaxZ());
            boundingBoxField.set(ent,bb);
//            ent.updateSize();
        }catch (IllegalAccessException e){
            e.printStackTrace();
        }
    }

    public static void setJumping(LivingEntity entity,boolean jump) {
        EntityLiving e = ((CraftLivingEntity) entity).getHandle();
        try{
            jumpingField.set(e,jump);
        }catch (IllegalAccessException ex){
            ex.printStackTrace();
        }
    }

    public static boolean getJumping(LivingEntity entity) {
        EntityLiving e = ((CraftLivingEntity) entity).getHandle();
        try{
            return jumpingField.getBoolean(e);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    public static float getAD(LivingEntity entity) {
        EntityLiving e = ((CraftLivingEntity) entity).getHandle();
        return e.aZ;
    }

    public static float getWS(LivingEntity entity) {
        EntityLiving e = ((CraftLivingEntity) entity).getHandle();
        return e.bb;
    }

    public static boolean setGoalTarget(Mob entity,LivingEntity goalTarget) {
        EntityInsentient e = ((CraftMob) entity).getHandle();
        EntityLiving t = ((CraftLivingEntity) goalTarget).getHandle();
        try{
            goalTargetField.set(e,t);
            return true;
        }catch (IllegalAccessException ex){
            ex.printStackTrace();
            return false;
        }
    }
//
//    public static Object getGoalTarget(Mob entity) {
//        EntityInsentient e = ((CraftMob) entity).getHandle();
//        try{
//            e = goalTargetField.get(e);
//            return
//        }catch (IllegalAccessException ex){
//            ex.printStackTrace();
//            return false;
//        }
//    }

    public static ItemStack getSnowballItem(Snowball snowball) {
        EntitySnowball nms = ((CraftSnowball) snowball).getHandle();
        try{
            return CraftItemStack.asBukkitCopy((net.minecraft.server.v1_15_R1.ItemStack) getItemMethod.invoke(nms));
        }catch (IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
        }
        return null;
    }
}
