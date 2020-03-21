package cn.whiteg.rpgArmour.utils;

import org.bukkit.command.CommandSender;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public abstract class Downloader extends Thread {
    private final String url;
    private final CommandSender sender;
    Map<String, String> property;
    private int code;
    private long size = 0;
    private volatile boolean closed = false;
    private HttpURLConnection conn;

    public Downloader(String url,CommandSender sender,Map<String, String> property) {
        this.url = url;
        this.sender = sender;
        this.property = property;
    }

    public Downloader(String url,CommandSender sender) {
        this(url,sender,null);
    }

    public Downloader(String url) {
        this(url,null,null);
    }


    /**
     * 从输入流中获取字节数组
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    public abstract void readInputStream(InputStream inputStream) throws IOException;

    public boolean isClosed() {
        return closed;
    }

    @Override
    public void run() {
        try{
            URL url = new URL(this.url);
            conn = (HttpURLConnection) url.openConnection();
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

            code = conn.getResponseCode();
            size = conn.getContentLengthLong();
            log("开始下载" + url + "大小" + CommonUtils.tanSpace(size));
            //调用输入
            try (var input = conn.getInputStream()){
                readInputStream(input);
            }
            closed = true;
            onDone();
        }catch (Exception e){
            onError();
        } finally {
            close();
        }


    }

    public abstract long getDownloaded();

    public long getSize() {
        return size;
    }

    public void close() {
        log("已关闭下载任务");
        closed = true;
    }

    public void onDone() {
        log("资源下载完成");
    }

    public void onError() {
        log("资源下载失败:" + code);
    }

    public String getUrl() {
        return url;
    }

    public void log(String msg) {
        if (sender != null){
            sender.sendMessage(msg);
        }
    }

    public String getFileName(URL url) {
        String file = url.getFile();
        int i = Math.max(0,file.lastIndexOf('/'));
        return file.substring(i);
    }

    public int getCode() {
        return code;
    }

    public HttpURLConnection getConn() {
        return conn;
    }
}
