package cn.whiteg.rpgArmour.utils;

import cn.whiteg.mmocore.reflection.FieldAccessor;
import cn.whiteg.mmocore.reflection.ReflectUtil;
import cn.whiteg.mmocore.util.NMSUtils;
import cn.whiteg.moepacketapi.utils.EntityNetUtils;
import io.netty.util.collection.IntObjectHashMap;
import net.minecraft.network.Connection;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrowableProjectile;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;


@SuppressWarnings("CallToPrintStackTrace")
public class EntityUtils {
    public static Field boundingBoxField;
    public static Field sizeField;
    public static Field goalTargetField;
    public static FieldAccessor<Integer> fieldArmorStandDisabledSlots;
    private static FieldAccessor<Boolean> jump;
    private static FieldAccessor<Float> inputX;
    private static FieldAccessor<Float> inputY;
    private static FieldAccessor<Float> inputZ;
    private static FieldAccessor<Integer> itemUseTimeLeftField;
    private static FieldAccessor<ServerGamePacketListenerImpl> playerConnectionField;
    private static FieldAccessor<ServerPlayerGameMode> playerInteractManagerField;
    private static FieldAccessor<Integer> playerPrepTime;

    private static FieldAccessor<Object> entityYaw;
    private static FieldAccessor<Object> entityPitch;

    private static EntityDataAccessor<net.minecraft.world.item.ItemStack> ThrowableItemKey;  //用于操作投掷物物品堆的Key

