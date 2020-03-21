package cn.whiteg.rpgArmour.manager;

import cn.whiteg.rpgArmour.RPGArmour;
import cn.whiteg.rpgArmour.api.CustEntity;
import cn.whiteg.rpgArmour.api.CustEntityChunkEvent;
import cn.whiteg.rpgArmour.custEntitys.Seat;
import cn.whiteg.rpgArmour.custEntitys.SkeletonWin;
import cn.whiteg.rpgArmour.custEntitys.SlimeWin;
import cn.whiteg.rpgArmour.custEntitys.ZombieWarrior;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static cn.whiteg.rpgArmour.RPGArmour.logger;

public class CustEntityManager implements Listener {
    public static final NamespacedKey ROOT_KEY = new NamespacedKey(RPGArmour.plugin,"cust_entity");
    public static final NamespacedKey TYPE_KEY = new NamespacedKey(RPGArmour.plugin,"type");
    private final Map<String, CustEntity> entitys = new HashMap<>();

    public CustEntityManager() {
        Bukkit.getScheduler().runTask(RPGArmour.plugin,() -> {
            regEntity(new ZombieWarrior());
            regEntity(new SkeletonWin());
            regEntity(new SlimeWin());
            regEntity(Seat.get());
            Bukkit.getScheduler().runTask(RPGArmour.plugin,() -> {
                for (World world : Bukkit.getWorlds()) {
                    for (Entity entity : world.getEntities()) {
                        handleLoader(entity,true);
                    }
                }
                RPGArmour.plugin.regListener(this);
            });
        });
    }

    public static void setPersistentDataContainer(Entity entity,PersistentDataContainer con) {
        var dataContainer = entity.getPersistentDataContainer();
        dataContainer.set(CustEntityManager.ROOT_KEY,PersistentDataType.TAG_CONTAINER,con);
    }

    public static PersistentDataContainer getPersistentDataContainer(Entity entity,boolean create) {
        var dataContainer = entity.getPersistentDataContainer();
        var con = dataContainer.get(CustEntityManager.ROOT_KEY,PersistentDataType.TAG_CONTAINER);
        if (con == null && create){
            con = dataContainer.getAdapterContext().newPersistentDataContainer();
            dataContainer.set(CustEntityManager.ROOT_KEY,PersistentDataType.TAG_CONTAINER,con);
        }
        return con;
    }

    //注册自定义实体
    public void regEntity(CustEntity ca) {
        entitys.put(ca.getId(),ca);
        if (ca instanceof Listener listener) RPGArmour.plugin.regListener(listener);
    }

    public void unregEntity(CustEntity ca) {
        if (ca == null) return;
        entitys.remove(ca.getId());
        if (ca instanceof Listener) RPGArmour.plugin.unregListener((Listener) ca);
    }

    public void regEntity(String name,CustEntity ca) {
        entitys.put(name,ca);
    }

//    @EventHandler
//    public void onEntitySpawn(EntitySpawnEvent event) {
//        for (Map.Entry<String, CustEntity> entry : entitys.entrySet()) {
//            entry.getValue().is(event.getEntity());
//        }
//    }

    public boolean spawnEntity(@NotNull String name,Entity entity) {
        CustEntity ce = entitys.get(name);
        if (ce != null) return ce.init(entity);
        return false;
    }


    //todo 即使加了2tick的延迟也依然无法保证能获取到实体,再想办法处理吧x
//    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        if (event.isNewChunk()) return;
        Bukkit.getScheduler().runTaskLater(RPGArmour.plugin,() -> {
            for (Entity entity : event.getChunk().getEntities()) {
                handleLoader(entity,true);
            }
        },2L);
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        for (Entity entity : event.getChunk().getEntities()) {
            handleLoader(entity,false);
        }

    }

    public Set<String> getCustEntityKeys() {
        return entitys.keySet();
    }

    public CustEntity getCustEntity(String key) {
        return entitys.get(key);
    }

    public CustEntity getCustEntity(Entity entity) {
        //持久化数据储存, 开发中
        var root = getPersistentDataContainer(entity,false);
        if (root != null){
            var type = root.get(TYPE_KEY,PersistentDataType.STRING);
            if (type != null){
                var custentity = getCustEntity(type);
                if (custentity != null) return custentity;
                //如果没有找到
                logger.warning("找不到实体类: " + type);
            }
        }
        return null;
    }

    //处理加载和卸载
    public void handleLoader(Entity entity,boolean isLoad) {
        var custEntity = getCustEntity(entity);
        if (custEntity instanceof CustEntityChunkEvent event){
            if (isLoad) event.load(entity);
            else event.unload(entity);
        }
    }

    public void unreg() {
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                handleLoader(entity,false);
            }
        }
    }

}
