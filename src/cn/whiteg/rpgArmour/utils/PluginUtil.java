package cn.whiteg.rpgArmour.utils;

import cn.whiteg.mmocore.reflection.FieldAccessor;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class PluginUtil {

    private static final Class<?> pluginLoadderClass;
    private static final FieldAccessor<?> pluginField;

    static {
        try{
            pluginLoadderClass = Class.forName("org.bukkit.plugin.java.PluginClassLoader");
            pluginField = new FieldAccessor<>(pluginLoadderClass.getDeclaredField("plugin"));
        }catch (ClassNotFoundException | NoSuchFieldException e){
            throw new RuntimeException(e);
        }
    }

    public static void kickPlayer(Player p,String Message) {
        p.kickPlayer(Message);
    }

    @Deprecated
    public static PluginCommand getPluginCommanc(final JavaPlugin plugin,final String name) {
        return getPluginCommand(plugin,name);
    }

    public static PluginCommand getPluginCommand(final JavaPlugin plugin,final String name) {
        PluginCommand pc = plugin.getCommand(name);
        if (pc == null){
            try{
                final Constructor<PluginCommand> cr = PluginCommand.class.getDeclaredConstructor(String.class,Plugin.class);
                cr.setAccessible(true);
                pc = cr.newInstance(name,plugin);
                pc.setDescription("None " + name);
                pc.setUsage("/" + name);
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
        return pc;
    }


    public static List<String> getUrls(ClassLoader loader) throws IOException {
        return getUrls(loader,false);
    }

    /**
     * 遍历插件内的资源文件
     *
     * @param loader Class加载器
     * @param folder 是否包含文件夹
     * @return 返回文件列表
     * @throws IOException IO异常
     */
    public static List<String> getUrls(ClassLoader loader,boolean folder) throws IOException {
        if (loader instanceof URLClassLoader classLoader){
            List<String> list = new ArrayList<>();
            URL[] jars = classLoader.getURLs();
            for (URL jar : jars) {
                JarInputStream jarInput = new JarInputStream(jar.openStream());
                while (true) {
                    JarEntry entry = jarInput.getNextJarEntry();
                    if (entry == null) break;
                    if (!folder && entry.isDirectory()) continue;
                    list.add(entry.getName());
                }
            }
            return list;
        }
        return Collections.emptyList();
    }

    //获取调用这个方法的插件
    public static JavaPlugin getCurrentPlugin() {
        final Thread thread = Thread.currentThread();
        final StackTraceElement[] stackTrace = thread.getStackTrace();
        for (int i = stackTrace.length - 2; i > 0; i--) {
            final StackTraceElement element = stackTrace[i];
            try{
                final Class<?> clazz = Class.forName(element.getClassName());
                final JavaPlugin plugin = getPluginFormClass(clazz);
                if(plugin != null) return plugin;
            }catch (ClassNotFoundException | IllegalAccessError e){
                e.printStackTrace();
            }
        }
        throw new RuntimeException("找不到插件");
    }

    //获取这个类属于哪个插件
    public static JavaPlugin getPluginFormClass(Class<?> clazz){
//                if (clazz.getClassLoader() instanceof PluginClassLoader loader){
//                    return loader.getPlugin();
//                }
        final ClassLoader loader = clazz.getClassLoader();
        if(pluginLoadderClass.isInstance(loader)){
            return (JavaPlugin) pluginField.get(loader);
        }
        return null;
    }
}
