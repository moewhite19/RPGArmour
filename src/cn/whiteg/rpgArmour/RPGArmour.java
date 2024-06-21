package cn.whiteg.rpgArmour;

import cn.whiteg.mmocore.MMOCore;
import cn.whiteg.mmocore.common.CommandManage;
import cn.whiteg.rpgArmour.listener.*;
import cn.whiteg.rpgArmour.manager.*;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

import java.util.logging.Logger;


public class RPGArmour extends PluginBase {
    public static RPGArmour plugin;
    public static Debuger debuger;
    public static Logger logger;
    public static ConsoleCommandSender console;
    public MMOCore mmoCore;
    public CommandManage commandManager;
    public SimpleHttpServer slimeHttpServer;
    private CustItemManager itemManager;
    private CustEntityManager entityManager;
    private RecipeManage recipeManage;
    private ApiManager apiManager;
    //todo 待开发侧边栏GUI
//    private GUIManager guiManager;
    private CanBreakEntityItem canBreakEntityItem;

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
        mmoCore = MMOCore.plugin;
        recipeManage = new RecipeManage(this);
        itemManager = new CustItemManager();
        entityManager = new CustEntityManager();
        regListener(new PlayerItemEatListener());
        regListener(new RideListenetr());
        regListener(new UndyingListener());
        regListener(new DisplayDamageListener()); //显示伤害
        canBreakEntityItem = new CanBreakEntityItem();
        regListener(canBreakEntityItem);
        //修复刷怪笼事件
//        regListener(new SpawnerReasonFix());
        regListener(new Craftting());
        commandManager = new CommandManage(this);
        commandManager.setExecutor();
        ResourcePackManage.load();
        Bukkit.getScheduler().runTask(this,() -> {
            recipeManage.onSync();
        });
//        slimeHttpServer = SimpleHttpServer.create(Setting.getConfig().getConfigurationSection("HttpServer"));
    }

    public void onDisable() {
        unregListener();
        if (slimeHttpServer != null){
            slimeHttpServer.shutdown();
            slimeHttpServer = null;
        }
        getLogger().info("已关闭");
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

    //   todo 待开发
//    public GUIManager getGuiManager() {
//        return guiManager;
//    }

    public ApiManager getApiManager() {
        return apiManager;
    }

    public CanBreakEntityItem getCanBreakEntityItem() {
        return canBreakEntityItem;
    }
}