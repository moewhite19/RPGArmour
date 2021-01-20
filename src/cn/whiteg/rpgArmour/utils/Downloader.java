package cn.whiteg.rpgArmour.utils;

import org.bukkit.command.CommandSender;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public abstract class Downloader extends Thread {
    private final String url;
    private final File file;
    private final CommandSender sender;
    Map<String, String> property;
    private InputStream inputStream = null;
    private FileOutputStream fos = null;
    private ByteArrayOutputStream bos = null;
    private volatile long size = 0;
    private volatile long downloaded = 0;
    private volatile boolean close = false;

    public Downloader(String url,File saveFile,CommandSender sender,Map<String, String> property) {
        this.url = url;
        this.file = saveFile;
        this.sender = sender;
        this.property = property;
    }

    public Downloader(String url,File savePath,CommandSender sender) {
        this(url,savePath,sender,null);
    }

    public Downloader(String url,File savePath) {
        this(url,savePath,null,null);
    }


    /**
     * 从输入流中获取字节数组
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    public byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len;
        bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            downloaded += len;
            bos.write(buffer,0,len);
        }
        bos.close();
        return bos.toByteArray();
    }

    public boolean isClose() {
        return close;
    }

    @Override
    public void run() {
        try{
            URL url = new URL(this.url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            // 设置连接主机服务器的超时时间：15000毫秒
            conn.setConnectTimeout(15000);
            // 设置读取远程返回的数据时间：30000毫秒
            conn.setReadTimeout(30000);
            //防止屏蔽程序抓取而返回403错误
//            conn.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            if (property != null && !property.isEmpty()){
                property.forEach(conn::setRequestProperty);
            }
            size = conn.getContentLengthLong();
            //得到输入流
            log("开始下载" + url + "大小" + CommonUtils.tanSize(size));
            inputStream = conn.getInputStream();
            //获取自己数组
            byte[] getData = readInputStream(inputStream);
            bos = null;
            //文件保存位置
            if (!file.getParentFile().exists()){
                file.getParentFile().mkdirs();
            }
            if (!file.exists()) file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(getData);
            close();
            close = true;
            onDone(file);
        }catch (IOException e){
            onError();
        }


    }

    public long getDownloaded() {
        return downloaded;
    }

    public long getSize() {
        return size;
    }

    public void close() {
        if (fos != null){
            try{
                fos.close();
            }catch (IOException e){
                e.printStackTrace();
            }
            fos = null;
        }
        if (inputStream != null){
            try{
                inputStream.close();
            }catch (IOException e){
                e.printStackTrace();
            }
            inputStream = null;
        }
        if (bos != null){
            try{
                bos.close();
            }catch (IOException e){
                e.printStackTrace();
            }
            bos = null;
        }
        log("已关闭下载任务");
        close = true;
    }

    abstract public void onDone(File file);

    public void onError() {
        close();
        log("资源包下载失败");
    }

    public String getUrl() {
        return url;
    }

    public void log(String msg) {
        if (sender != null){
            sender.sendMessage(msg);
        }
    }
}
