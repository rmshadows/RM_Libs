import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.crypto.Cipher;

public class RSA_PKCS8_Utils {
    public static final String KEY_ALGORITHM = "RSA";
//    public static final String SIGNATURE_ALGORITHM = "MD5withRSA";
    public static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

    public static final String PUBLIC_KEY = "PUK";
    public static final String PRIVATE_KEY = "PRK";

    /**
     * 用私钥对信息生成数字签名
     * @param data
     *            加密数据
     * @param privateKey
     *            私钥
     * @return
     */
    public static String sign(byte[] data, PrivateKey privateKey){
        try {
            // 用私钥对信息生成数字签名
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initSign(privateKey);
            signature.update(data);
            return RSA_Tools.bytes2base64(signature.sign());
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 校验数字签名
     * @param data
     *            加密数据
     * @param publicKey
     *            公钥
     * @param sign
     *            数字签名(注意，是sign返回的Base64签名)
     * @return 校验成功返回true 失败返回false
     */
    public static boolean verify(byte[] data, PublicKey publicKey, String sign) {
        try {
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initVerify(publicKey);
            signature.update(data);
            // 验证签名是否正常
            return signature.verify(RSA_Tools.base642bytes(sign));
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 解密<br>
     * 用私钥解密
     * @param data
     * @param privateKey
     * @return
     */
    public static byte[] decryptByPrivateKey(byte[] data, PrivateKey privateKey){
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            // 对数据解密
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(data);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解密<br>
     * 用公钥解密
     * @param data
     * @param publicKey
     * @return
     */
    public static byte[] decryptByPublicKey(byte[] data, PublicKey publicKey) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            // 对数据解密
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            return cipher.doFinal(data);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 加密<br>
     * 用公钥加密
     * @param data
     * @param publicKey
     * @return
     */
    public static byte[] encryptByPublicKey(byte[] data, PublicKey publicKey) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            // 对数据加密
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(data);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 加密<br>
     * 用私钥加密
     *
     * @param data
     * @param privateKey
     * @return
     */
    public static byte[] encryptByPrivateKey(byte[] data, PrivateKey privateKey){
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            // 对数据加密
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            return cipher.doFinal(data);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 取得Base64后的私钥
     * @param keyMap
     * @return Base64后的密钥
     */
    public static String getBase64PrivateKey(Map<String, Object> keyMap) {
        Key key = (Key) keyMap.get(PRIVATE_KEY);
        return RSA_Tools.bytes2base64(key.getEncoded());
    }

    /**
     * 取得Base64后的公钥
     * @param keyMap
     * @return
     */
    public static String getBase64PublicKey(Map<String, Object> keyMap){
        Key key = (Key) keyMap.get(PUBLIC_KEY);
        return RSA_Tools.bytes2base64(key.getEncoded());
    }

    /**
     * 取得私钥
     * @param keyMap
     * @return Base64后的密钥
     */
    public static PrivateKey getPrivateKey(Map<String, Object> keyMap) {
        return (PrivateKey) keyMap.get(PRIVATE_KEY);
    }

    /**
     * 取得公钥
     * @param keyMap
     * @return PublicKey
     */
    public static PublicKey getPublicKey(Map<String, Object> keyMap){
        return (PublicKey)keyMap.get(PUBLIC_KEY);
    }

    /**
     * 加载PKCS8私钥
     * @param f 私钥文件
     * @return 私钥
     */
    public static PrivateKey loadPKCS8_PRK(File f){
        try {
            byte[] read_b = RSA_Tools.readBytesUsingBufferedInputStream(f);
            String read_str = new String(read_b, RSA_Tools.CHARSET);
            // 去头尾和换行符
            read_str = read_str.replace(RSA_Tools.PKCS8_PRK_HEADER, "");
            read_str = read_str.replace(RSA_Tools.PKCS8_PRK_TAILER, "");
            read_str = read_str.replaceAll(System.lineSeparator(), "");
            // 注意这一步要将读取的Base64密钥转bytes
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(RSA_Tools.base642bytes(read_str));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
//            System.out.println(privateKey.toString());
            return privateKey;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 加载PKCS8公钥
     * https://github.com/wustrive2008/aes-rsa-java/blob/master/src/main/java/com/wustrive/aesrsa/util/RSA.java
     * @param f 公钥文件
     * @return
     */
    public static PublicKey loadPKCS8_PUK(File f){
        byte[] read_b = RSA_Tools.readBytesUsingBufferedInputStream(f);
        String read_str = null;
        try {
            read_str = new String(read_b, RSA_Tools.CHARSET);
            // 去头尾和换行符
            read_str = read_str.replace(RSA_Tools.PKCS8_PUK_HEADER, "");
            read_str = read_str.replace(RSA_Tools.PKCS8_PUK_TAILER, "");
            read_str = read_str.replaceAll(System.lineSeparator(), "");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(RSA_Tools.base642bytes(read_str));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(keySpec);
//            System.out.println(publicKey.toString());
            return publicKey;
        } catch (UnsupportedEncodingException | InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 保存PKCS8 RSA密钥
     * @param Base64_RSA_Key Base64编码后的密钥
     * @param save_path 保存路径
     * @param isPRK 是否是私钥
     */
    public static void savePKCS8_RSA_Key(String Base64_RSA_Key, File save_path, boolean isPRK){
        String toWrite;
        if (isPRK){
            toWrite = String.format("%s\n%s\n%s", RSA_Tools.PKCS8_PRK_HEADER, Base64_RSA_Key, RSA_Tools.PKCS8_PRK_TAILER);
        }else {
            toWrite = String.format("%s\n%s\n%s", RSA_Tools.PKCS8_PUK_HEADER, Base64_RSA_Key, RSA_Tools.PKCS8_PUK_TAILER);
        }
        try {
            RSA_Tools.writeBytesUsingBufferedOutputStream(save_path, toWrite.getBytes(RSA_Tools.CHARSET));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 初始化PKCS8密钥
     * @return Map
     */
    public static Map<String, Object> generatePKCS8_RSAKey(int key_size){
        KeyPairGenerator keyPairGen = null;
        try {
            keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        keyPairGen.initialize(key_size, new SecureRandom());
        KeyPair keyPair = keyPairGen.generateKeyPair();
        // 公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        // 私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        Map<String, Object> keyMap = new HashMap<String, Object>(2);
        keyMap.put(PUBLIC_KEY, publicKey);
        keyMap.put(PRIVATE_KEY, privateKey);
        return keyMap;
    }

    /**
     * 生成PKCS8 RSA密钥对 (直接保存Encoder密钥Python不兼容)
     * @param key_size 密钥长度
     * @param alg 算法 默认RSA 可为null
     * @return KeyPair
     */
    public static KeyPair generatePKCS8_RSAKeyPair(int key_size, String alg) {
        if (Objects.equals(alg, "") || alg == null){
            alg = KEY_ALGORITHM;
        }
        KeyPairGenerator keyPairGen = null;
        try {
            keyPairGen = KeyPairGenerator.getInstance(alg);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        keyPairGen.initialize(key_size, new SecureRandom());
        return keyPairGen.generateKeyPair();
    }

    /**
     * 直接读取PKCS1密钥并转为PKCS8密钥使用
     * @param prk 私钥文件
     * @param puk 公钥文件
     * @return Map
     */
    public static Map<String, Object> loadPKCS1_RSA_Key_as_PKCS8(File prk, File puk){
        Map<String,Object> keyMap = new HashMap<String, Object>(2);
        keyMap.put(PRIVATE_KEY, RSA_PKCS1_Utils.loadPKCS1_PRK(prk));
        keyMap.put(PUBLIC_KEY, RSA_PKCS1_Utils.loadPKCS1_PUK(puk));
        return keyMap;
    }
}