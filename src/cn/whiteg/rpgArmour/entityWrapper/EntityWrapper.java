package cn.whiteg.rpgArmour.entityWrapper;

import cn.whiteg.mmocore.reflection.FieldAccessor;
import cn.whiteg.mmocore.reflection.ReflectUtil;
import cn.whiteg.mmocore.reflection.ReflectionFactory;
import cn.whiteg.mmocore.util.NMSUtils;
import cn.whiteg.moepacketapi.utils.MethodInvoker;
import cn.whiteg.rpgArmour.RPGArmour;
import cn.whiteg.rpgArmour.utils.EntityUtils;
import cn.whiteg.rpgArmour.utils.PacketUnit;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SyncedDataHolder;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.ClassTreeIdRegistry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.craftbukkit.util.CraftVector;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
public abstract class EntityWrapper implements SyncedDataHolder {
    static FieldAccessor<Boolean> data_b1;
//    static FieldAccessor<Integer> PacketPassengersVehicleId;
//    static FieldAccessor<int[]> PacketPassengersArrays;


    static MethodInvoker<SynchedEntityData.DataItem<?>> dataWatcher_getItem;

    static EntityDataAccessor<Byte> DATA_SHARED_FLAGS_ID;
    static EntityDataAccessor<Boolean> DATA_CUSTOM_NAME_VISIBLE;
    static EntityDataAccessor<Boolean> DATA_NO_GRAVITY;
    static EntityDataAccessor<Integer> DATA_AIR_SUPPLY_ID;
    static EntityDataAccessor<Optional<Component>> DATA_CUSTOM_NAME;
    static EntityDataAccessor<Boolean> DATA_SILENT;
    static EntityDataAccessor<Pose> DATA_POSE;
    static EntityDataAccessor<Integer> DATA_TICKS_FROZEN;
    SynchedEntityData.Builder dataWatcherBuilder;
    public final static AtomicInteger ENTITY_COUNT;


    static FieldAccessor<SynchedEntityData.DataItem<?>[]> dataWatchBuilerById;
    static ClassTreeIdRegistry ID_REGISTRY;


    static {
        try{
            data_b1 = (FieldAccessor<Boolean>) ReflectionFactory.createFieldAccessor(ReflectUtil.getFieldFormType(SynchedEntityData.DataItem.class,boolean.class)); //change???

            try{
//                dataWatcher_getItem = new MethodInvoker<>(DataWatcher.class.getDeclaredMethod("c",EntityDataAccessor.class));
                findMethod:
                {
                    for (Method method : SynchedEntityData.class.getDeclaredMethods()) {
                        final Class<?>[] types = method.getParameterTypes();
                        if (types.length == 1 && EntityDataAccessor.class.isAssignableFrom(types[0]) && SynchedEntityData.DataItem.class.isAssignableFrom(method.getReturnType())){
                            dataWatcher_getItem = new MethodInvoker<>(method);
                            break findMethod;
                        }
                    }
                    throw new NoSuchMethodException();
                }

                dataWatchBuilerById = new FieldAccessor<>(SynchedEntityData.Builder.class.getDeclaredField("itemsById"));
                final Field field = SynchedEntityData.class.getDeclaredField("ID_REGISTRY");
                field.setAccessible(true);
                ID_REGISTRY = (ClassTreeIdRegistry) field.get(null);

//                PacketPassengersVehicleId = new FieldAccessor<>(ClientboundSetPassengersPacket.class.getDeclaredField("vehicle"));
//                PacketPassengersArrays = new FieldAccessor<>(ClientboundSetPassengersPacket.class.getDeclaredField("passengers"));
            }catch (NoSuchMethodException e){
                e.printStackTrace();
            }

            Objects.requireNonNull(dataWatcher_getItem);

            DATA_SHARED_FLAGS_ID = (EntityDataAccessor<Byte>) ReflectUtil.getFieldAndAccessible(Entity.class,"DATA_SHARED_FLAGS_ID").get(null);
            DATA_AIR_SUPPLY_ID = (EntityDataAccessor<Integer>) ReflectUtil.getFieldAndAccessible(Entity.class,"DATA_AIR_SUPPLY_ID").get(null);
            DATA_CUSTOM_NAME_VISIBLE = (EntityDataAccessor<Boolean>) ReflectUtil.getFieldAndAccessible(Entity.class,"DATA_CUSTOM_NAME_VISIBLE").get(null);
            DATA_SILENT = (EntityDataAccessor<Boolean>) ReflectUtil.getFieldAndAccessible(Entity.class,"DATA_SILENT").get(null);
            DATA_NO_GRAVITY = (EntityDataAccessor<Boolean>) ReflectUtil.getFieldAndAccessible(Entity.class,"DATA_NO_GRAVITY").get(null);
            DATA_CUSTOM_NAME = (EntityDataAccessor<Optional<Component>>) ReflectUtil.getFieldAndAccessible(Entity.class,"DATA_CUSTOM_NAME").get(null);
            DATA_POSE = (EntityDataAccessor<Pose>) ReflectUtil.getFieldAndAccessible(Entity.class,"DATA_POSE").get(null);
            DATA_TICKS_FROZEN = (EntityDataAccessor<Integer>) ReflectUtil.getFieldAndAccessible(Entity.class,"DATA_TICKS_FROZEN").get(null);


            //AtomicInteger
            Field count_f = ReflectUtil.getFieldFormType(Entity.class,AtomicInteger.class);
            count_f.setAccessible(true);
            ENTITY_COUNT = (AtomicInteger) count_f.get(null);
        }catch (NoSuchFieldException | IllegalAccessException e){
            throw new RuntimeException(e);
        }
    }

