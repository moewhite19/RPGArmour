package cn.whiteg.rpgArmour.enitity;

import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;

public class MyZombie extends EntityZombie {
    private static MinecraftKey minecraftKey;
    static {
        //System.out.print("注册实体");
        //minecraftKey = new MinecraftKey("my_zombie");
    }
    public MyZombie(Location location) {
        super(((CraftWorld) location.getWorld()).getHandle());
//        this.locX = location.getX();
//        this.locY = location.getY();
//        this.locZ = location.getZ();
        modspawn();
    } public MyZombie(World world) {
        super(((CraftWorld)world).getHandle());
        modspawn();
    }

    public MyZombie(net.minecraft.server.v1_15_R1.World world) {
        super(world);
    }

    public void modspawn(){
      //  this.setCustomName(IChatBaseComponent.ChatSerializer.a("§b我的僵尸"));
        this.setCustomNameVisible(true);
        //ItemStack is = new ItemStack(Material.COMMAND_BLOCK);
        Bukkit.broadcastMessage("我的僵尸");
    }
}
