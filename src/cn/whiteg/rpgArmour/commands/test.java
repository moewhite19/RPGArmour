package cn.whiteg.rpgArmour.commands;

import cn.whiteg.mmocore.common.CommandInterface;
import cn.whiteg.rpgArmour.Setting;
import cn.whiteg.rpgArmour.enitity.MyZombie;
import cn.whiteg.rpgArmour.entityWrapper.DropItem;
import cn.whiteg.rpgArmour.entityWrapper.Zombie;
import cn.whiteg.rpgArmour.nms.NMSItem;
import cn.whiteg.rpgArmour.nms.TagCompound;
import net.minecraft.server.v1_15_R1.PacketPlayOutExplosion;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.Vector;

import java.util.*;

public class test extends CommandInterface {
    Map<UUID, String> cache = new WeakHashMap<>();

    @Override
    public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args) {
        if (!sender.hasPermission("whiteg.test")){
            sender.sendMessage("§b权限不足");
            return true;
        }
        if (args.length < 2) return false;
        test(sender,args);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender,Command cmd,String label,String[] args) {
        return null;
    }

    public void test(CommandSender sender,String[] args) {
        sender.sendMessage("测试项目" + Arrays.toString(args));
        switch (args[1]) {
            case "0": {
                //EntityTypes<MyZombie> nz = EntityTypes.a("myzombie",EntityTypes.a.a(MyZombie.class,MyZombie::new));
                //EntityTypes.a("zombie", EntityTypes.a.a(MyMode.class, MyMode::new));.
                Player player = (Player) sender;
                MyZombie mz = new MyZombie(player.getLocation());
                ((CraftWorld) player.getLocation().getWorld()).getHandle().addEntity(mz,CreatureSpawnEvent.SpawnReason.CUSTOM);
                break;
            }
            case "1": {
                Player player = (Player) sender;
                Location loc = player.getLocation();
//            player.getWorld().init(loc , MyMode.class , (MyMode e) -> {
//
//            });
 /*           World world = ((CraftWorld) player.getWorld()).getHandle();
            if(MyMode.md!=null){
                Entity ent = MyMode.md.a(world);
                ent.locX = loc.getX();
                ent.locY = loc.getY();
                ent.locZ = loc.getZ();
            }else {
                sender.sendMessage("生成失败");
            }*/
                break;
            }
            case "hasTag": {
                Player player = (Player) sender;
                PlayerInventory inv = player.getInventory();
                ItemStack item = inv.getItemInMainHand();
                NMSItem nms = NMSItem.asNmsItemCopy(item);
                sender.sendMessage("has" + nms.hasTag());
                break;
            }
            case "setTag": {
                Player player = (Player) sender;
                PlayerInventory inv = player.getInventory();
                ItemStack item = inv.getItemInMainHand();
                NMSItem nms = NMSItem.asNmsItemCopy(item);
                TagCompound tgs = nms.hasTag() ? nms.getTag() : nms.craftTag();
                String key = args.length > 2 ? args[2] : "Test";
                String str = args.length > 3 ? args[3] : "Test";
                tgs.setString(key,str);
                sender.sendMessage(" 设置 " + key + " 为 " + str);
                nms.update();
                inv.setItemInMainHand(nms.getItem());
                break;
            }

            case "getTag": {
                Player player = (Player) sender;
                PlayerInventory inv = player.getInventory();
                ItemStack item = inv.getItemInMainHand();
                NMSItem nms = NMSItem.asNmsItemCopy(item);
                TagCompound tgs = nms.getTag();
                String key = args.length > 2 ? args[2] : "Test";
                if (tgs.hasTag()) sender.sendMessage("目标Tag为 " + tgs.getString(key));
                else sender.sendMessage("对象没用Tag");
                break;
            }

            //测试掉落物发包
            case "item": {
                if (sender instanceof Player){
                    Player player = (Player) sender;
                    final DropItem dropItem = new DropItem(player.getLocation(),player.getInventory().getItemInMainHand());
                    dropItem.setVector(new Vector(0D,3D,0D));
                    player.getNearbyEntities(32D,32D,32D).forEach(entity -> {
                        if (entity instanceof Player){
                            Player p = (Player) entity;
                            dropItem.setVector(p.getVelocity());
                            dropItem.spawn(p);
                        }
                    });
                    dropItem.spawn(player);
                }
                break;
            }
            //测试储存物品到Yml
            case "saveItem": {
                Player player = (Player) sender;
                PlayerInventory inv = player.getInventory();
                ItemStack item = inv.getItemInMainHand();
                String key = args.length > 2 ? args[2] : "Test";
                Setting.getStorage().set(key,item);
                Setting.saveStorage();
                break;
            }
            case "loadItem": {
                Player player = (Player) sender;
                PlayerInventory inv = player.getInventory();
                String key = args.length > 2 ? args[2] : "Test";
                inv.setItemInMainHand(Setting.getStorage().getItemStack(key));
                break;
            }

            case "t1": {
                Player player = (Player) sender;
                Location loc = player.getLocation();
                Zombie zombie = new Zombie();
                zombie.setLocation(loc);
                zombie.setVector(player.getVelocity());
                zombie.spawn(player);
                break;
            }
            default:
                sender.sendMessage("NULL");
                break;
        }
        /*
        EntityLiving oldEntity = ((CraftLivingEntity) sender).getHandle();
        sender.sendMessage("System "+oldEntity.getAttributeMap().a(args[2]).toString());
   */
    }

    //生成盔甲架
    public ArmorStand spawnEntity(Location loc) {
        return loc.getWorld().spawn(loc,ArmorStand.class);
    }

    //生成盔甲架并带上头颅
    public ArmorStand spawnEntity(Player player) {
        final ArmorStand st = spawnEntity(player.getLocation());
        st.getEquipment().setHelmet(getHead(player));
        return st;
    }

    //获取玩家头颅
    public ItemStack getHead(Player player) {
        ItemStack head = new ItemStack(org.bukkit.Material.PLAYER_HEAD);
        SkullMeta mat = (SkullMeta) head.getItemMeta();
        mat.setOwningPlayer(player);
        head.setItemMeta(mat);
        return head;
    }

    public void sendExplosion(Player player,Location loc,float size) {
        PacketPlayOutExplosion fakeExplosion = new PacketPlayOutExplosion(loc.getBlockX(),loc.getY(),loc.getZ(),size,new ArrayList<>(),null);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(fakeExplosion);
    }
}