    final Random random = new Random();
    final UUID uuid;
    final int entityId;
    public SynchedEntityData dataWatcher;
    net.minecraft.world.entity.EntityType<? extends Entity> entityType;
    Collection<Player> canVisble = null;
    Location location;
    String customName;
    Vector vector;


    public EntityWrapper(net.minecraft.world.entity.EntityType<? extends Entity> entityType) {
        this.entityType = entityType;
        this.entityId = ENTITY_COUNT.incrementAndGet();
        uuid = new UUID(random.nextLong(),System.currentTimeMillis());
        dataWatcherBuilder = new SynchedEntityData.Builder(this);
        dataWatchBuilerById.set(dataWatcherBuilder,new SynchedEntityData.DataItem[ID_REGISTRY.getCount(NMSUtils.getEntityClass(entityType))]);
        dataWatcherBuilder
                .define(DATA_SHARED_FLAGS_ID,(byte) 0)
                .define(DATA_AIR_SUPPLY_ID,300)
                .define(DATA_CUSTOM_NAME_VISIBLE,false)
                .define(DATA_CUSTOM_NAME,Optional.empty())
                .define(DATA_SILENT,false)
                .define(DATA_NO_GRAVITY,false)
                .define(DATA_POSE,Pose.STANDING)
                .define(DATA_TICKS_FROZEN,0);

    }

    public static FriendlyByteBuf createDataSerializer() {
        return new FriendlyByteBuf(Unpooled.buffer());
    }

    public void setVisible(Player player,boolean visible) {
        if (location == null || player.getWorld() != location.getWorld()) return;
        if (visible){
            if (canVisble == null) canVisble = new HashSet<>();
            canVisble.add(player);
            spawn(player);
        } else if (canVisble != null){
            remove(player);
            canVisble.remove(player);
        }
    }

    public void playerShow(Collection<Player> players) {
        var p1 = createPacketSpawnEntity();
        Packet<ClientGamePacketListener> p2 = createPacketEntityMetadata();
        for (Player player : players) {
            var np = EntityUtils.getNmsPlayer(player);
            var conn = EntityUtils.getServerGamePacketListenerImpl(np);
            PacketUnit.sendPacket(p1,conn);
            if (p2 != null) PacketUnit.sendPacket(p2,conn);
        }
        setVisble(players);
    }

    public void playersForEach(Consumer<Player> action) {
        if (canVisble == null) return;
        final Iterator<Player> it = canVisble.iterator();
        while (it.hasNext()) {
            final Player player = it.next();
            if (!player.isOnline() || player.getWorld() != location.getWorld()){
                it.remove();
                continue;
            }
            action.accept(player);
        }
    }

    public boolean canVisible(Player p) {
        if (canVisble == null) return false;
        return canVisble.contains(p);
    }

    public EntityType<? extends Entity> getEntityType() {
        return entityType;
    }

