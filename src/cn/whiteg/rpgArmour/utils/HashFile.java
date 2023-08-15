package cn.whiteg.rpgArmour.utils;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;

public class HashFile {
    private final static char[] hexDigits = {'0','1','2','3','4','5','6',
            '7','8','9','a','b','c','d','e','f'};

    /**
     * 对一个文件获取md5值
     *
     * @return md5串
     * @throws NoSuchAlgorithmException
     */
    public static String getMD5(File file) throws IOException,
            NoSuchAlgorithmException {

        var messagedigest = MessageDigest.getInstance("MD5");
        try (FileInputStream in = new FileInputStream(file); FileChannel ch = in.getChannel()){
            MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY,0,
                    file.length());
            messagedigest.update(byteBuffer);
            return bufferToHex(messagedigest.digest());
        }

    }

    /***
     * 计算SHA1码
     *
     * @return String 适用于上G大的文件
     * @throws NoSuchAlgorithmException
     * */
    public static String getSha1(File file) throws OutOfMemoryError,
            IOException, NoSuchAlgorithmException {
        var messagedigest = MessageDigest.getInstance("SHA-1");
        try (FileInputStream in = new FileInputStream(file); FileChannel ch = in.getChannel()){
            MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY,0,
                    file.length());
            messagedigest.update(byteBuffer);
            return bufferToHex(messagedigest.digest());
        }
    }

    /***
     * 计算SHA1码
     *
     * @return String 适用于任何输入流
     * @throws NoSuchAlgorithmException
     * */
    public static String getSha1(InputStream inputStream) throws OutOfMemoryError,
            IOException, NoSuchAlgorithmException {
        var messagedigest = MessageDigest.getInstance("SHA-1");
        byte[] buff = new byte[2048];
        int len;
        while ((len = inputStream.read(buff)) != -1) {
            messagedigest.update(buff,0,len);
        }
        return bufferToHex(messagedigest.digest());
    }

    /**
     * 获取文件CRC32码
     *
     * @return String
     */
    public static String getCRC32(File file) {
        CRC32 crc32 = new CRC32();
        // MessageDigest.get
        try (var fileInputStream = new FileInputStream(file)){
            byte[] buffer = new byte[8192];
            int length;
            while ((length = fileInputStream.read(buffer)) != -1) {
                crc32.update(buffer,0,length);
            }
            return crc32.getValue() + "";
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @return String
     * @Description 计算二进制数据
     */
    public static String bufferToHex(byte[] bytes) {
        return bufferToHex(bytes,0,bytes.length);
    }

    public static String bufferToHex(byte[] bytes,int m,int n) {
        StringBuffer stringbuffer = new StringBuffer(2 * n);
        int k = m + n;
        for (int l = m; l < k; l++) {
            appendHexPair(bytes[l],stringbuffer);
        }
        return stringbuffer.toString();
    }

    public static void appendHexPair(byte bt,StringBuffer stringbuffer) {
        char c0 = hexDigits[(bt & 0xf0) >> 4];
        char c1 = hexDigits[bt & 0xf];
        stringbuffer.append(c0);
        stringbuffer.append(c1);
    }

}
