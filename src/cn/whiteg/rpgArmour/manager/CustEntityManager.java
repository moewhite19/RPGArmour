package cn.whiteg.rpgArmour.manager;

import cn.whiteg.rpgArmour.RPGArmour;
import cn.whiteg.rpgArmour.api.CustEntity;
import cn.whiteg.rpgArmour.api.CustEntityChunkEvent;
import cn.whiteg.rpgArmour.api.CustEntityID;
import cn.whiteg.rpgArmour.api.CustEntityName;
import cn.whiteg.rpgArmour.custEntitys.Seat;
import cn.whiteg.rpgArmour.custEntitys.SkeletonWin;
import cn.whiteg.rpgArmour.custEntitys.SlimeWin;
import cn.whiteg.rpgArmour.custEntitys.ZombWarrior;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.*;

public class CustEntityManager implements Listener {
    public static final String custtag = "CustEntity";
    private Map<String, CustEntity> entitys = new HashMap<>();
    private Set<CustEntityChunkEvent> chunkEvents = new HashSet<>();

    public CustEntityManager() {
        Bukkit.getScheduler().runTask(RPGArmour.plugin,() -> {
            regEntity(new ZombWarrior());
            regEntity(new SkeletonWin());
            regEntity(new SlimeWin());
            regEntity(Seat.get());
            Bukkit.getScheduler().runTask(RPGArmour.plugin,() -> {
                for (World world : Bukkit.getWorlds()) {
                    for (Entity entity : world.getEntities()) {
                        Set<String> so = entity.getScoreboardTags();
                        if (so != null && !so.isEmpty() && so.contains(custtag)){
                            for (CustEntityChunkEvent ca : chunkEvents) {
                                if (ca.is(entity)){
                                    ca.load(entity);
                                    break;
                                }
                            }
                        }
                    }
                }
                RPGArmour.plugin.regListener(this);
            });
//        regEntity(new ZombWin());
        });


    }

    //注册自定义实体
    public void regEntity(CustEntity ca) {
        entitys.put(ca.getClass().getName(),ca);
        if (ca instanceof CustEntityChunkEvent){
            chunkEvents.add((CustEntityChunkEvent) ca);
        }
        if (ca instanceof Listener) RPGArmour.plugin.regListener((Listener) ca);
    }

    public void unregEntity(CustEntity ca) {
        if (ca == null) return;
        entitys.remove(ca.getClass().getName());
        if (ca instanceof CustEntityChunkEvent){
            chunkEvents.remove((CustEntityChunkEvent) ca);
        }
        if (ca instanceof Listener) RPGArmour.plugin.unregListener((Listener) ca);
    }

    public void regEntity(String name,CustEntity ca) {
        entitys.put(name,ca);
    }

    @Nullable
    public boolean spawnEntity(@NotNull String name,Entity entity) {
        CustEntity ce = entitys.get(name);
        if (ce != null) return ce.init(entity);
        return false;
    }

//    @EventHandler
//    public void onEntitySpawn(EntitySpawnEvent event) {
//        for (Map.Entry<String, CustEntity> entry : entitys.entrySet()) {
//            entry.getValue().is(event.getEntity());
//        }
//    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        for (Entity entity : event.getChunk().getEntities()) {
            final Set<String> so = entity.getScoreboardTags();
            if (!so.isEmpty() && so.contains(custtag)){
                for (CustEntityChunkEvent ca : chunkEvents) {
                    if (ca.is(entity)){
                        ca.load(entity);
                        return;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        for (Entity entity : event.getChunk().getEntities()) {
            final Set<String> so = entity.getScoreboardTags();
            if (!so.isEmpty() && so.contains(custtag)){
                for (CustEntityChunkEvent ca : chunkEvents) {
                    if (ca.is(entity)){
                        ca.unload(entity);
                        return;
                    }
                }
            }
        }
    }

    public Set<String> getCustEntityKeys() {
        return entitys.keySet();
    }

    public CustEntity getCustEntity(String key) {
        CustEntity custEntity = entitys.get(key);
        if (custEntity == null) ;
        for (Map.Entry<String, CustEntity> entry : entitys.entrySet()) {
            if (entry.getValue() instanceof CustEntityName){
                if (((CustEntityName) entry.getValue()).getRawName().equals(key)){
                    return entry.getValue();
                }
            } else if (entry.getValue() instanceof CustEntityID){
                if (((CustEntityID) entry.getValue()).getId().equals(key)){
                    return entry.getValue();

                }
            }
        }
        return custEntity;
    }

    public List<String> getEntityNames() {
        List<String> list = new ArrayList<>(entitys.size());
        for (Map.Entry<String, CustEntity> entry : entitys.entrySet()) {
            if (entry.getValue() instanceof CustEntityName){
                list.add(ChatColor.stripColor(((CustEntityName) entry.getValue()).getName()));
            } else if (entry.getValue() instanceof CustEntityID){
                list.add(((CustEntityID) entry.getValue()).getId());
            } else {
                list.add(ChatColor.stripColor(entry.getValue().getClass().getName()));
            }
        }
        return list;
    }

    public void unreg() {
        for (World world : Bukkit.getWorlds())
            for (Entity entity : world.getEntities()) {
                Set<String> so = entity.getScoreboardTags();
                if (!so.isEmpty() && so.contains(custtag)){
                    for (CustEntityChunkEvent ca : chunkEvents) {
                        if (ca.is(entity)){
                            ca.unload(entity);
                            return;
                        }
                    }
                }
            }
    }
}
