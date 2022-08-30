import com.github.xiangyuecn.rsajava.RSA_PEM;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class RSA_Tools {
    public static final String PKCS1_PRK_HEADER = "-----BEGIN RSA PRIVATE KEY-----";
    public static final String PKCS1_PRK_TAILER = "-----END RSA PRIVATE KEY-----";
    public static final String PKCS1_PUK_HEADER = "-----BEGIN RSA PUBLIC KEY-----";
    public static final String PKCS1_PUK_TAILER = "-----END RSA PUBLIC KEY-----";
    public static final String PKCS8_PRK_HEADER = "-----BEGIN PRIVATE KEY-----";
    public static final String PKCS8_PRK_TAILER = "-----END PRIVATE KEY-----";
    public static final String PKCS8_PUK_HEADER = "-----BEGIN PUBLIC KEY-----";
    public static final String PKCS8_PUK_TAILER = "-----END PUBLIC KEY-----";
    public static final String CHARSET = "UTF-8";

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

    /**
     * PKCS1转PKCS8
     * @param pem
     * @return
     */
    public static Map<String, Object> PKCS1_2_PKCS8(RSA_PEM pem){
        Map<String, Object> keys = new HashMap<>(2);
        try {
            keys.put(RSA_PKCS8_Utils.PRIVATE_KEY, pem.getRSAPrivateKey());
            keys.put(RSA_PKCS8_Utils.PUBLIC_KEY, pem.getRSAPublicKey());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return keys;
    }

    /**
     * PKCS8 转 PKCS1
     * @param prk
     * @param puk
     * @return
     */
    public static RSA_PEM PKCS8_2_PKCS1(PrivateKey prk, PublicKey puk){
        RSAPrivateKey rprk = (RSAPrivateKey) prk;
        RSAPublicKey rpuk = (RSAPublicKey) puk;
        return new RSA_PEM(rpuk, rprk);
    }
}
