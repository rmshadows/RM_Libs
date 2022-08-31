package RSA_Utils;

import com.github.xiangyuecn.rsajava.RSA_PEM;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Map;
import java.util.Objects;

public class Main {
    public static void main(String[] args) throws Exception {
        // 仅测试Java
        RSA_Utils_Test();
        // PKCS8测试
//        savePKCS8_RSA();
//        loadPKCS8_key();
//        PKCS8_Test();

        // PKCS1测试
//        savePKCS1_RSA();
//        loadPKCS1_key();

        // PKCS8转PKCS8测试
//        savePKCS8_RSA_as_PKCS1();
    }

    static void RSA_Utils_Test() throws UnsupportedEncodingException { // 自我测试
        // RSA_Utils总测试
        // 先生成密钥分别保存
        RSA_PEM rsa_pem = RSA_PKCS1_Utils.generatePKCS1_RSAKey(2048);
        RSA_PKCS1_Utils.savePKCS1_RSA_Key(rsa_pem, new File("pkcs1.pem"), true);
        RSA_PKCS1_Utils.savePKCS1_RSA_Key(rsa_pem, new File("pkcs1.pub"), false);
        Map<String, Object> key_map = RSA_Tools.PKCS1_2_PKCS8(rsa_pem);
        RSA_PKCS8_Utils.savePKCS8_RSA_Key(RSA_PKCS8_Utils.getBase64PrivateKey(key_map), new File("pkcs8.pem"),true);
        RSA_PKCS8_Utils.savePKCS8_RSA_Key(RSA_PKCS8_Utils.getBase64PublicKey(key_map), new File("pkcs8.pub"),false);
        // 然后分别加载
        PrivateKey pkcs1_prk = RSA_PKCS1_Utils.loadPKCS1_PRK(new File("pkcs1.pem"));
        PublicKey pkcs1_puk = RSA_PKCS1_Utils.loadPKCS1_PUK(new File("pkcs1.pub"));
        PrivateKey pkcs8_prk = RSA_PKCS8_Utils.loadPKCS8_PRK(new File("pkcs8.pem"));
        PublicKey pkcs8_puk = RSA_PKCS8_Utils.loadPKCS8_PUK(new File("pkcs8.pub"));
        Map<String, Object> key_map_f = RSA_PKCS8_Utils.loadPKCS1_RSA_Key_as_PKCS8(new File("pkcs1.pem"), new File("pkcs1.pub"));
        PrivateKey pkcs8_prk_f = RSA_PKCS8_Utils.getPrivateKey(key_map_f);
        PublicKey pkcs8_puk_f = RSA_PKCS8_Utils.getPublicKey(key_map_f);
        // 以下Result应该全True
        boolean result;
        result = Objects.equals(RSA_Tools.bytes2base64(pkcs1_prk.getEncoded()), RSA_Tools.bytes2base64(pkcs8_prk.getEncoded()));
        System.out.println(String.format("PKCS1 与 PKCS8加载的私钥是否相同：%b", result));
        result = Objects.equals(RSA_Tools.bytes2base64(pkcs1_puk.getEncoded()), RSA_Tools.bytes2base64(pkcs8_puk.getEncoded()));
        System.out.println(String.format("PKCS1 与 PKCS8加载的公钥是否相同：%b", result));

        result = Objects.equals(RSA_Tools.bytes2base64(pkcs8_prk_f.getEncoded()), RSA_Tools.bytes2base64(pkcs8_prk.getEncoded()));
        System.out.println(String.format("PKCS8转PKCS1 与 PKCS8加载的私钥是否相同：%b", result));
        result = Objects.equals(RSA_Tools.bytes2base64(pkcs8_puk_f.getEncoded()), RSA_Tools.bytes2base64(pkcs8_puk.getEncoded()));
        System.out.println(String.format("PKCS8转PKCS1 与 PKCS8加载的公钥是否相同：%b", result));

        result = Objects.equals(RSA_Tools.bytes2base64(pkcs8_prk_f.getEncoded()), RSA_Tools.bytes2base64(pkcs1_prk.getEncoded()));
        System.out.println(String.format("PKCS8转PKCS1 与 PKCS1加载的私钥是否相同：%b", result));
        result = Objects.equals(RSA_Tools.bytes2base64(pkcs8_puk_f.getEncoded()), RSA_Tools.bytes2base64(pkcs1_puk.getEncoded()));
        System.out.println(String.format("PKCS8转PKCS1 与 PKCS1加载的是否相同：%b", result));

        // 加密测试 任意私钥加密可用任意公钥解密
        String encrypt = RSA_Tools.bytes2base64(RSA_PKCS8_Utils.encryptByPrivateKey("妳好2022".getBytes(RSA_Tools.CHARSET), pkcs1_prk));
        System.out.println("encrypt = " + encrypt);
        System.out.println(new String(Objects.requireNonNull(RSA_PKCS8_Utils.decryptByPublicKey(RSA_Tools.base642bytes(encrypt), pkcs8_puk_f)), RSA_Tools.CHARSET));
        // 删除测试文件
        new File("pkcs1.pem").delete();
        new File("pkcs1.pub").delete();
        new File("pkcs8.pem").delete();
        new File("pkcs8.pub").delete();
    }