    static {
        Field[] fields;
        fields = Entity.class.getDeclaredFields();
        //碰撞箱
        for (Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers()) && field.getType().equals(AABB.class)){
                boundingBoxField = field;
                boundingBoxField.setAccessible(true);
                break;
            }
        }

        //大小
        try{
            sizeField = ReflectUtil.getFieldFormType(Entity.class,EntityDimensions.class);
            sizeField.setAccessible(true);
        }catch (NoSuchFieldException e){
            e.printStackTrace();
        }

        //仇恨目标
        try{
            goalTargetField = ReflectUtil.getFieldFormType(Mob.class,LivingEntity.class);
            goalTargetField.setAccessible(true);
        }catch (NoSuchFieldException e){
            e.printStackTrace();
        }

        //网络协议控制器
        try{
            playerConnectionField = new FieldAccessor<>(ReflectUtil.getFieldFormType(ServerPlayer.class,ServerGamePacketListenerImpl.class));
            playerInteractManagerField = new FieldAccessor<>(ReflectUtil.getFieldFormType(ServerPlayer.class,ServerPlayerGameMode.class));
        }catch (NoSuchFieldException e){
            e.printStackTrace();
        }

        try{
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
            fieldArmorStandDisabledSlots = new FieldAccessor<>(net.minecraft.world.entity.decoration.ArmorStand.class.getDeclaredField("disabledSlots"));

            //获取实体物品使用时间
        /*
         * from line 135
           protected ItemStack activeItem;
           protected int bd; <--
           protected int be;
     */
            itemUseTimeLeftField = new FieldAccessor<>(LivingEntity.class.getDeclaredField("swingTime"));
        }catch (NoSuchFieldException e){
            e.printStackTrace();
        }

        try{
            final Field field = ThrowableItemProjectile.class.getDeclaredField("DATA_ITEM_STACK");
            field.setAccessible(true);
            //noinspection unchecked
            ThrowableItemKey = (EntityDataAccessor<net.minecraft.world.item.ItemStack>) field.get(null);
        }catch (IllegalAccessException | NoSuchFieldException e){
            e.printStackTrace();
        }


        //获取实体在骑乘中的操作
        try{
            var result = ReflectUtil.getFieldFormStructure(LivingEntity.class,boolean.class,float.class,float.class,float.class);
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
            var result = ReflectUtil.getFieldFormStructure(Entity.class,Vec3.class,float.class,float.class,float.class,float.class);
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
//            playerPrepTime = new FieldAccessor<>(ReflectUtil.getFieldFormStructure(LivingEntity.class,
//                    float.class,
//                    float.class,
//                    int.class,
//                    WalkAnimationState.class)[2]);
            playerPrepTime = new FieldAccessor<>(LivingEntity.class.getDeclaredField("attackStrengthTicker"));
        }catch (NoSuchFieldException e){
            e.printStackTrace();
        }
    }

    public static void setEntityDimensions(org.bukkit.entity.Entity entity,float width,float height,boolean update) {
        setEntityDimensions(entity,width,height,height,update);
    }

    public static void setEntityDimensions(org.bukkit.entity.Entity entity,float width,float height,float eye_height,boolean update) {
        try{
            EntityDimensions size = new EntityDimensions(width,height,eye_height,EntityAttachments.createDefault(width,height),true);
            Entity nmsEntity = getNmsEntity(entity);
            sizeField.set(nmsEntity,size);
            /*
            public void i_() {
                EntityDimensions entitysize = this.be;
                EntityPose entitypose = this.al();
                EntityDimensions entitysize1 = this.a(entitypose);
                this.be = entitysize1;
                this.bf = this.a(entitypose, entitysize1);
                this.an();
                boolean flag = (double)entitysize1.a <= 4.0 && (double)entitysize1.b <= 4.0;
                if (!this.H.B && !this.al && !this.ae && flag && (entitysize1.a > entitysize.a || entitysize1.b > entitysize.b) && !(this instanceof EntityHuman)) {
                    Vec3 vec3d = this.de().b(0.0, (double)entitysize.b / 2.0, 0.0);
                    double d0 = (double)Math.max(0.0F, entitysize1.a - entitysize.a) + 1.0E-6;
                    double d1 = (double)Math.max(0.0F, entitysize1.b - entitysize.b) + 1.0E-6;
                    VoxelShape voxelshape = VoxelShapes.a(AABB.a(vec3d, d0, d1, d0));
                    this.H.a(this, voxelshape, vec3d, (double)entitysize1.a, (double)entitysize1.b, (double)entitysize1.a).ifPresent((vec3d1) -> {
                        this.a(vec3d1.b(0.0, (double)(-entitysize1.b) / 2.0, 0.0));
                    });
                }

            }
             */
            //这个方法还是直接去Entity.class里搜索EntityDimensions关键词吧
            if (update) nmsEntity.refreshDimensions();
        }catch (IllegalAccessException e){
            e.printStackTrace();
        }
    }

    public static void setBoundingBox(org.bukkit.entity.Entity entity,BoundingBox boundingBox) {
        Entity ent = getNmsEntity(entity);
        AABB bb = new AABB(boundingBox.getMinX(),boundingBox.getMinY(),boundingBox.getMinZ(),boundingBox.getMaxX(),boundingBox.getMaxY(),boundingBox.getMaxZ());
        ent.setBoundingBox(bb);
    }

    //设置实体是否要跳跃
    public static void setJumping(org.bukkit.entity.LivingEntity entity,boolean jumpin) {
        jump.set(getNmsEntity(entity),jumpin);
    }

    public static boolean getJumping(org.bukkit.entity.LivingEntity entity) {
        return jump.get(getNmsEntity(entity));
    }

    //获取玩家控制坐骑的平行X轴(左右
    public static float getInputX(org.bukkit.entity.LivingEntity entity) {
        return inputX.get(getNmsEntity(entity));
    }

    //获取玩家控制坐骑的前后Z轴(前后
    public static float getInputZ(org.bukkit.entity.LivingEntity entity) {
        return inputZ.get(getNmsEntity(entity));
    }

    //获取玩家控制坐骑的前后Y轴(意味不明
    public static float getInputY(org.bukkit.entity.LivingEntity entity) {
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
    public static boolean setGoalTarget(org.bukkit.entity.Mob entity,org.bukkit.entity.LivingEntity goalTarget) {
        Mob e = (Mob) getNmsEntity(entity);
        LivingEntity t = (LivingEntity) getNmsEntity(goalTarget);
        try{
            goalTargetField.set(e,t);
            return true;
        }catch (IllegalAccessException ex){
            ex.printStackTrace();
            return false;
        }
    }

    //获取实体目标
    public static org.bukkit.entity.Entity getGoalTarget(org.bukkit.entity.Mob entity) {
        return entity.getTarget();
//        Mob e = ((CraftMob) entity).getHandle();
//        return e.getGoalTarget().getBukkitEntity();
    }

    //获取雪球的Item对象
    public static ItemStack getSnowballItem(ThrowableProjectile snowball) {
        net.minecraft.world.item.ItemStack item = getNmsEntity(snowball).getEntityData().get(ThrowableItemKey);
        return CraftItemStack.asBukkitCopy(item);
    }

    public static void setSnowballItem(ThrowableProjectile snowball,ItemStack itemStack) {
        net.minecraft.world.item.ItemStack item = CraftItemStack.asNMSCopy(itemStack);
        getNmsEntity(snowball).getEntityData().set(ThrowableItemKey,item);
    }


    //实体是否为刷怪笼刷出
    public static boolean isSpawner(org.bukkit.entity.Entity entity) {
        return entity.fromMobSpawner() || entity.getEntitySpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER_EGG;  //Paper方法
    }

    //盔甲架是否锁住
    private static int getDisabledSlots(ArmorStand as) {
        net.minecraft.world.entity.decoration.ArmorStand armorStand = (net.minecraft.world.entity.decoration.ArmorStand) EntityUtils.getNmsEntity(as);
        return (int) fieldArmorStandDisabledSlots.get(armorStand);
    }

    //锁住盔甲架
    public static void setSlotsDisabled(ArmorStand as,boolean slotsDisabled) {
        net.minecraft.world.entity.decoration.ArmorStand armorStand = (net.minecraft.world.entity.decoration.ArmorStand) EntityUtils.getNmsEntity(as);
        fieldArmorStandDisabledSlots.set(armorStand,slotsDisabled ? 0xFFFFFF : 0);
    }

    //获取实体物品使用时间
    public static int getItemUseTimeLeft(org.bukkit.entity.LivingEntity entity) {
        LivingEntity nmsEntity = (LivingEntity) EntityUtils.getNmsEntity(entity);
        return itemUseTimeLeftField.get(nmsEntity);
    }
    //获取玩家视野内的其他玩家
//    public static Set<ServerServerGamePacketListenerImpl> getVisionPlayers(Player player) {
//        ServerPlayer np = ((CraftPlayer) player).getHandle();
//        var track = np.getWorldServer().getChunkProvider().a.G.get(np.getId());
//        if (track == null) return null;
//        return track.f;
//    }


    public static ServerGamePacketListenerImpl getServerGamePacketListenerImpl(ServerPlayer player) {
        return playerConnectionField.get(player);
    }

    public static ServerPlayerGameMode getServerPlayerGameMode(ServerPlayer player) {
        return playerInteractManagerField.get(player);
    }

    public static Connection getConnection(ServerPlayer player) {
        return EntityNetUtils.getNetWork(EntityNetUtils.getPlayerConnection(player));
    }

    public static int getEntityId(org.bukkit.entity.Entity entity) {
        return entity.getEntityId();
    }

    public static ServerPlayer getNmsPlayer(Player player) {
        return (ServerPlayer) getNmsEntity(player);
    }


    public static net.minecraft.world.entity.Entity getNmsEntity(org.bukkit.entity.Entity entity) {
        try{
            return EntityNetUtils.getNmsEntity(entity);
        }catch (Throwable e){
            throw new RuntimeException(e);
        }
    }


    //    武器准备时间，相当于攻击CD， 如剑的准备时间是15tick
    public static int getPlayerPrepTime(org.bukkit.entity.Entity player) {
        if (player instanceof LivingEntity) return playerPrepTime.get(NMSUtils.getNmsEntity(player));
        return 0;
    }

    public static void setPlayerPrepTime(org.bukkit.entity.Entity player,int cooldown) {
        if (player instanceof LivingEntity) playerPrepTime.set(NMSUtils.getNmsEntity(player),cooldown);
    }

    public static EntityDataAccessor<?>[] getEntityDataWatchesHelper(Class<? extends Entity> clazz) {
        IntObjectHashMap<EntityDataAccessor<?>> map = new IntObjectHashMap<>();
        while (true) {
            if (clazz.equals(Object.class) || clazz == null) break;
            for (Field field : clazz.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) && field.getType().equals(EntityDataAccessor.class)){
                    field.setAccessible(true);
                    try{
                        final EntityDataAccessor<?> dataKey = (EntityDataAccessor<?>) field.get(null);
                        map.put(dataKey.id(),dataKey);
                    }catch (IllegalAccessException e){
                        throw new RuntimeException(e);
                    }
                }
            }
            clazz = (Class<? extends Entity>) clazz.getSuperclass();
        }

        EntityDataAccessor[] array = new EntityDataAccessor[map.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = map.get(i);
        }
        return array;
    }
}
