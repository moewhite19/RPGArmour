package cn.whiteg.rpgArmour.utils;

import cn.whiteg.mmocore.reflection.FieldAccessor;
import cn.whiteg.mmocore.reflection.ReflectUtil;
import cn.whiteg.mmocore.util.NMSUtils;
import cn.whiteg.moepacketapi.utils.EntityNetUtils;
import cn.whiteg.moepacketapi.utils.MethodInvoker;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.PlayerInteractManager;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import net.minecraft.world.entity.projectile.EntityProjectileThrowable;
import net.minecraft.world.entity.projectile.EntitySnowball;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class EntityUtils {
    public static Field boundingBoxField;
    public static Field sizeField;
    public static Field goalTargetField;
    public static Field fieldArmorStandDisabledSlots;
    public static MethodInvoker<net.minecraft.world.item.ItemStack> getItemMethod;
    private static FieldAccessor<Boolean> jump;
    private static FieldAccessor<Float> inputX;
    private static FieldAccessor<Float> inputY;
    private static FieldAccessor<Float> inputZ;
    private static FieldAccessor<Integer> itemUseTimeLeftField;
    private static FieldAccessor<PlayerConnection> playerConnectionField;
    private static FieldAccessor<PlayerInteractManager> playerInteractManagerField;
    private static FieldAccessor<Integer> playerPrepTime;

    private static FieldAccessor<Object> entityYaw;
    private static FieldAccessor<Object> entityPitch;

    static {
        Field[] fields;
        fields = Entity.class.getDeclaredFields();
        //碰撞箱
        for (Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers()) && field.getType().equals(AxisAlignedBB.class)){
                boundingBoxField = field;
                boundingBoxField.setAccessible(true);
                break;
            }
        }

        //大小
        try{
            sizeField = ReflectUtil.getFieldFormType(Entity.class,EntitySize.class);
            sizeField.setAccessible(true);
        }catch (NoSuchFieldException e){
            e.printStackTrace();
        }

        //仇恨目标
        try{
            goalTargetField = ReflectUtil.getFieldFormType(EntityInsentient.class,EntityLiving.class);
            goalTargetField.setAccessible(true);
        }catch (NoSuchFieldException e){
            e.printStackTrace();
        }

        //网络协议控制器
        try{
            playerConnectionField = new FieldAccessor<>(ReflectUtil.getFieldFormType(EntityPlayer.class,PlayerConnection.class));
            playerInteractManagerField = new FieldAccessor<>(ReflectUtil.getFieldFormType(EntityPlayer.class,PlayerInteractManager.class));
        }catch (NoSuchFieldException e){
            e.printStackTrace();
        }

        //盔甲架是否锁住
        /*
* from line 39
private final NonNullList<ItemStack> handItems;
private final NonNullList<ItemStack> armorItems;
private boolean armorStandInvisible;
public long bi;
public int disabledSlots; <---
public Vector3f headPose;
public Vector3f bodyPose;
        */
        fields = EntityArmorStand.class.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            var field = fields[i];
            if (field.getType().equals(long.class)){
                fieldArmorStandDisabledSlots = fields[i + 1];
                fieldArmorStandDisabledSlots.setAccessible(true);
                break;
            }
        }


        //获取实体物品使用时间
        /*
         * from line 135
           protected ItemStack activeItem;
           protected int bd; <--
           protected int be;
     */
        fields = EntityLiving.class.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            var field = fields[i];
            if (field.getType().equals(net.minecraft.world.item.ItemStack.class)){
                itemUseTimeLeftField = new FieldAccessor<>(fields[i + 1]);
                break;
            }
        }

        //获取雪球的物品堆
        findMethod:
        {
            for (Method method : EntityProjectileThrowable.class.getDeclaredMethods()) {
                if (method.getReturnType().isAssignableFrom(net.minecraft.world.item.ItemStack.class)){
                    getItemMethod = new MethodInvoker<>(method);
                    break findMethod;
                }
            }
            throw new RuntimeException("Cant find method: getItemMethod");
        }

        //获取实体在骑乘中的操作
        try{
            var result = ReflectUtil.getFieldFormStructure(EntityLiving.class,boolean.class,float.class,float.class,float.class);
            for (Field field : result) {
                field.setAccessible(true);
            }
            jump = new FieldAccessor<>(result[0]);
            inputX = new FieldAccessor<>(result[1]);
            inputY = new FieldAccessor<>(result[2]);
            inputZ = new FieldAccessor<>(result[3]);
        }catch (NoSuchFieldException e){
            e.printStackTrace();
//            throw new IllegalArgumentException("搜索不到方法" + Arrays.toString(fields));
        }
        //获取实体的Pitch和Yaw
        try{
            var result = ReflectUtil.getFieldFormStructure(Entity.class,Vec3D.class,float.class,float.class,float.class,float.class);
            for (Field field : result) {
                field.setAccessible(true);
            }
            entityYaw = new FieldAccessor<>(result[1]);
            entityPitch = new FieldAccessor<>(result[2]);
        }catch (NoSuchFieldException e){
            e.printStackTrace();
//            throw new IllegalArgumentException("搜索不到方法" + Arrays.toString(fields));
        }


        //protected int aO;
        try{
            playerPrepTime = new FieldAccessor<>(ReflectUtil.getFieldFormStructure(EntityLiving.class,
                    float.class,
                    float.class,
                    int.class,
                    WalkAnimationState.class)[2]);
        }catch (NoSuchFieldException e){
            e.printStackTrace();
        }
    }


    public static void setEntitySize(org.bukkit.entity.Entity entity,float width,float height,boolean update) {
        try{
            EntitySize size = new EntitySize(width,height,true);
            Entity nmsEntity = getNmsEntity(entity);
            sizeField.set(nmsEntity,size);
            /*
            public void i_() {
                EntitySize entitysize = this.be;
                EntityPose entitypose = this.al();
                EntitySize entitysize1 = this.a(entitypose);
                this.be = entitysize1;
                this.bf = this.a(entitypose, entitysize1);
                this.an();
                boolean flag = (double)entitysize1.a <= 4.0 && (double)entitysize1.b <= 4.0;
                if (!this.H.B && !this.al && !this.ae && flag && (entitysize1.a > entitysize.a || entitysize1.b > entitysize.b) && !(this instanceof EntityHuman)) {
                    Vec3D vec3d = this.de().b(0.0, (double)entitysize.b / 2.0, 0.0);
                    double d0 = (double)Math.max(0.0F, entitysize1.a - entitysize.a) + 1.0E-6;
                    double d1 = (double)Math.max(0.0F, entitysize1.b - entitysize.b) + 1.0E-6;
                    VoxelShape voxelshape = VoxelShapes.a(AxisAlignedBB.a(vec3d, d0, d1, d0));
                    this.H.a(this, voxelshape, vec3d, (double)entitysize1.a, (double)entitysize1.b, (double)entitysize1.a).ifPresent((vec3d1) -> {
                        this.a(vec3d1.b(0.0, (double)(-entitysize1.b) / 2.0, 0.0));
                    });
                }

            }
             */
            //这个方法还是直接去Entity.class里搜索EntitySize关键词吧
            if (update) nmsEntity.i_();
        }catch (IllegalAccessException e){
            e.printStackTrace();
        }
    }

    public static void setBoundingBox(org.bukkit.entity.Entity entity,BoundingBox boundingBox) {
        Entity ent = getNmsEntity(entity);
        AxisAlignedBB bb = new AxisAlignedBB(boundingBox.getMinX(),boundingBox.getMinY(),boundingBox.getMinZ(),boundingBox.getMaxX(),boundingBox.getMaxY(),boundingBox.getMaxZ());
        ent.a(bb);
    }

    //设置实体是否要跳跃
    public static void setJumping(LivingEntity entity,boolean jumpin) {
        jump.set(getNmsEntity(entity),jumpin);
    }

    public static boolean getJumping(LivingEntity entity) {
        return jump.get(getNmsEntity(entity));
    }

    //获取玩家控制坐骑的平行X轴(左右
    public static float getInputX(LivingEntity entity) {
        return inputX.get(getNmsEntity(entity));
    }

    //获取玩家控制坐骑的前后Z轴(前后
    public static float getInputZ(LivingEntity entity) {
        return inputZ.get(getNmsEntity(entity));
    }

    //获取玩家控制坐骑的前后Y轴(意味不明
    public static float getInputY(LivingEntity entity) {
        return inputY.get(getNmsEntity(entity));
    }

    /*
    控制实体视角轴
     */
    public static float getEntityRotYaw(org.bukkit.entity.Entity entity) {
        return (float) entityYaw.get(getNmsEntity(entity));
    }

    public static float getEntityRotPitch(org.bukkit.entity.Entity entity) {
        return (float) entityPitch.get(getNmsEntity(entity));
    }

    public static void setEntityRotYaw(org.bukkit.entity.Entity entity,float f) {
        entityYaw.set(getNmsEntity(entity),f);
    }

    public static void setEntityRotPitch(org.bukkit.entity.Entity entity,float f) {
        entityPitch.set(getNmsEntity(entity),f);
    }


    //设置实体目标
    public static boolean setGoalTarget(Mob entity,LivingEntity goalTarget) {
        EntityInsentient e = (EntityInsentient) getNmsEntity(entity);
        EntityLiving t = (EntityLiving) getNmsEntity(goalTarget);
        try{
            goalTargetField.set(e,t);
            return true;
        }catch (IllegalAccessException ex){
            ex.printStackTrace();
            return false;
        }
    }

    //获取实体目标
    public static org.bukkit.entity.Entity getGoalTarget(Mob entity) {
        return entity.getTarget();
//        EntityInsentient e = ((CraftMob) entity).getHandle();
//        return e.getGoalTarget().getBukkitEntity();
    }

    //获取雪球的Item对象
    public static ItemStack getSnowballItem(Snowball snowball) {
        EntitySnowball nms = (EntitySnowball) getNmsEntity(snowball);
        return CraftItemStack.asBukkitCopy(getItemMethod.invoke(nms));
    }

    //实体是否为刷怪笼刷出
    public static boolean isSpawner(org.bukkit.entity.Entity entity) {
        return entity.fromMobSpawner() || entity.getEntitySpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER_EGG;  //Paper方法
    }

    //盔甲架是否锁住
    private static int getDisabledSlots(ArmorStand as) {
        EntityArmorStand armorStand = (EntityArmorStand) EntityUtils.getNmsEntity(as);
        try{
            return (int) fieldArmorStandDisabledSlots.get(armorStand);
        }catch (IllegalAccessException e){
            e.printStackTrace();
        }
        return 0;
    }

    //锁住盔甲架
    public static void setSlotsDisabled(ArmorStand as,boolean slotsDisabled) {
        EntityArmorStand armorStand = (EntityArmorStand) EntityUtils.getNmsEntity(as);
        try{
            fieldArmorStandDisabledSlots.set(armorStand,slotsDisabled ? 0xFFFFFF : 0);
        }catch (IllegalAccessException e){
            e.printStackTrace();
        }
    }

    //获取实体物品使用时间
    public static int getItemUseTimeLeft(LivingEntity entity) {
        EntityLiving nmsEntity = (EntityLiving) EntityUtils.getNmsEntity(entity);
        return itemUseTimeLeftField.get(nmsEntity);
    }

    //获取玩家视野内的其他玩家
