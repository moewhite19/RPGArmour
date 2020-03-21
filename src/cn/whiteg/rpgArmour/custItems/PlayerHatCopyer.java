package cn.whiteg.rpgArmour.custItems;


import cn.whiteg.mmocore.DataCon;
import cn.whiteg.mmocore.MMOCore;
import cn.whiteg.rpgArmour.RPGArmour;
import cn.whiteg.rpgArmour.api.CustItem_CustModle;
import cn.whiteg.rpgArmour.event.ArmourChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;

public class PlayerHatCopyer extends CustItem_CustModle implements Listener {
//    public static List<World> worldList = new ArrayList<>();
    //   private short power = 0;
    //  private BukkitTask tick;
    // List<String> lores;
//    public final Map<UUID, Staus> staMap = new HashMap<>();
//    private final int flyid = 2;
//    BukkitTask timer = null;
//    private float flyspeed = 0.03f;

    public PlayerHatCopyer() {
        super(Material.BOWL,37,"§b戴立得头套");
//        try{
//            for (Player player : Bukkit.getOnlinePlayers()) {
//                check(player);
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        ConfigurationSection c = Setting.getCustItemConfit(getClass().getSimpleName());
//        if (c != null){
//            flyspeed = (float) c.getDouble("flySpeed",flyspeed);
//        }
    }


    @EventHandler(ignoreCancelled = true)
    public void onWear(ArmourChangeEvent event) {
        if (event.getType() == ArmourChangeEvent.ArmourType.HELMET && is(event.getItem())){
            Bukkit.getScheduler().runTask(RPGArmour.plugin,() -> {
                PlayerInventory pi = event.getPlayer().getInventory();
                final DataCon dc = MMOCore.getPlayerData(event.getPlayer());
                if (!is(pi.getHelmet()) || dc == null) return;
                String skinuuid = dc.getString("Player.SkinUUID");
                if (skinuuid == null || skinuuid.isEmpty()) return;
                final ItemStack head = new ItemStack(Material.PLAYER_HEAD);
                final SkullMeta im = (SkullMeta) head.getItemMeta();
                im.setOwningPlayer(event.getPlayer());
                head.setItemMeta(im);
                int am = pi.getHelmet().getAmount();
                head.setAmount(am);
                pi.setHelmet(head);
            });
        }
    }

//    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
//    public void onInvClick(InventoryClickEvent event) {
//        if (event.getClickedInventory().getType() != InventoryType.PLAYER || event.getSlot() != 39) return;
//        Staus sta = staMap.get(event.getWhoClicked().getUniqueId());
//        if (sta == null) check((Player) event.getWhoClicked());
//        if (event.getHotbarButton() != -1){
//            if (event.getRawSlot() != 5) return;
//            sta.remove();
//            return;
//        } else if (event.getSlot() == 39 && event.getClick() != ClickType.MIDDLE){
//            sta.remove();
//        }
//        //setItem(event.getCurrentItem());
//    }

}