    public void setEntityType(EntityType<? extends Entity> entityType) {
        this.entityType = entityType;
    }

    public void spawn(Player player) {
        spawn(EntityUtils.getServerGamePacketListenerImpl(EntityUtils.getNmsPlayer(player)));
    }

    public void spawn(ServerGamePacketListenerImpl pc) {
        var p1 = createPacketSpawnEntity();
        var p2 = createPacketEntityMetadata();
        PacketUnit.sendPacket(p1,pc);
        if (p2 != null) PacketUnit.sendPacket(p2,pc);
    }

    public void remove(Player player) {
        PacketUnit.sendPacket(createPacketEntityDestroy(),player);
    }

    public SynchedEntityData getDataWatcher() {
        if (dataWatcher == null){
            initDataWatcher();
        }
        return dataWatcher;
    }

    public void remove() {
        if (canVisble != null){
            var packet = createPacketEntityDestroy();
            playersForEach(player -> {
                PacketUnit.sendPacket(packet,player);
            });
            canVisble = null;
        }
    }

    public Collection<Player> getVisble() {
        return canVisble;
    }

    public void setVisble(Collection<Player> canVisble) {
        this.canVisble = canVisble;
    }

    public int getEntityId() {
        return entityId;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Location getLocation() {
        return location.clone();
    }

    public void setLocation(Location location) {
        this.location = location;
        if (canVisble != null){
            Packet<ClientGamePacketListener> packet = cratePacketEntityTeleport();
            playersForEach(player -> PacketUnit.sendPacket(packet,player));
        }
    }

    /**
     * Create a {@code PacketPlayOutSpawnEntity} object.
     * Only {@link EntityType#ARMOR_STAND} and {@link EntityType#ITEM} are supported!
     */
    public Packet<ClientGamePacketListener> createPacketSpawnEntity() {
        var v = getVector();
        var mot = v == null ? new Vec3(0,0,0) : new Vec3(v.getX(),v.getY(),v.getZ());
        return new ClientboundAddEntityPacket(entityId,uuid,location.getX(),location.getY(),location.getZ(),location.getPitch(),location.getYaw(),entityType,0,mot,0);
    }

    //创建实体传送包
    public Packet<ClientGamePacketListener> cratePacketEntityTeleport() {
        var buff = createDataSerializer();
        buff.writeInt(this.entityId);
        buff.writeDouble(location.getX());
        buff.writeDouble(location.getY());
        buff.writeDouble(location.getZ());
        buff.writeByte((byte) ((int) (location.getYaw() * 256.0F / 360.0F)));
        buff.writeByte((byte) ((int) (location.getPitch() * 256.0F / 360.0F)));
        buff.writeBoolean(true);
        return ClientboundTeleportEntityPacket.STREAM_CODEC.decode(buff);
    }

    //创建实体骑乘包
    public Packet<ClientGamePacketListener> cratePacketMount(org.bukkit.entity.Entity entity) {
        var passengers = entity.getPassengers();
        ArrayList<Integer> list = new ArrayList<>(passengers.size() + 1);
        for (org.bukkit.entity.Entity passenger : passengers) {
            int id = passenger.getEntityId();
            if (!list.contains(id)) list.add(id);
        }
        if (!list.contains(getEntityId())) list.add(getEntityId());
        int[] array = Arrays.stream(list.toArray(new Integer[0])).mapToInt(Integer::valueOf).toArray();

        final FriendlyByteBuf buff = new FriendlyByteBuf(Unpooled.buffer()).writeInt(entity.getEntityId()).writeVarIntArray(array);
        return ClientboundSetPassengersPacket.STREAM_CODEC.decode(buff);
    }

    public Packet<ClientGamePacketListener> createPacketEntityDestroy() {
        return new ClientboundRemoveEntitiesPacket(entityId);
    }

    @Nullable
    public Packet<ClientGamePacketListener> createPacketEntityMetadata() {
        final List<SynchedEntityData.DataValue<?>> b = getDataWatcher().packAll();
        if (b != null) return new ClientboundSetEntityDataPacket(entityId,b);
        return null;
    }

    public void sendUpdate() {
        if (canVisble != null){
            List<SynchedEntityData.DataValue<?>> list = getDataWatcher().packAll();
            if (list == null) return;
            final ClientboundSetEntityDataPacket p = new ClientboundSetEntityDataPacket(entityId,list);
            playersForEach(player -> {
                PacketUnit.sendPacket(p,player);
            });
        }
    }

    public String getCustomName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
        final Component ibc = Component.literal(customName);
        dataWatcher.set(DATA_CUSTOM_NAME,Optional.of(ibc));
    }

