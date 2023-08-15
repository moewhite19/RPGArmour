package cn.whiteg.rpgArmour.commands;

import cn.whiteg.mmocore.common.HasCommandInterface;
import cn.whiteg.rpgArmour.RPGArmour;
import cn.whiteg.rpgArmour.Setting;
import cn.whiteg.rpgArmour.manager.ResourcePackManage;
import cn.whiteg.rpgArmour.utils.CommonUtils;
import cn.whiteg.rpgArmour.utils.Downloader;
import cn.whiteg.rpgArmour.utils.HashFile;
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
import org.jetbrains.annotations.NotNull;

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
    public boolean executo(@NotNull CommandSender sender,@NotNull Command cmd,@NotNull String label,String[] args) {
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
                            //noinspection ResultOfMethodCallIgnored
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
                             //重写OutputSteam流的写入方法，输出的过程插入md5和sha1效验检测
                             OutputStream output = new FileOutputStream(file){
                                 @Override
                                 public void write(int b) throws IOException {
                                     final byte h = (byte) b;
                                     sha1Digest.update(h);
                                     md5Digest.update(h);
                                     super.write(b);
                                 }

                                 @Override
                                 public void write(byte @NotNull [] b) throws IOException {
                                     sha1Digest.update(b);
                                     md5Digest.update(b);
                                     super.write(b);
                                 }

                                 @Override
                                 public void write(byte @NotNull [] b,int off,int len) throws IOException {
                                     sha1Digest.update(b,off,len);
                                     md5Digest.update(b,off,len);
                                     super.write(b,off,len);
                                 }
                             };
                             ZipOutputStream zipOutput = new ZipOutputStream(output)
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

                        //打包完成后计算sha值
                        var sha1 = HashFile.bufferToHex(sha1Digest.digest());
                        var md5 = HashFile.bufferToHex(md5Digest.digest());
//                        if(Setting.DEBUG){
//                            log("Sha1为: " + sha1);
//                            log("Md5为: " + md5);
//                            try{
//                                log("重新检测的结果为: ");
                                  //偷个懒x,虽然会无意义的加载两次文件，但是这是现成的方法啊，这是测试代码啊
//                                log("Sha1为: " + HashFile.getSha1(file));
//                                log("Md5为: " + HashFile.getMD5(file));
//                            }catch (NoSuchAlgorithmException e){
//                                e.printStackTrace();
//                            }
//                        }
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

                    @Override
                    public void onDone() {
                        sendpack.updateBoard();
                    }
                };
                downloader.start();
            }
        };
        downloader.start();
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
                        bar.setTitle("下载进度" + CommonUtils.tanSpace(loaded) + "/" + CommonUtils.tanSpace(size) + "速度" + CommonUtils.tanSpace(speed));
                        float r = (float) ((double) loaded / (double) size);
                        bar.setProgress(r > 1 ? 1 : (r < 0 ? 0 : r));
                    }
                }.runTaskTimerAsynchronously(RPGArmour.plugin,20,20);
            }catch (IllegalArgumentException | IllegalStateException e){
                e.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public List<String> complete(@NotNull CommandSender sender,@NotNull Command cmd,@NotNull String label,String[] args) {
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
        return "从github同步资源包";
    }
}
