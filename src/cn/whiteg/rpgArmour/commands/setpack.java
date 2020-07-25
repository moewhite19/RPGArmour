package cn.whiteg.rpgArmour.commands;

import cn.whiteg.mmocore.common.CommandInterface;
import cn.whiteg.rpgArmour.RPGArmour;
import cn.whiteg.rpgArmour.Setting;
import cn.whiteg.rpgArmour.manager.ResourcePackManage;
import cn.whiteg.rpgArmour.utils.CommonUtils;
import cn.whiteg.rpgArmour.utils.Downloader;
import cn.whiteg.rpgArmour.utils.hashFile;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class setpack extends CommandInterface {
    Downloader downloader = null;

    @Override
    public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args) {
        if (sender.hasPermission("mmo.setresourcepack")){
            if (args.length == 1){
                if (downloader != null){
                    if (!downloader.isClose()){
                        sender.sendMessage("下载任务:" + downloader.getUrl() + " 大小" + CommonUtils.tanByte(downloader.getSize()) + " 进度" + CommonUtils.tanByte(downloader.getDownloaded()));
                        return true;
                    } else {
                        downloader = null;
                    }
                }
                sender.sendMessage("没有下载任务");
            } else if (args.length == 2){
                final String url = args[1];
                if (url.equals("stop")){
                    if (downloader != null){
                        downloader.close();
                        downloader.stop();
                        downloader = null;
                    } else {
                        sender.sendMessage("没有下载任务");
                    }
                    return true;
                }
                if (downloader != null){
                    downloader.close();
                    downloader.stop();
                }
                downloader = new Downloader(url,RPGArmour.plugin.getDataFolder(),"resourcepack.zip",sender) {
                    @Override
                    public void onDone(File file) {
                        try{
                            final String s = hashFile.getSha1(file);
                            log("资源包下载完成 sha1值为" + s);
                            ResourcePackManage.set(getUrl(),s);
                            downloader = null;
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                };
                downloader.start();
                if (sender instanceof Player){
                    try{
                        final Player p = (Player) sender;
                        BossBar bar = Bukkit.createBossBar("下载进度",BarColor.WHITE,BarStyle.SOLID);
                        bar.addPlayer(p);
                        bar.setVisible(true);
                        new BukkitRunnable() {
                            /**
                             * When an object implementing interface <code>Runnable</code> is used
                             * to create a thread, starting the thread causes the object's
                             * <code>run</code> method to be called in that separately executing
                             * thread.
                             * <p>
                             * The general contract of the method <code>run</code> is that it may
                             * take any action whatsoever.
                             *
                             * @see Thread#run()
                             */
                            long flag = 0;

                            @Override
                            public void run() {
                                if (!p.isOnline() || downloader == null || downloader.isClose()){
                                    bar.removeAll();
                                    cancel();
                                    return;
                                }

                                long size = downloader.getSize();
                                long loaded = downloader.getDownloaded();
                                long speed = loaded - flag;
                                flag = loaded;
                                float r = (float) ((double) loaded / (double) size);
                                bar.setTitle("下载进度" + CommonUtils.tanByte(loaded) + "/" + CommonUtils.tanByte(size) + "速度" + CommonUtils.tanByte(speed));
                                bar.setProgress(r);
                            }
                        }.runTaskTimerAsynchronously(RPGArmour.plugin,20,20);
                    }catch (IllegalArgumentException | IllegalStateException e){
                        e.printStackTrace();
                    }
                }

            } else {
                sender.sendMessage("参数有误");
            }
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender,Command cmd,String label,String[] args) {
        if (args.length == 2){
            final ConfigurationSection sc = Setting.getStorage().getConfigurationSection("resourcepack");
            if (sc != null) return Collections.singletonList(sc.getString("url"));
        }
        return null;
    }
}
