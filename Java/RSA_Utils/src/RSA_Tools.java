import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class RSA_Tools {
    /**
     * 读取二进制文件
     * @param f
     * from IO_Utils
     * @return
     */
    public static byte[] readBytesUsingBufferedInputStream(File f){
        Path path = Paths.get(f.getAbsolutePath());
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(path.toFile()));
             ByteArrayOutputStream baos = new ByteArrayOutputStream();){
            byte[] read = new byte[4];
            int r_len = -1;
            while ((r_len = bis.read(read)) != -1){
                baos.write(read, 0, r_len);
            }
            baos.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * 写入二进制文件
     * @param f
     * from IO_Utils
     * @param data
     */
    public static void writeBytesUsingBufferedOutputStream(File f, byte[] data){
        Path path = Paths.get(f.getAbsolutePath());
        try (BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(data));
             BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path.toFile()));){
            int read = -1;
            byte[] buf = new byte[1024];
            while ((read = bis.read(buf)) != -1){
                bos.write(buf, 0, read);
            }
            bos.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * base64加密
     * 传入byte数组执行base64加密
     * from Code_Utils
     * @param data byte[] 数组待编码数据
     * @return String 编码后的字符串数据
     */
    public static String bytes2base64(byte[] data) {
        // BASE64Encoder encoder = new BASE64Encoder();
        // String encode = encoder.encode(data);
        // 从JKD 9开始rt.jar包已废除，从JDK 1.8开始使用java.util.Base64.Encoder
        Base64.Encoder encoder = Base64.getEncoder();
        String encode = encoder.encodeToString(data);
        return encode;
    }

    /**
     * base64解密
     * 传入String执行base64解密
     * from Code_Utils
     * @param data String 编码数据
     * @return byte[] 解码数据的数组
     */
    public static byte[] base642bytes(String data) {
        // BASE64Decoder decoder = new BASE64Decoder();
        // byte[] buffer = decoder.decodeBuffer(data);
        // 从JKD 9开始rt.jar包已废除，从JDK 1.8开始使用java.util.Base64.Decoder
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] buffer = decoder.decode(data);
        return buffer;
    }
}
