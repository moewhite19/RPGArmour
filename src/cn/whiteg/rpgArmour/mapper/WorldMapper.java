package cn.whiteg.rpgArmour.mapper;

import net.minecraft.world.level.World;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;

public class WorldMapper {
    final World world;

    public WorldMapper(World world) {
        this.world = world;
    }

    public WorldMapper(org.bukkit.World world) {
        this(((CraftWorld) world).getHandle());
    }

    public World getWorld() {
        return world;
    }


}
