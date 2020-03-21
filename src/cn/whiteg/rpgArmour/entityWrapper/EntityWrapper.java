package cn.whiteg.rpgArmour.entityWrapper;

import cn.whiteg.rpgArmour.utils.EntityUtils;
import cn.whiteg.rpgArmour.utils.NMSUtils;
import cn.whiteg.rpgArmour.utils.Utils;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.phys.Vec3D;
import org.apache.commons.lang.ObjectUtils;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R1.util.CraftVector;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
public abstract class EntityWrapper {
    static Field data_i;
    static Method data_b;
    static DataWatcherObject<Byte> flags;
    static DataWatcherObject<Integer> air_tick;
    static DataWatcherObject<Boolean> name_visible;
    static DataWatcherObject<Boolean> silent;
    static DataWatcherObject<Boolean> noGravity;
    static DataWatcherObject<Optional<IChatBaseComponent>> displayName;
    public final static AtomicInteger ENTITY_COUNT;


    static {
        try{
            data_i = DataWatcher.class.getDeclaredField("i"); //change???
            data_i.setAccessible(true);

            data_b = DataWatcher.class.getDeclaredMethod("b",DataWatcherObject.class); //set???
            data_b.setAccessible(true);
            Field f;
            f = NMSUtils.getFieldFormType(Entity.class,"net.minecraft.network.syncher.DataWatcherObject<java.lang.Byte>"); // flags
            f.setAccessible(true);
            flags = (DataWatcherObject<Byte>) f.get(null);

            f = NMSUtils.getFieldFormType(Entity.class,"net.minecraft.network.syncher.DataWatcherObject<java.lang.Integer>"); //Air Ticks???
            f.setAccessible(true);
            air_tick = (DataWatcherObject<Integer>) f.get(null);

            f = NMSUtils.getFieldFormType(Entity.class,"net.minecraft.network.syncher.DataWatcherObject<java.util.Optional<net.minecraft.network.chat.IChatBaseComponent>>"); // custom name

            f.setAccessible(true);
            displayName = (DataWatcherObject<Optional<IChatBaseComponent>>) f.get(null);

            var fields = Entity.class.getDeclaredFields();
            int end = fields.length - 3;
            loom:
            for (int i = 0; i < end; i++) {
                for (int j = 0; j < 3; j++) {
                    f = fields[i + j];
                    if (!f.getAnnotatedType().getType().getTypeName().equals("net.minecraft.network.syncher.DataWatcherObject<java.lang.Boolean>")){
                        continue loom;
                    }
                }
                f = fields[i]; // custom name visible
                f.setAccessible(true);
                name_visible = (DataWatcherObject<Boolean>) f.get(null);

                f = fields[i + 1]; // silent
                f.setAccessible(true);
                silent = (DataWatcherObject<Boolean>) f.get(null);
                f.setAccessible(true);

                f = fields[i + 2];// no gravitysilent
                f.setAccessible(true);
                noGravity = (DataWatcherObject<Boolean>) f.get(null);
            }

            //AtomicInteger
            Field count_f = NMSUtils.getFieldFormType(Entity.class,AtomicInteger.class);
            count_f.setAccessible(true);
            ENTITY_COUNT = (AtomicInteger) count_f.get(null);
        }catch (NoSuchFieldException | NoSuchMethodException | IllegalAccessException e){
            throw new RuntimeException(e);
        }
    }

    final Random random = new Random();
    final Class<?> packetPlayOutEntityTeleportClass = PacketPlayOutEntityTeleport.class;
    final UUID uuid;
    final int entityId;
    public DataWatcher dataWatcher;
    EntityTypes<? extends Entity> entityType;
    Collection<Player> canVisble = null;
    Location location;
    String customName;
    Vector vector;

    public EntityWrapper(EntityTypes<? extends Entity> entityType) {
        this.entityType = entityType;
        this.entityId = ENTITY_COUNT.incrementAndGet();
        uuid = new UUID(random.nextLong(),System.currentTimeMillis());
    }

