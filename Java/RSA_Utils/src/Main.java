import com.github.xiangyuecn.rsajava.RSA_PEM;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {
        // PKCS8测试
//        savePKCS8_RSA();
//        loadPKCS8_key();
//        PKCS8_Test();
        // PKCS1测试
//        savePKCS1_RSA();
        loadPKCS1_key();


    }

    static void PKCS1_Test() throws UnsupportedEncodingException {
        PrivateKey r = RSA_PKCS8_Utils.loadPKCS8_PRK(new File("test.pem"));
        PublicKey u = RSA_PKCS8_Utils.loadPKCS8_PUK(new File("test.pub"));
        byte[] todo = "待加密".getBytes(RSA_Tools.CHARSET);
        String e;
        byte[] f;
        // 签名测试
        e = RSA_PKCS8_Utils.sign(todo, r);
        System.out.println(e);
        boolean re = RSA_PKCS8_Utils.verify("待加密".getBytes(RSA_Tools.CHARSET), u, e);
        System.out.println(re);
        // 加解密
        f = RSA_PKCS8_Utils.encryptByPrivateKey(todo, r);
        System.out.println(new String(RSA_PKCS8_Utils.decryptByPublicKey(f, u), RSA_Tools.CHARSET));
        f = RSA_PKCS8_Utils.encryptByPublicKey(todo, u);
        System.out.println(new String(RSA_PKCS8_Utils.decryptByPrivateKey(f, r), RSA_Tools.CHARSET));
    }

    static void loadPKCS1_key() {
        RSA_PKCS1_Utils.loadPKCS1_PRK(new File("prk.pem"));
//        RSA_PKCS8_Utils.loadPKCS8_PUK(new File("puk.pub"));
    }

    static void savePKCS1_RSA(){
        RSA_PEM rp = RSA_PKCS1_Utils.generatePKCS1_RSAKey(512);
        RSA_PKCS1_Utils.savePKCS1_RSA_Key(rp, new File("test.pem"), true);
        RSA_PKCS1_Utils.savePKCS1_RSA_Key(rp, new File("test.pub"), false);
    }
    static void PKCS8_Test() throws UnsupportedEncodingException {
        PrivateKey r = RSA_PKCS8_Utils.loadPKCS8_PRK(new File("test.pem"));
        PublicKey u = RSA_PKCS8_Utils.loadPKCS8_PUK(new File("test.pub"));
        byte[] todo = "待加密".getBytes(RSA_Tools.CHARSET);
        String e;
        byte[] f;
        // 签名测试
        e = RSA_PKCS8_Utils.sign(todo, r);
        System.out.println(e);
        boolean re = RSA_PKCS8_Utils.verify("待加密".getBytes(RSA_Tools.CHARSET), u, e);
        System.out.println(re);
        // 加解密
        f = RSA_PKCS8_Utils.encryptByPrivateKey(todo, r);
        System.out.println(new String(RSA_PKCS8_Utils.decryptByPublicKey(f, u), RSA_Tools.CHARSET));
        f = RSA_PKCS8_Utils.encryptByPublicKey(todo, u);
        System.out.println(new String(RSA_PKCS8_Utils.decryptByPrivateKey(f, r), RSA_Tools.CHARSET));
        new File("test.pem").delete();
        new File("test.pub").delete();
    }

    static void loadPKCS8_key() {
        RSA_PKCS8_Utils.loadPKCS8_PRK(new File("test.pem"));
        RSA_PKCS8_Utils.loadPKCS8_PUK(new File("test.pub"));
    }

    static void savePKCS8_RSA(){
        Map<String, Object> rsa = RSA_PKCS8_Utils.generatePKCS8_RSAKey(512);
        String prk = RSA_PKCS8_Utils.getBase64PrivateKey(rsa);
        String puk = RSA_PKCS8_Utils.getBase64PublicKey(rsa);
        RSA_PKCS8_Utils.savePKCS8_RSA_Key(prk, new File("test.pem"), true);
        RSA_PKCS8_Utils.savePKCS8_RSA_Key(puk, new File("test.pub"), false);
    }

    static void savePKCS8_RSA_as_PKCS1(){
        Map<String, Object> rsa = RSA_PKCS8_Utils.generatePKCS8_RSAKey(512);
        RSA_Tools.PKCS8_2_PKCS1(RSA_PKCS8_Utils.getBase64PrivateKey(rsa), RSA_PKCS8_Utils.getBase64PublicKey(rsa));
        RSA_PKCS8_Utils.savePKCS8_RSA_Key(prk, new File("test.pem"), true);
        RSA_PKCS8_Utils.savePKCS8_RSA_Key(puk, new File("test.pub"), false);
    }
}