//    public static Set<ServerPlayerConnection> getVisionPlayers(Player player) {
//        EntityPlayer np = ((CraftPlayer) player).getHandle();
//        var track = np.getWorldServer().getChunkProvider().a.G.get(np.getId());
//        if (track == null) return null;
//        return track.f;
//    }

    //根据实体类获取EntityType
    public static <T extends Entity> EntityTypes<T> getEntityType(Class<T> clazz) {
        String name = EntityTypes.class.getName().concat("<").concat(clazz.getName()).concat(">");
        for (Field field : EntityTypes.class.getFields()) {
            try{
                if (field.getAnnotatedType().getType().getTypeName().equals(name))
                    //noinspection unchecked
                    return (EntityTypes<T>) field.get(null);
            }catch (IllegalAccessException e){
                e.printStackTrace();
            }
        }
        return null;
    }


    public static PlayerConnection getPlayerConnection(EntityPlayer player) {
        return playerConnectionField.get(player);
    }

    public static PlayerInteractManager getPlayerInteractManager(EntityPlayer player) {
        return playerInteractManagerField.get(player);
    }

    public static NetworkManager getNetworkManager(EntityPlayer player) {
        return EntityNetUtils.getNetWork(EntityNetUtils.getPlayerConnection(player));
    }

    public static int getEntityId(org.bukkit.entity.Entity entity) {
        return entity.getEntityId();
    }

    public static EntityPlayer getNmsPlayer(Player player) {
        return (EntityPlayer) getNmsEntity(player);
    }


    public static net.minecraft.world.entity.Entity getNmsEntity(org.bukkit.entity.Entity entity) {
        try{
            return EntityNetUtils.getNmsEntity(entity);
        }catch (Throwable e){
            throw new RuntimeException(e);
        }
    }


    //    武器准备时间，相当于攻击CD， 如剑的准备时间是15tick
    public static float getPlayerPrepTime(org.bukkit.entity.Entity player) {
        if (player instanceof LivingEntity) return playerPrepTime.get(NMSUtils.getNmsEntity(player));
        return 0f;
    }

    public static void setPlayerPrepTime(org.bukkit.entity.Entity player,int cooldown) {
        if (player instanceof LivingEntity) playerPrepTime.set(NMSUtils.getNmsEntity(player),cooldown);
    }
}
