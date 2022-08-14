package AES_Utils;

import java.io.UnsupportedEncodingException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES CFB加密工具类
 * @author 伍子夜 & Ryan
 * 参考来源：https://www.jb51.net/article/111057.htm
 */
public class AES_CFB {
    // 仅用于Debug，不加密
    private static boolean debug_mode = false;
    private static final String CipherMode = "AES/CFB/NoPadding";// 与Python默认配置兼容
    //    private static final String CipherMode = "AES/CFB/PKCS5Padding";// 使用CFB加密，需要设置IV
    // 偏移量
    private static byte[] IV;
    // 填充
    private static String passwd_padding = "0";
    // 密钥长度
    private static int passwd_len = 32;
    // 密码
    private static String password = "";
    private static final String CHARACTER = "UTF-8";

    public static int getPasswd_len() {
        return passwd_len;
    }

    public static void setPasswd_len(int passwd_len) {
        AES_CFB.passwd_len = passwd_len;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        AES_CFB.password = password;
    }

    public static String getPasswd_padding() {
        return passwd_padding;
    }

    public static void setPasswd_padding(String passwd_padding) {
        AES_CFB.passwd_padding = passwd_padding;
    }

    public static String getIV() {
        try {
            return new String(IV, CHARACTER);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setIV(String IV) {
        AES_CFB.IV = createIV(IV);
    }

    public AES_CFB(String passwd, String pwd_padding, String iv, int pwd_len) {
//        try {
//            throw new Exception("给定的值不是控制字符！");
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
        AES_CFB.passwd_padding = pwd_padding;
        AES_CFB.password = passwd;
        AES_CFB.IV = createIV(iv);
        AES_CFB.passwd_len = pwd_len;
    }

    public AES_CFB(String passwd, String pwd_padding, String iv) {
        password = passwd;
        passwd_padding = pwd_padding;
        AES_CFB.IV = createIV(iv);
    }

    public AES_CFB(String passwd, String pwd_padding) {
        password = passwd;
        passwd_padding = pwd_padding;
        AES_CFB.IV = createIV("");
    }

    public AES_CFB(String passwd){
        AES_CFB.password = passwd;
        AES_CFB.passwd_padding = "0";
        AES_CFB.IV = createIV("");
    }

    /**
     * 加密
     * @param password 密码
     * @param iv 偏移量
     * @param clear_content 明文
     * @return HexString 16进制字符串
     */
    public String encrypt(String password, String iv, String clear_content) {
        if(!debug_mode) {
            byte[] data = null;
            try {
                data = clear_content.getBytes(CHARACTER);
            } catch (Exception e) {
                e.printStackTrace();
            }
            data = bytesEncrypt(data, password, createIV(iv));
            String result = AES_Tools.bytes2hex(data);
            return result;
        }else {
            return clear_content;
        }
    }

    /**
     * 默认密码加密
     * @param clear_content String 明文
     * @return HexString 密文
     */
    public String encrypt(String clear_content) {
        if(!debug_mode) {
            byte[] data = null;
            try {
                data = clear_content.getBytes(CHARACTER);
            } catch (Exception e) {
                e.printStackTrace();
            }
            data = bytesEncrypt(data, password, IV);
            String result = AES_Tools.bytes2hex(data);
            return result;
        }else {
            return clear_content;
        }
    }

    /**
     * 解密16进制的字符串为字符串
     * @param password 密码
     * @param iv 偏移量
     * @param hex_content 密文
     * @return 字符串
     */
    public String decrypt(String password, String iv, String hex_content) {
        if(!debug_mode) {
            byte[] data = null;
            try {
                data = AES_Tools.hex2bytes(hex_content);
            } catch (Exception e) {
                e.printStackTrace();
            }
            data = bytesDecrypt(data, password, createIV(iv));
            if (data == null)
                return null;
            String result = null;
            try {
                result = new String(data, CHARACTER);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return result;
        }else {
            return hex_content;
        }
    }

    /**
     * 默认密码解密
     * @param hex_content
     * @return
     */
    public String decrypt(String hex_content) {
        if(!debug_mode) {
            byte[] data = null;
            try {
                data = AES_Tools.hex2bytes(hex_content);
            } catch (Exception e) {
                e.printStackTrace();
            }
            data = bytesDecrypt(data, password, IV);
            if (data == null)
                return null;
            String result = null;
            try {
                result = new String(data, CHARACTER);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return result;
        }else {
            return hex_content;
        }
    }

    /**
     * 生成偏移量（填充）
     * @param iv 偏移量
     * @return byte[]偏移量
     */
    private static byte[] createIV(String iv) {
        byte[] data = null;
        if (iv == null) {
            iv = "";
        }
        StringBuilder sb = new StringBuilder(16);
        sb.append(iv);
        while (sb.length() < 16) {
            sb.append(passwd_padding);
        }
        if (sb.length() > 16) {
            sb.setLength(16);
        }
        try {
            data = sb.toString().getBytes(CHARACTER);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * 生成加密后的密钥(填充密钥到32位)
     *
     * @param password 密钥
     * @return SecretKeySpec 密钥种子
     */
    private static SecretKeySpec createKey(String password) {
        byte[] data = null;
        if (password == null) {
            password = "";
        }
        StringBuilder sb = new StringBuilder(passwd_len);
        sb.append(password);
        while (sb.length() < passwd_len) {
            sb.append(passwd_padding);
        }
        if (sb.length() > passwd_len) {
            sb.setLength(passwd_len);
        }
        try {
            data = sb.toString().getBytes(CHARACTER);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new SecretKeySpec(data, "AES");
    }

    /**
     * 加密字节数据(base)
     * @param content 明文数组
     * @param password 密码
     * @return 密文数组
     */
    private static byte[] bytesEncrypt(byte[] content, String password, byte[] iv) {
        try {
            // 生成加密后的密钥
            SecretKeySpec key = createKey(password);
            Cipher cipher = Cipher.getInstance(CipherMode);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);
            return cipher.doFinal(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解密字节数组(Base)
     * @param content 密文数组
     * @param password 密码
     * @return 明文数组
     */
    private static byte[] bytesDecrypt(byte[] content, String password, byte[] iv) {
        try {
            SecretKeySpec key = createKey(password);
            Cipher cipher = Cipher.getInstance(CipherMode);
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));

            return cipher.doFinal(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
