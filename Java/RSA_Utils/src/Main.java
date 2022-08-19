import javax.swing.*;
import java.security.Key;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {
        Map rsa = RSA_Utils.generateRSAKey(512);
//        System.out.println("rsa = " + rsa);
//        System.out.println(rsa.get("PUK"));
        String s = RSA_Utils.getPrivateKey(rsa);
        System.out.println("s = " + s);
        IOUtils.writeFile(pubEncBase64, new File("pub.txt"));
    }
}