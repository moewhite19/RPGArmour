package cn.whiteg.rpgArmour;

import cn.whiteg.rpgArmour.utils.CommonUtils;
import com.google.common.util.concurrent.RateLimiter;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.bukkit.configuration.ConfigurationSection;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class SimpleHttpServer implements HttpHandler {
    final HttpServer server;
    final private long rateLimiting;
    final private int maxCount;
    private final String dirAbsolutePath;
    AtomicInteger linkCount = new AtomicInteger(0);
    File root;

    public SimpleHttpServer(File rootDir,URL url,long rateLimiting,int maxCount) throws IOException {
        this.rateLimiting = rateLimiting;
        this.maxCount = maxCount;
        server = HttpServer.create(new InetSocketAddress(url.getHost(),url.getPort()),0);
        server.createContext("/",this);
        this.root = rootDir;
        if (!rootDir.exists()) //noinspection ResultOfMethodCallIgnored
            rootDir.mkdirs();
        dirAbsolutePath = rootDir.getAbsolutePath();
        server.setExecutor(Executors.newCachedThreadPool()); //设置线程队列
        server.start();
        System.out.println("启动Http服务器: " + url + "#" + server.getExecutor());
    }

    public static SimpleHttpServer create(ConfigurationSection sc) {
        if (sc == null) return null;
        if (sc.getBoolean("enable",false)){
            try{
                return new SimpleHttpServer(new File(sc.getString("serverDir","plugins/RPGArmour/http").replace('/',File.separatorChar)),new URL(sc.getString("serverUrl","http://0.0.0.0:1888")),CommonUtils.toByteLength(sc.getString("rateLimiting")),sc.getInt("maxCount",0));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void handle(HttpExchange exchange) {
        //限制连接数
        if (maxCount == 0 || linkCount.get() < maxCount){
            linkCount.addAndGet(1);
            try (exchange){
                final String path = exchange.getRequestURI().getPath();
                File file = new File(root,path);
                final String absolutePath = file.getAbsolutePath();
//                System.out.println(Thread.currentThread() + "访问文件: " + absolutePath);
//                System.out.println("请求头: ");
                final Headers requestHeaders = exchange.getRequestHeaders();
//                for (Map.Entry<String, List<String>> entry : requestHeaders.entrySet()) {
//                    System.out.println(entry.getKey() + ": " + entry.getValue().toString());
//                }
                //防止下载目录外的文件
                if (!absolutePath.startsWith(dirAbsolutePath)){
                    responseExchange(exchange,HttpURLConnection.HTTP_BAD_REQUEST,"BAD_REQUEST");
                    return;
                }
                //文件存在
                BasicFileAttributes fileAttributes = Files.readAttributes(file.toPath(),BasicFileAttributes.class);
                if (file.exists() && fileAttributes.isRegularFile()){
                    RateLimiter rateLimiter = this.rateLimiting > 0 ? RateLimiter.create(this.rateLimiting) : null;
                    long length = fileAttributes.size();
                    long start = 0;
                    long end = length;

                    final Headers responseHeaders = exchange.getResponseHeaders();

                    //读取断点续传
                    String range = requestHeaders.getFirst("Range");
                    if (range != null && range.startsWith("bytes=")){
//                            System.out.println("收到断点续传: " + range);
                        range = range.substring(range.indexOf('=') + 1);
                        final int index = range.indexOf('-');
                        if (index > 0){
                            String v = range.substring(0,index);
                            if (!v.isBlank()) start = Long.parseLong(v);
                            v = range.substring(index + 1);
                            if (!v.isBlank()) end = Long.parseLong(v);
                        } else {
                            start = Long.parseLong(range);
                        }
                        length = Math.min(length,end - start);
                    }


                    responseHeaders.set("Content-length",String.valueOf(length));
                    responseHeaders.set("Accept-Ranges","bytes");
                    if (end != length){
                        //断点续传
                        range = "bytes " + start + "-" + end + "/" + length;
//                            System.out.println("返回断点续传: " + range);
                        responseHeaders.set("Content-Range",range);
                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_PARTIAL,length);
                    } else {
                        //完整下载
                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK,length);
                    }

                    try (OutputStream output = exchange.getResponseBody(); InputStream input = new BufferedInputStream(new FileInputStream(file))){
                        //todo 这个断点续传好像没法工作捏
                        if (start > 0){
                            long skip = input.skip(start);
                            if (skip != start) System.out.println("想要跳过" + start + "只跳过了" + skip);
                        }
                        byte[] buff = new byte[2048];
                        int len;
                        while ((len = input.read(buff,0,Math.toIntExact(Math.min(length,buff.length)))) > 0) {
                            //如果有限速处理限速
                            if (rateLimiter != null && !rateLimiter.tryAcquire(len)){
                                while (!rateLimiter.tryAcquire(len)) {
                                    sleep(1);
                                }
                            }
                            output.write(buff,0,len);
                            length -= len;
                        }

                    }
                } else {
                    responseExchange(exchange,HttpURLConnection.HTTP_NOT_FOUND,"not fond");
                }

            }catch (IOException e){
                //客户端未下载完成
//                    System.out.println(currentThread.getName() + "客户端下载未完成" + CommonUtils.tanSpace(schedule) + " : " + e.getMessage());
//                    e.printStackTrace();
            } finally {
                linkCount.addAndGet(-1);
//                    System.out.println(currentThread.getName() + "连接减少");
            }
        } else {
            exchange.close();
        }
    }

    public void shutdown() {
        server.stop(0);
        if (server.getExecutor() instanceof ExecutorService service){
            service.shutdown();
        }
        System.out.println("已关闭Http服务器");
    }

    private void sleep(long l) {
        try{
            Thread.sleep(l);
        }catch (InterruptedException e){
            throw new RuntimeException(e);
        }
    }

    public static void responseExchange(HttpExchange exchange,int code,String msg) throws IOException {
        exchange.sendResponseHeaders(code,msg == null ? 0 : msg.length());
        if (msg != null && !msg.isBlank()){
            try (final OutputStream responseBody = exchange.getResponseBody(); final OutputStreamWriter writer = new OutputStreamWriter(responseBody)){
                writer.write(msg);
            }
        }
    }

    public HttpServer getServer() {
        return server;
    }

    public File getRootDir() {
        return root;
    }
}