    static void PKCS1_Test() throws UnsupportedEncodingException {
        PrivateKey r = RSA_PKCS8_Utils.loadPKCS8_PRK(new File("test.pem"));
        PublicKey u = RSA_PKCS8_Utils.loadPKCS8_PUK(new File("test.pub"));
        byte[] todo = "待加密".getBytes(RSA_Tools.CHARSET);
        String e;
        byte[] f;
        // 签名测试
        e = RSA_PKCS8_Utils.sign(todo, r, false);
        System.out.println(e);
        boolean re = RSA_PKCS8_Utils.verify("待加密".getBytes(RSA_Tools.CHARSET), u, e, false);
        System.out.println(re);
        // 加解密
        f = RSA_PKCS8_Utils.encryptByPrivateKey(todo, r);
        System.out.println(new String(RSA_PKCS8_Utils.decryptByPublicKey(f, u), RSA_Tools.CHARSET));
        f = RSA_PKCS8_Utils.encryptByPublicKey(todo, u);
        System.out.println(new String(RSA_PKCS8_Utils.decryptByPrivateKey(f, r), RSA_Tools.CHARSET));
    }

    static void loadPKCS1_key() {
        PrivateKey prk = RSA_PKCS1_Utils.loadPKCS1_PRK(new File("prk.pem"));
        System.out.println(RSA_Tools.bytes2base64(prk.getEncoded()));
        PublicKey puk = RSA_PKCS1_Utils.loadPKCS1_PUK(new File("puk.pub"));
        System.out.println(RSA_Tools.bytes2base64(puk.getEncoded()));
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
        e = RSA_PKCS8_Utils.sign(todo, r, false);
        System.out.println(e);
        boolean re = RSA_PKCS8_Utils.verify("待加密".getBytes(RSA_Tools.CHARSET), u, e, false);
        System.out.println(re);
        // 加解密
        f = RSA_PKCS8_Utils.encryptByPrivateKey(todo, r);
        System.out.println(new String(Objects.requireNonNull(RSA_PKCS8_Utils.decryptByPublicKey(f, u)), RSA_Tools.CHARSET));
        f = RSA_PKCS8_Utils.encryptByPublicKey(todo, u);
        System.out.println(new String(Objects.requireNonNull(RSA_PKCS8_Utils.decryptByPrivateKey(f, r)), RSA_Tools.CHARSET));
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
        Map<String, Object> rsa = RSA_PKCS8_Utils.generatePKCS8_RSAKey(1024);
        RSA_PEM pem = RSA_Tools.PKCS8_2_PKCS1(RSA_PKCS8_Utils.getPrivateKey(rsa), RSA_PKCS8_Utils.getPublicKey(rsa));
        RSA_PKCS1_Utils.savePKCS1_RSA_Key(pem, new File("test.pem"), true);
    }
}