    public void setVector(Player player,Vector v) {
        final ClientboundSetEntityMotionPacket p = new ClientboundSetEntityMotionPacket(entityId,CraftVector.toNMS(v));
        PacketUnit.sendPacket(p,player);
    }

    public Vector getVector() {
        return vector;
    }

    public void setVector(Player player) {
        final ClientboundSetEntityMotionPacket p = new ClientboundSetEntityMotionPacket(entityId,CraftVector.toNMS(vector));
        PacketUnit.sendPacket(p,player);
    }

    public void setVector(Vector v) {
        vector = v;
        if (canVisble != null){
            final ClientboundSetEntityMotionPacket p = new ClientboundSetEntityMotionPacket(entityId,CraftVector.toNMS(v));
            playersForEach(player -> {
                PacketUnit.sendPacket(p,player);
            });
        }
    }

    public void setNoGravity(boolean b) {
        dataWatcher.set(DATA_NO_GRAVITY,b);
    }

    public boolean isNoGravity() {
        return dataWatcher.get(DATA_NO_GRAVITY);
    }

    public void setInvisible(boolean invisible) {
        this.setSharedFlag(5,invisible);
    }

    public boolean getInvisible() {
        return getSharedFlag(5);
    }

    public boolean hasCustomName() {
        return ((Optional) this.dataWatcher.get(DATA_CUSTOM_NAME)).isPresent();
    }

    public void setCustomNameVisible(boolean visible) {
        this.dataWatcher.set(DATA_CUSTOM_NAME_VISIBLE,visible);
    }

    public boolean isCustomNameVisible() {
        return this.dataWatcher.get(DATA_CUSTOM_NAME_VISIBLE);
    }


    public boolean getSharedFlag(int index) {
        return (this.dataWatcher.get(DATA_SHARED_FLAGS_ID) & 1 << index) != 0;
    }

    public void setSharedFlag(int index,boolean value) {
        byte b0 = dataWatcher.get(DATA_SHARED_FLAGS_ID);
        if (value){
            this.dataWatcher.set(DATA_SHARED_FLAGS_ID,(byte) (b0 | 1 << index));
        } else {
            this.dataWatcher.set(DATA_SHARED_FLAGS_ID,(byte) (b0 & ~(1 << index)));
        }
    }

    /**
     * Create a NMS data watcher object to send via a {@code ClientboundSetEntityDataPacket} packet.
     * Gravity will be disabled and the custom name will be displayed if available.
     */
    public void initDataWatcher() {
        if (dataWatcherBuilder != null){
            // 尝试初始化数据观察者
            try{
                dataWatcher = dataWatcherBuilder.build();
                // 清理
                dataWatcherBuilder = null;
            }catch (IllegalStateException e){
                // 如果初始化失败，获取ById的数据观察者数组
                SynchedEntityData.DataItem<?>[] arrays = dataWatchBuilerById.get(dataWatcherBuilder);
                // 打印日志
                RPGArmour.logger.warning("当前参数: " + Arrays.toString(arrays));
                // 获取当前实体类型对应的EntityDataAccessor数组
                final EntityDataAccessor<?>[] helper = EntityUtils.getEntityDataWatchesHelper(NMSUtils.getEntityClass(entityType));
                // 打印日志
                RPGArmour.logger.warning("可用参数: " + Arrays.toString(helper));
                throw e;
            }
        }
    }

    @Deprecated
    public void sendPacket(Packet<?> packet,Player player) {
        PacketUnit.sendPacket(packet,player);
    }


    @Override
    public void onSyncedDataUpdated(List<SynchedEntityData.DataValue<?>> entries) {

    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> data) {

    }

    public SynchedEntityData.Builder getDataWatcherBuilder() {
        if (dataWatcherBuilder == null) throw new IllegalArgumentException("This method can only be used at build");
        return dataWatcherBuilder;
    }
}
