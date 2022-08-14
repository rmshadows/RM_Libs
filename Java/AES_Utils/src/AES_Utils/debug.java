package AES_Utils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class debug {
    static String U = "UTF-8";
    static byte[] pwdHandler(String password, boolean isIv) throws UnsupportedEncodingException {
        byte[] data = null;
        int tlength = 32;
        if (isIv){
            tlength = 16;
        }
        if (password != null) {
            byte[] pwd_bytes = password.getBytes("UTF-8"); //一个中文3位长度，一数字1位
            if (password.length() < tlength) {
                System.arraycopy(pwd_bytes, 0, data = new byte[tlength], 0, pwd_bytes.length);
            }
            else {
                data = pwd_bytes;
            }
        }
//        System.out.println(new String(data, CHARACTER));
        return data;
    }

    public static void main(String[] args) throws UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException {
//        SecretKeySpec keySpec = new SecretKeySpec(pwdHandler("1234567890123456", false), "AES");
//        SecretKeySpec keySpec = new SecretKeySpec("12345678901234567890123456789012".getBytes(U), "AES");
        SecretKeySpec keySpec = new SecretKeySpec("1234567890123456".getBytes(U), "AES");
        IvParameterSpec ivSpec = new IvParameterSpec("1234567890123457".getBytes(U));
//        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        // 4 执行
        byte[] cipherTextBytes = cipher.doFinal("0123456789012348".getBytes(U));
        System.out.println(AES_Tools.bytes2hex(cipherTextBytes));
    }
}
