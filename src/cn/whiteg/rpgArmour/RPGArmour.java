package cn.whiteg.rpgArmour;

import cn.whiteg.mmocore.MMOCore;
import cn.whiteg.rpgArmour.custItems.ResurrectArmor;
import cn.whiteg.rpgArmour.listener.*;
import cn.whiteg.rpgArmour.manager.*;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;

import java.util.logging.Logger;


public class RPGArmour extends PluginBase {
    public static RPGArmour plugin;
    public static Debuger debuger;
    public static Logger logger;
    public static ConsoleCommandSender console;
    public MMOCore mmoCore;
    public CommandManager commandManager;
    private CustItemManager itemManager;
    private CustEntityManager entityManager;
    private RecipeManage recipeManage;
    private ApiManager apiManager;
    private GUIManager guiManager; //待开发

    public RPGArmour() {
        plugin = this;
    }

    public void onLoad() {
        logger = getLogger();
        console = Bukkit.getConsoleSender();
        logger.info("--开始加载");
        Setting.reload();
        saveDefaultConfig();
        if (Setting.DEBUG){
            logger.info("已开启调试");
            debuger = new Debuger(getLogger());
            //EntityTypes<MyZombie> nz = EntityTypes.a("myzombie",EntityTypes.a.a(MyZombie.class,MyZombie::new));
            //ARMOR_STAND = a("armor_stand",EntityTypes.a.a(EntityArmorStand::new,EnumCreatureType.MISC).a(0.5F,1.975F));
            //EntityTypes.a("zombie", EntityTypes.a.a(MyMode.class, MyMode::new));
        } else {
            debuger = new Debuger(null);
        }
    }

    public void onEnable() {
//        guiManager = new GUIManager(this); 待开发
        recipeManage = new RecipeManage(this);
        itemManager = new CustItemManager();
        entityManager = new CustEntityManager();
        if (Bukkit.getPluginManager().getPlugin("MMOCore") != null)
            mmoCore = MMOCore.plugin;
        regListener(new ArmorListener());
        regListener(new PlayerItemEatListener());
        regListener(new RideListenetr());
        regListener(new PlayerDammangeListener());
        regListener(new undyingListener());
        regListener(new ResurrectArmor());
        regListener(new DisplayDamageListener());
        //修复刷怪笼事件
//        regListener(new SpawnerReasonFix());
        if (Setting.forgeResourcePack) regListener(new forgePackListener());
//        regEven(new Craftting());
        commandManager = new CommandManager();
        final PluginCommand pcmd = getCommand("rpgarmour");
        if (pcmd != null){
            pcmd.setExecutor(commandManager);
            pcmd.setTabCompleter(commandManager);
        }
        ResourcePackManage.set();
        Bukkit.getScheduler().runTask(this,() -> {
            recipeManage.onSync();
        });
    }

    public void onDisable() {
        //animation.unreg();
        unregListener();
        getLogger().info("关闭");
        recipeManage.unload();
        recipeManage = null;
        entityManager = null;
        itemManager = null;
        apiManager = null;
    }

    public CustItemManager getItemManager() {
        return itemManager;
    }

    public CustEntityManager getEntityManager() {
        return entityManager;
    }

    public RecipeManage getRecipeManage() {
        return recipeManage;
    }

    public GUIManager getGuiManager() {
        return guiManager;
    }

    public ApiManager getApiManager() {
        return apiManager;
    }
}