import javax.swing.*;
import java.io.File;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {
//        Map rsa = RSA_Utils.generateRSAKey(512);
//        System.out.println("rsa = " + rsa);
//        String s1 = RSA_Utils.getPrivateKey(rsa);
//        String s2 = RSA_Utils.getPublicKey(rsa);
//        System.out.println("s1 = " + s1);
//        String s = String.format("%s\n%s\n%s", RSA_Utils.PRK_HEADER, s1, RSA_Utils.PRK_TAILER);
//        RSA_Tools.writeBytesUsingBufferedOutputStream(new File("test.pem"), s.getBytes(RSA_Utils.CHARSET));

        byte[] read_b = RSA_Tools.readBytesUsingBufferedInputStream(new File("t.pem"));
        String read_str = new String(read_b, RSA_Utils.CHARSET);
        read_str = read_str.replace(RSA_Utils.PRK_HEADER, "");
        read_str = read_str.replace(RSA_Utils.PRK_TAILER, "");
        read_str = read_str.replaceAll(System.lineSeparator(), "");
        System.out.println("read_str = " + read_str);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//        EncodedKeySpec privateKeySpec = new X509EncodedKeySpec(read_b);
        EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(RSA_Tools.base642bytes(read_str));
//        EncodedKeySpec privateKeySpec = new X509EncodedKeySpec(read_str.getBytes(RSA_Utils.CHARSET));
        keyFactory.generatePrivate(privateKeySpec);

    }
}