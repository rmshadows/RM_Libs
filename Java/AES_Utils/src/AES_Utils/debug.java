package AES_Utils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class debug {
    public static void main(String[] args) throws UnsupportedEncodingException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        String str = "1234567890123456你";
        byte[] key = "12345678901234567890123456789012".getBytes("UTF-8");
        byte[] iv = "1234567890123456".getBytes("UTF-8");
        // 生成加密后的密钥
        SecretKeySpec skey = new SecretKeySpec(key, "AES");
//        Cipher cipher = Cipher.getInstance("AES/CFB/PKCS5Padding");
        Cipher cipher = Cipher.getInstance("AES/CFB/NoPadding");
//        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, skey, ivParameterSpec);
        String hex = AES_Tools.bytes2hex(cipher.doFinal(str.getBytes("UTF-8")));
        System.out.println("hex = " + hex);
    }

}