    public static PacketDataSerializer createDataSerializer() {
        return new PacketDataSerializer(Unpooled.buffer());
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
        Packet<PacketListenerPlayOut> p2 = createPacketEntityMetadata();
        for (Player player : players) {
            var np = EntityUtils.getNmsPlayer(player);
            var conn = EntityUtils.getPlayerConnection(np);
            conn.a(p1);
            conn.a(p2);
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

    public EntityTypes<? extends Entity> getEntityType() {
        return entityType;
    }

    public void setEntityType(EntityTypes<? extends Entity> entityType) {
        this.entityType = entityType;
    }

    public void spawn(Player player) {
        spawn(EntityUtils.getPlayerConnection(EntityUtils.getNmsPlayer(player)));
    }

    public void spawn(PlayerConnection pc) {
        var p1 = createPacketSpawnEntity();
        var p2 = createPacketEntityMetadata();
        pc.a(p1);
        pc.a(p2);
    }

    public void remove(Player player) {
        Utils.sendPacket(createPacketEntityDestroy(),player);
    }

    public DataWatcher getDataWatcher() {
        if (dataWatcher == null){
            initDataWatcher();
        }
        return dataWatcher;
    }

    public void remove() {
        if (canVisble != null){
            var packet = createPacketEntityDestroy();
            playersForEach(player -> {
                Utils.sendPacket(packet,player);
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
            Packet<PacketListenerPlayOut> packet = cratePacketEntityTeleport();
            playersForEach(player -> Utils.sendPacket(packet,player));
        }
    }

    /**
     * Create a {@code PacketPlayOutSpawnEntity} object.
     * Only {@link EntityType#ARMOR_STAND} and {@link EntityType#DROPPED_ITEM} are supported!
     */
    public Packet<PacketListenerPlayOut> createPacketSpawnEntity() {
        var v = getVector();
        var mot = v == null ? new Vec3D(0,0,0) : new Vec3D(v.getX(),v.getY(),v.getZ());
        return new PacketPlayOutSpawnEntity(entityId,uuid,location.getX(),location.getY(),location.getZ(),location.getPitch(),location.getYaw(),entityType,0,mot,0);
    }

    //创建实体传送包
    public Packet<PacketListenerPlayOut> cratePacketEntityTeleport() {
        var var0 = createDataSerializer();
        var0.d(this.entityId);
        var0.writeDouble(location.getX());
        var0.writeDouble(location.getY());
        var0.writeDouble(location.getZ());
        var0.writeByte((byte) ((int) (location.getYaw() * 256.0F / 360.0F)));
        var0.writeByte((byte) ((int) (location.getPitch() * 256.0F / 360.0F)));
        var0.writeBoolean(true);
        return new PacketPlayOutEntityTeleport(var0);
    }

    //创建实体骑乘包
    public Packet<PacketListenerPlayOut> cratePacketMount(org.bukkit.entity.Entity entity) {
        var passengers = entity.getPassengers();
        ArrayList<Integer> list = new ArrayList<>(passengers.size() + 1);
        for (org.bukkit.entity.Entity passenger : passengers) {
            int id = passenger.getEntityId();
            if (!list.contains(id)) list.add(id);
        }
        if (!list.contains(getEntityId())) list.add(getEntityId());
        int[] array = Arrays.stream(list.toArray(new Integer[0])).mapToInt(Integer::valueOf).toArray();
        return new PacketPlayOutMount(new PacketDataSerializer(Unpooled.buffer()).d(entity.getEntityId()).a(array));
    }

    public Packet<PacketListenerPlayOut> createPacketEntityDestroy() {
        return new PacketPlayOutEntityDestroy(entityId);
    }

    public Packet<PacketListenerPlayOut> createPacketEntityMetadata() {
        return new PacketPlayOutEntityMetadata(entityId,getDataWatcher(),true);
    }

    public void sendUpdate() {
        if (canVisble != null){
            final PacketPlayOutEntityMetadata p = new PacketPlayOutEntityMetadata(entityId,getDataWatcher(),true);
            playersForEach(player -> {
                Utils.sendPacket(p,player);
            });
        }
    }

    public String getCustomName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
        final IChatBaseComponent ibc = IChatBaseComponent.a(customName);
        dataWatcher.b(displayName,Optional.of(ibc));
        sendUpdate();
    }

    public void setVector(Player player,Vector v) {
        final PacketPlayOutEntityVelocity p = new PacketPlayOutEntityVelocity(entityId,CraftVector.toNMS(v));
        Utils.sendPacket(p,player);
    }

    public Vector getVector() {
        return vector;
    }

    public void setVector(Player player) {
        final PacketPlayOutEntityVelocity p = new PacketPlayOutEntityVelocity(entityId,CraftVector.toNMS(vector));
        Utils.sendPacket(p,player);
    }

    public void setVector(Vector v) {
        vector = v;
        if (canVisble != null){
            final PacketPlayOutEntityVelocity p = new PacketPlayOutEntityVelocity(entityId,CraftVector.toNMS(v));
            playersForEach(player -> {
                Utils.sendPacket(p,player);
            });
        }
    }

    public void setNoGravity(boolean b) {
        getDataWatcher().b(noGravity,b);
        sendUpdate();
    }

    public void setCustomNameVisible(boolean b) {
        dataWatcher.b(name_visible,b);
    }

    /**
     * Create a NMS data watcher object to send via a {@code PacketPlayOutEntityMetadata} packet.
     * Gravity will be disabled and the custom name will be displayed if available.
     */
    public void initDataWatcher() {
        byte flags = (byte) (1 << 5);
        dataWatcher = new DataWatcher(null) {
            @Override
            public <T> void b(DataWatcherObject<T> datawatcherobject,T t0) {
                try{
                    Item<T> datawatcher_item = (Item<T>) data_b.invoke(this,datawatcherobject);
                    if (ObjectUtils.notEqual(t0,datawatcher_item.b())){
                        datawatcher_item.a(t0);
                        datawatcher_item.a(true);
                        data_i.set(this,true);
                    }
                }catch (IllegalAccessException | InvocationTargetException e){
                    e.printStackTrace();
                }
            }
        };
        dataWatcher.a(EntityWrapper.flags,flags);  //flags
        dataWatcher.a(air_tick,300);
        dataWatcher.a(name_visible,customName != null); // custname
        dataWatcher.a(silent,true); // silent
        dataWatcher.a(noGravity,false);
        dataWatcher.a(displayName,Optional.empty());
    }

    public void sendPacket(Packet<?> packet,Player player) {
        EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        var playerConnection = EntityUtils.getPlayerConnection(nmsPlayer);
        playerConnection.a(packet);
    }
}
