package AES_Utils;

import java.io.UnsupportedEncodingException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES CBC算法包
 * @author jessie
 */
public class AES_CBC {
    /**
     * 参数：
     * keyA = KeyWords 密钥
     * keyB = ivSpec vi偏移量 16位 128
     * 密 码 位  数 （keyA）= 16byte
     */
    private static final String ENCRY_ALGORITHM = "AES";//加密方法名称
    private static final String CIPHER_MODE = "AES/CBC/PKCS5Padding";//填充方式
    private static final String CHARACTER = "UTF-8";//编码
    private static int PWD_SIZE = 16;//采用128位的Key 16byte
    // 密钥 keyA
    private static String password = "default";
    // vi偏移量 keyB
    private static String iv_spec = "iv";

    /**
     * 默认16位
     * @param passwd 密码
     * @param iv vi偏移量
     */
    public AES_CBC(String passwd, String iv){
        password = passwd;
        iv_spec = iv;
    }

    /**
     * 默认16位
     * @param passwd 密码
     * @param iv vi偏移量
     * @param pwdsize 位数 16-32
     */
    public AES_CBC(String passwd, String iv, int pwdsize){
        password = passwd;
        iv_spec = iv;
        PWD_SIZE = pwdsize;
    }

    /**
     * 密钥长度补全
     * 把所给的String密钥转为PWD_SIZE长度 的 Byte数组并填充
     * @param password String KeyA or KeyB
     * @param isIv : 是否是偏移量 是的话固定长度16
     * @return Byte[] 密钥的byte数组
     * @throws UnsupportedEncodingException 忽略编码错误
     */
    private static byte[] pwdHandler(String password, boolean isIv) throws UnsupportedEncodingException {
        byte[] data = null;
        if (!isIv){
            if (password != null) {
                byte[] bytes = password.getBytes(CHARACTER); //一个中文3位长度，一数字1位
                if (password.length() < PWD_SIZE) {
                    System.arraycopy(bytes, 0, data = new byte[PWD_SIZE], 0, bytes.length);
                }
                else {
                    data = bytes;
                }
            }
        }else {
            if (password != null) {
                byte[] bytes = password.getBytes(CHARACTER); //一个中文3位长度，一数字1位
                if (password.length() < 16) {
                    System.arraycopy(bytes, 0, data = new byte[16], 0, bytes.length);
                }
                else {
                    data = bytes;
                }
            }
        }
        return data;
    }

    /**
     * 将明文密码加密成密文密码
     * @param clearText String 明文密码
     * @return String 密文密码
     */
    public String encrypt(String clearText) {
        String encoded = null;
        try{
            byte[] a = clearText.getBytes(CHARACTER);//明文密码转字节数组
            // keyA
            byte[] b = pwdHandler(password, false);
            // keyB
            byte[] c = pwdHandler(iv_spec, true);
            byte[] d = null;
            d =bytesEncrypt(a, b, c);
            encoded = AES_Tools.bytes2hex(d);
        }catch (Exception e){
            e.printStackTrace();
        }
        return encoded;
    }

    /**
     * 将密文密码解密成明文密码
     * @param cipherText String 密文密码
     * @return String 明文密码
     */
    public String decrypt(String cipherText) {
        String decoded = null;
        try{
            byte[] a = AES_Tools.hex2bytes(cipherText);
            //keyA
            byte[] b = b = pwdHandler(password, false);
            //keyB
            byte[] c = pwdHandler(iv_spec, true);
            byte[] d = bytesDecrypt(a, b, c);
            decoded = new String(d, CHARACTER);
        }catch (Exception e){
            e.printStackTrace();
        }
        return decoded;
    }

    /**
     * 将明文密码加密成密文密码
     * @param pwd 密码
     * @param iv vi偏移量
     * @param clearText 明文
     * @return HexString密文
     */
    public String encrypt(String pwd, String iv, String clearText) {
        String encoded = null;
        try{
            byte[] a = clearText.getBytes(CHARACTER);//明文密码转字节数组
            // keyA
            byte[] b = pwdHandler(pwd, false);
            // keyB
            byte[] c = pwdHandler(iv, true);
            byte[] d = null;
            d =bytesEncrypt(a, b, c);
            encoded = AES_Tools.bytes2hex(d);
        }catch (Exception e){
            e.printStackTrace();
        }
        return encoded;
    }

    /**
     * 将密文密码解密成明文密码
     * @param pwd 密码
     * @param iv vi偏移量
     * @param cipherText hexString密文
     * @return 明文
     */
    public String decrypt(String pwd, String iv, String cipherText) {
        String decoded = null;
        try{
            byte[] a = AES_Tools.hex2bytes(cipherText);
            //keyA
            byte[] b = b = pwdHandler(pwd, false);
            //keyB
            byte[] c = pwdHandler(iv, true);
            byte[] d = bytesDecrypt(a, b, c);
            decoded = new String(d, CHARACTER);
        }catch (Exception e){
            e.printStackTrace();
        }
        return decoded;
    }

    /**
     * 最原始的AES加密器
     * 将提供的byte数组的‘明文’和‘密钥’转化为 输出字节数字
     * @param clearTextBytes byte[] 明文密码
     * @param pwdBytes byte[] KeyA
     * @return byte[]
     */
    private static byte[] bytesEncrypt(byte[] clearTextBytes, byte[] pwdBytes, byte[] ivBytes) {
        try {
            //参数要求：keySpec、ivSpec
            // 1 获取加密密钥
            SecretKeySpec keySpec = new SecretKeySpec(pwdBytes, ENCRY_ALGORITHM);
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
            // 2 获取Cipher实例
            Cipher cipher = Cipher.getInstance(CIPHER_MODE);
            // 查看数据块位数 默认为16（byte） * 8 =128 bit
            //System.out.println("数据块位数(byte)：" + cipher.getBlockSize());
            // 3 初始化Cipher实例。设置执行模式以及加密密钥
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            // 4 执行
            byte[] cipherTextBytes = cipher.doFinal(clearTextBytes);
//            System.out.println("加密完成。");
            // 5 返回密文字符集
            return cipherTextBytes;
        } catch (Exception e) {
			e.printStackTrace();
        }
        //加密失败
        System.out.println("加密失败!");
        return null;
    }

    /**
     * 最原始的AES解码器
     * 将提供的byte数组的‘密文’和‘密钥’转化为 输出字节数字
     * @param cipherTextBytes byte[] 密文
     * @param pwdBytes byte[] 密码KeyA
     * @return byte[]
     */
    private static byte[] bytesDecrypt(byte[] cipherTextBytes, byte[] pwdBytes, byte[] ivBytes) {
        //参数设置：keySpec、ivSpec
        try {
            // 1 获取解密密钥
            SecretKeySpec keySpec = new SecretKeySpec(pwdBytes, ENCRY_ALGORITHM);
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
            // 2 获取Cipher实例
            Cipher cipher = Cipher.getInstance(CIPHER_MODE);
            // 查看数据块位数 默认为16（byte） * 8 =128 bit
            //System.out.println("数据块位数(byte)：" + cipher.getBlockSize());
            // 3 初始化Cipher实例。设置执行模式以及加密密钥
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            // 4 执行
            byte[] clearTextBytes = cipher.doFinal(cipherTextBytes);
            // 5 返回明文字符集
            return clearTextBytes;
        } catch (Exception e) {
			e.printStackTrace();
        }
        // 解密错误 返回null
//        System.out.println("解密失败!");
        return null;
    }
}