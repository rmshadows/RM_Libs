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
    private static final String CipherMode = "AES/CFB/NoPadding";// 使用CFB加密，需要设置IV

    // 填充
    private static String passwd_fill = "0";

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        AES_CFB.password = password;
    }

    public static String getPasswd_fill() {
        return passwd_fill;
    }

    public static void setPasswd_fill(String passwd_fill) {
        AES_CFB.passwd_fill = passwd_fill;
    }

    // 密码
    private static String password = "default";

    public AES_CFB(String passwd, String pwd_fill) {
        password = passwd;
//        try {
//            throw new Exception("给定的值不是控制字符！");
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
        passwd_fill = pwd_fill;
    }

    public AES_CFB(String passwd){
        password = passwd;
    }

    /**
     * 加密
     * @param password 密码
     * @param clear_content 明文
     * @return HexString 16进制字符串
     */
    public String encrypt(String password, String clear_content) {
        if(!debug_mode) {
            byte[] data = null;
            try {
                data = clear_content.getBytes("UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
            data = bytesEncrypt(data, password);
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
                data = clear_content.getBytes("UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
            data = bytesEncrypt(data, password);
            String result = AES_Tools.bytes2hex(data);
            return result;
        }else {
            return clear_content;
        }
    }

    /**
     * 解密16进制的字符串为字符串
     * @param password 密码
     * @param hex_content 密文
     * @return 字符串
     */
    public String decrypt(String password, String hex_content) {
        if(!debug_mode) {
            byte[] data = null;
            try {
                data = AES_Tools.hex2bytes(hex_content);
            } catch (Exception e) {
                e.printStackTrace();
            }
            data = bytesDecrypt(data, password);
            if (data == null)
                return null;
            String result = null;
            try {
                result = new String(data, "UTF-8");
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
            data = bytesDecrypt(data, password);
            if (data == null)
                return null;
            String result = null;
            try {
                result = new String(data, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return result;
        }else {
            return hex_content;
        }
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
        StringBuilder sb = new StringBuilder(32);
        sb.append(password);
        while (sb.length() < 32) {
            sb.append(passwd_fill);
        }
        if (sb.length() > 32) {
            sb.setLength(32);
        }
        try {
            data = sb.toString().getBytes("UTF-8");
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
    private static byte[] bytesEncrypt(byte[] content, String password) {
        try {
            // 生成加密后的密钥
            SecretKeySpec key = createKey(password);
            Cipher cipher = Cipher.getInstance(CipherMode);
            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(new byte[cipher.getBlockSize()]));
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
    private static byte[] bytesDecrypt(byte[] content, String password) {
        try {
            SecretKeySpec key = createKey(password);
            Cipher cipher = Cipher.getInstance(CipherMode);
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(new byte[cipher.getBlockSize()]));

            return cipher.doFinal(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
