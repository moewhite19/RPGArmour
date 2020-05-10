package cn.whiteg.rpgArmour.entityWrapper;

import cn.whiteg.rpgArmour.utils.JsonBuilder;
import cn.whiteg.rpgArmour.utils.Utils;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.ObjectUtils;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_15_R1.util.CraftVector;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public abstract class EntityWrapper {


    static Field g;
    static Method b;
    static DataWatcherObject<Byte> flags;
    static DataWatcherObject<Integer> air_tick;
    static DataWatcherObject<Boolean> name_visible;
    static DataWatcherObject<Boolean> silent;
    static DataWatcherObject<Boolean> noGravity;
    static DataWatcherObject<Optional<IChatBaseComponent>> displayName;
    static AtomicInteger entityCount;


    static {
        try{
            g = DataWatcher.class.getDeclaredField("g"); //???
            b = DataWatcher.class.getDeclaredMethod("b",DataWatcherObject.class); //???
            final Class<Entity> entityClass = Entity.class;
            Field W = entityClass.getDeclaredField("T"); // flags
            Field AIR_TICKS = entityClass.getDeclaredField("AIR_TICKS"); //Air Ticks???
            Field aA = entityClass.getDeclaredField("aA"); // custom name visible
            Field az = entityClass.getDeclaredField("az"); // custom name
            Field aB = entityClass.getDeclaredField("aB"); // silent
            Field aC = entityClass.getDeclaredField("aC");// no gravity
            g.setAccessible(true);
            b.setAccessible(true);
            W.setAccessible(true);
            AIR_TICKS.setAccessible(true);
            aA.setAccessible(true);
            az.setAccessible(true);
            aB.setAccessible(true);
            aC.setAccessible(true);
            silent = (DataWatcherObject<Boolean>) aB.get(null);
            name_visible = (DataWatcherObject<Boolean>) aA.get(null);
            flags = (DataWatcherObject<Byte>) W.get(null);
            air_tick = (DataWatcherObject<Integer>) AIR_TICKS.get(null);
            displayName = (DataWatcherObject<Optional<IChatBaseComponent>>) az.get(null);
            noGravity = (DataWatcherObject<Boolean>) aC.get(null);
            Field count_f = Entity.class.getDeclaredField("entityCount");
            count_f.setAccessible(true);
            entityCount = (AtomicInteger) count_f.get(null);
        }catch (NoSuchFieldException e){
            e.printStackTrace();
        }catch (NoSuchMethodException e){
            e.printStackTrace();
        }catch (IllegalAccessException e){
            e.printStackTrace();
        }
    }

    final Random random = new Random();
    final Class<?> packetPlayOutEntityTeleportClass = PacketPlayOutEntityTeleport.class;
    final UUID uuid;
    final int entityId;
    final EntityTypes<? extends Entity> entityType;
    DataWatcher dataWatcher;
    Collection<Player> canVisble = null;
    Location location;
    String customName;
    Vector vector;

    public EntityWrapper(EntityTypes<? extends Entity> entityType) {
        this.entityType = entityType;
        this.entityId = entityCount.incrementAndGet();
        uuid = new UUID(random.nextLong(),System.currentTimeMillis());
    }


    /**
     * Create a {@code PacketPlayOutSpawnEntity} object.
     * Only {@link EntityType#ARMOR_STAND} and {@link EntityType#DROPPED_ITEM} are supported!
     */
    public Packet<?> createPacketSpawnEntity(int id,UUID uuid,Location loc,EntityTypes<? extends Entity> type) {
        try{
            Class<?> packetClass = PacketPlayOutSpawnEntity.class;
            Object packet = packetClass.getConstructor().newInstance();
            Field[] fields = new Field[]{
                    packetClass.getDeclaredField("a"), // ID
                    packetClass.getDeclaredField("b"), // UUID (Only 1.9+)
                    packetClass.getDeclaredField("c"), // Loc X
                    packetClass.getDeclaredField("d"),// Loc Y
                    packetClass.getDeclaredField("e"),// Loc Z
                    packetClass.getDeclaredField("f"),// Mot X
                    packetClass.getDeclaredField("g"),// Mot Y
                    packetClass.getDeclaredField("h"),// Mot Z
                    packetClass.getDeclaredField("i"),// Pitch
                    packetClass.getDeclaredField("j"),// Yaw
                    packetClass.getDeclaredField("k"), // Type
                    packetClass.getDeclaredField("l") // Data
            };
            for (Field field : fields) {
                field.setAccessible(true);
            }
            fields[0].set(packet,id);
            fields[1].set(packet,uuid);
            fields[2].set(packet,loc.getX());
            fields[3].set(packet,loc.getY());
            fields[4].set(packet,loc.getZ());
            fields[5].set(packet,0);
            fields[6].set(packet,0);
            fields[7].set(packet,0);
            fields[8].set(packet,0);
            fields[9].set(packet,0);
            fields[10].set(packet,type);
            fields[11].set(packet,0);
            return (PacketPlayOutSpawnEntity) packet;
        }catch (NoSuchMethodException | NoSuchFieldException | IllegalAccessException | InvocationTargetException | InstantiationException e){
            e.printStackTrace();
//            plugin.getLogger().severe("Failed to create packet to spawn entity!");
//            plugin.debug("Failed to create packet to spawn entity!");
//            plugin.debug(e);
        }
        return null;
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
        canVisble = players;
        Packet p1 = createPacketSpawnEntity(entityId,uuid,location,entityType);
        Packet<PacketListenerPlayOut> p2 = new PacketPlayOutEntityMetadata(entityId,dataWatcher,true);
        playersForEach(p -> {
            if (p.isOnline() && p.getWorld() == location.getWorld()){
                Utils.sendPacket(p1,p);
                Utils.sendPacket(p2,p);
            }
        });
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

    abstract EntityType getEntityType();

    public void spawn(Player player) {
        Packet p1 = createPacketSpawnEntity(entityId,uuid,location,entityType);
        Packet<PacketListenerPlayOut> p2 = new PacketPlayOutEntityMetadata(entityId,dataWatcher,true);
        Utils.sendPacket(p1,player);
        Utils.sendPacket(p2,player);
    }

    public void remove(Player player) {
        Utils.sendPacket(new PacketPlayOutEntityDestroy(entityId),player);
    }

    public DataWatcher getDataWatcher() {
        if (dataWatcher == null){
            initDataWatcher();
        }
        return dataWatcher;
    }

    public void remove() {
        if (canVisble != null){
            PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(entityId);
            playersForEach(player -> {
                Utils.sendPacket(packet,player);
            });
            canVisble = null;
        }
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
        try{
            if (canVisble != null){
                Packet<PacketListenerPlayOut> packet = new PacketPlayOutEntityTeleport();
                Field[] fields = packetPlayOutEntityTeleportClass.getDeclaredFields();
                for (Field field : fields) {
                    field.setAccessible(true);
                }
                fields[0].set(packet,entityId);
                fields[1].set(packet,location.getX());
                fields[2].set(packet,location.getY());
                fields[3].set(packet,location.getZ());
                fields[4].set(packet,(byte) 0);
                fields[5].set(packet,(byte) 0);
                fields[6].set(packet,true);
                playersForEach(player -> Utils.sendPacket(packet,player));
            }
        }catch (ReflectiveOperationException e){
            e.printStackTrace();
        }
    }

    public void sendUpdate() {
        if (canVisble != null){
            final PacketPlayOutEntityMetadata p = new PacketPlayOutEntityMetadata(entityId,dataWatcher,true);
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
        final IChatBaseComponent ibc = ChatComponentScore.ChatSerializer.a(JsonBuilder.parse(customName).toString());
        dataWatcher.set(displayName,Optional.of(ibc));
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
        getDataWatcher().set(noGravity,b);
        sendUpdate();
    }

    public void setCustomNameVisible(boolean b) {
        dataWatcher.set(name_visible,b);
    }


    /**
     * Create a NMS data watcher object to send via a {@code PacketPlayOutEntityMetadata} packet.
     * Gravity will be disabled and the custom name will be displayed if available.
     */
    public void initDataWatcher() {
        byte flags = (byte) (1 << 5);
        dataWatcher = new DataWatcher(null) {
            @Override
            public <T> void set(DataWatcherObject<T> datawatcherobject,T t0) {
                try{
                    Item<T> datawatcher_item = (Item<T>) b.invoke(this,datawatcherobject);
                    if (ObjectUtils.notEqual(t0,datawatcher_item.b())){
                        datawatcher_item.a(t0);
                        datawatcher_item.a(true);
                        g.set(this,true);
                        //                        g = true;
                    }
                }catch (IllegalAccessException e){
                    e.printStackTrace();
                }catch (InvocationTargetException e){
                    e.printStackTrace();
                }

            }
        };
        dataWatcher.register(EntityWrapper.flags,flags);  //flags
        dataWatcher.register(air_tick,300);
        dataWatcher.register(name_visible,customName != null); // custname
        dataWatcher.register(silent,true); // silent
        dataWatcher.register(noGravity,false);
        dataWatcher.register(displayName,Optional.empty());
    }


    public void sendPacket(Packet packet,Player player) {
        EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        PlayerConnection playerConnection = nmsPlayer.playerConnection;
        playerConnection.sendPacket(packet);
    }


}
