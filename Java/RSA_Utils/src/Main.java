import java.io.UnsupportedEncodingException;

public class Main {
    public static void main(String[] args) throws UnsupportedEncodingException {
        System.out.println("Hello world!");
        String b = Code_Utils.Base64Bytes.bytes2base64("Hello world!".getBytes("UTF-8"));
        System.out.println(b);
        System.out.println(new String(Code_Utils.Base64Bytes.base642bytes(b), "UTF-8"));
    }
}