package cn.whiteg.rpgArmour.api;


import org.bukkit.entity.Entity;

public interface CustEntityChunkEvent extends CustEntity {
    void load(Entity entity);

    void unload(Entity entity);
}
