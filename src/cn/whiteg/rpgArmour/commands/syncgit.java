package cn.whiteg.rpgArmour.commands;

import cn.whiteg.mmocore.common.HasCommandInterface;
import cn.whiteg.rpgArmour.RPGArmour;
import cn.whiteg.rpgArmour.Setting;
import cn.whiteg.rpgArmour.manager.ResourcePackManage;
import cn.whiteg.rpgArmour.utils.CommonUtils;
import cn.whiteg.rpgArmour.utils.Downloader;
import cn.whiteg.rpgArmour.utils.hashFile;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class syncgit extends HasCommandInterface {
    Downloader downloader = null;

    @Override
    public boolean executo(CommandSender sender,Command cmd,String label,String[] args) {
        final ConfigurationSection cs = Setting.getConfig().getConfigurationSection("SyncGit");
        if (cs == null) return false;
        String repo = cs.getString("repo");
        String localHostURL = cs.getString("localhostURL");
        final String downloadUrl = "https://codeload.github.com/" + repo + "/zip/refs/heads/master";
        final String commitsUrl = "https://api.github.com/repos/" + repo + "/commits";
        if (downloader != null && !downloader.isClosed()){
            downloader.close();
            downloader.stop();
        }

        //先获取提交sha值
        downloader = new Downloader(commitsUrl,sender) {
            private String sha;

            @Override
            public void readInputStream(InputStream inputStream) throws IOException {
                final byte[] bytes = inputStream.readAllBytes();
                String raw = new String(bytes,StandardCharsets.UTF_8);
                JsonElement jsonElement = JsonParser.parseString(raw);
                jsonElement = jsonElement.getAsJsonArray().get(0);
                sha = jsonElement.getAsJsonObject().get("sha").getAsString();
                log(sha);
            }

            @Override
            public long getDownloaded() {
                return 1;
            }

            //完成后再下载压缩包
            @Override
            public void onDone() {
                File dir = new File(RPGArmour.plugin.slimeHttpServer.getRootDir(),"Resource");
                if (!dir.exists()){
                    if (!dir.mkdirs()){
                        log("无法创建目录： " + dir);
                        return;
                    }
                }
                //下载文件
                downloader = new Downloader(downloadUrl,sender) {
                    @Override
                    public void readInputStream(InputStream inputStream) throws IOException {
                        File file = new File(dir,sha.substring(0,7) + ".zip");
                        if (file.exists()){
                            log("文件已存在,但仍然重新下载");
                        } else {
                            file.createNewFile();
                        }

                        //消息摘要
                        MessageDigest sha1Digest;
                        MessageDigest md5Digest;
                        try{
                            sha1Digest = MessageDigest.getInstance("SHA-1");
                            md5Digest = MessageDigest.getInstance("MD5");
                        }catch (NoSuchAlgorithmException e){
                            log(e.getMessage());
                            return;
                        }

                        //输入重新打包
                        try (ZipInputStream zipInput = new ZipInputStream(inputStream);
                             OutputStream output = new FileOutputStream(file);
                             ZipOutputStream zipOutput = new ZipOutputStream(output);
                        ){
                            //重新打包过程
                            ZipEntry entry;
                            byte[] buff = new byte[1024];
                            int read;
                            while ((entry = zipInput.getNextEntry()) != null) {
                                String name = entry.getName();
                                int ix = name.indexOf('/');
                                if (ix == -1) continue;
                                name = name.substring(ix + 1);
                                if (name.isBlank() || entry.isDirectory()) continue;
                                final ZipEntry e = new ZipEntry(name);
                                e.setLastModifiedTime(entry.getLastModifiedTime());
                                zipOutput.putNextEntry(e);
                                while ((read = zipInput.read(buff)) != -1) {
                                    zipOutput.write(buff,0,read);
                                }
                            }
                        }

//

                        //打包完成后计算sha值
                        try (InputStream in = new FileInputStream(file)){
                            byte[] buff = new byte[2048];
                            int read;
                            while ((read = in.read(buff)) != -1) {
                                sha1Digest.update(buff,0,read);
                                md5Digest.update(buff,0,read);
                            }
                        }
                        var sha1 = hashFile.bufferToHex(sha1Digest.digest());
                        var md5 = hashFile.bufferToHex(md5Digest.digest());
                        log("Sha1为: " + sha1);
                        log("Md5为: " + md5);
                        String realUrl = localHostURL + "/Resource/" + file.getName();
                        ResourcePackManage.set(realUrl,sha1);
                        ResourcePackManage.saveConfig(realUrl,sha1,md5);
                    }

                    @Override
                    public long getDownloaded() {
                        return 1;
                    }

                    @Override
                    public void close() {
                        downloader = null;
                        super.close();
                    }
                };
                downloader.start();
            }
        };
        if (sender instanceof Player p && p.isOnline()){
            try{
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
                            downloader = null;
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
        return sender.hasPermission("mmo.setresourcepack") && RPGArmour.plugin.slimeHttpServer != null;
    }

    @Override
    public String getDescription() {
        return "设置资源包";
    }
}
