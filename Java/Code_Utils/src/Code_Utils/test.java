package Code_Utils;

import java.io.UnsupportedEncodingException;

public class test {
    public static void main(String[] args) throws UnsupportedEncodingException {
        String test = "字符串";
        String str = "";
        System.out.printf("原始的字符串： %s\n", test);
        str = Base64Bytes.bytes2base64(test.getBytes("UTF-8"));
        System.out.printf("Base64-E: %s\n", str);
        System.out.printf("Base64-D: %s\n", new String(Base64Bytes.base642bytes(str), "UTF-8"));

        str = BytesHexString.bytes2hex(test.getBytes("UTF-8"));
        System.out.printf("HexString-E: %s\n", str);
        System.out.printf("HexString-D: %s\n", new String(BytesHexString.hex2bytes(str), "UTF-8"));
    }
}
