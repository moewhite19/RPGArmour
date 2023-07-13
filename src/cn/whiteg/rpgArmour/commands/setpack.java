package cn.whiteg.rpgArmour.commands;

import cn.whiteg.mmocore.common.HasCommandInterface;
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

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;

public class setpack extends HasCommandInterface {
    Downloader downloader = null;

    @Override
    public boolean executo(CommandSender sender,Command cmd,String label,String[] args) {
        if (args.length == 0){
            if (downloader != null){
                if (!downloader.isClosed()){
                    sender.sendMessage("下载任务:" + downloader.getUrl() + " 大小" + CommonUtils.tanSpace(downloader.getSize()) + " 进度" + CommonUtils.tanSpace(downloader.getDownloaded()));
                    return true;
                } else {
                    downloader = null;
                }
            }
            sender.sendMessage("没有下载任务");
        } else if (args.length == 1){
            final String url = args[0];
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
            if (downloader != null && !downloader.isClosed()){
                downloader.close();
                downloader.stop();
            }
            downloader = new Downloader(url,sender) {
                long downloaded = 0;

                @Override
                public void readInputStream(InputStream inputStream) throws IOException {
                    MessageDigest sha1Digest;
                    MessageDigest md5Digest;
                    try{
                        sha1Digest = MessageDigest.getInstance("SHA-1");
                        md5Digest = MessageDigest.getInstance("MD5");
                    }catch (NoSuchAlgorithmException e){
                        log(e.getMessage());
                        return;
                    }
                    byte[] buff = new byte[2048];
                    int len;
                    while ((len = inputStream.read(buff)) != -1) {
                        downloaded += len;
                        sha1Digest.update(buff,0,len);
                        md5Digest.update(buff,0,len);
                    }
                    var sha1 = hashFile.bufferToHex(sha1Digest.digest());
                    var md5 = hashFile.bufferToHex(md5Digest.digest());
                    log("Sha1为: " + sha1);
                    log("Md5为: " + md5);
                    ResourcePackManage.set(url,sha1);
                    ResourcePackManage.saveConfig(url,sha1,md5);
                }

                @Override
                public long getDownloaded() {
                    return downloaded;
                }

                @Override
                public void close() {
                    downloader = null;
                    super.close();
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
                        long flag = 0;

                        @Override
                        public void run() {
                            if (!p.isOnline() || downloader == null || downloader.isClosed()){
                                bar.removeAll();
                                cancel();
                                return;
                            }

                            long size = downloader.getSize();
                            long loaded = downloader.getDownloaded();
                            long speed = loaded - flag;
                            flag = loaded;
                            float r = (float) ((double) loaded / (double) size);
                            bar.setTitle("下载进度" + CommonUtils.tanSpace(loaded) + "/" + CommonUtils.tanSpace(size) + "速度" + CommonUtils.tanSpace(speed));
                            bar.setProgress(r);
                        }
                    }.runTaskTimerAsynchronously(RPGArmour.plugin,20,20);
                }catch (IllegalArgumentException | IllegalStateException e){
                    e.printStackTrace();
                }
            }

        } else if (args.length == 2){
            ResourcePackManage.set(args[0],args[1]);
            sender.sendMessage("已设置资源包");
        } else {
            sender.sendMessage("参数有误");
        }
        return true;
    }

    @Override
    public List<String> complete(CommandSender sender,Command cmd,String label,String[] args) {
        if (args.length == 1){
            final ConfigurationSection sc = Setting.getStorage().getConfigurationSection("resourcepack");
            if (sc != null) return Collections.singletonList(sc.getString("url"));
        }
        return null;
    }

    @Override
    public boolean canUseCommand(CommandSender sender) {
        return sender.hasPermission("mmo.setresourcepack");
    }

    @Override
    public String getDescription() {
        return "设置资源包";
    }
}
