package AES_Utils;

/**
 * AES V2 测试：CBC 32 字节 + KDF（PBKDF2）
 */
public class testV2 {
    public static void main(String[] args) {
        String plain = "妳好Hello@";
        String password = "123456";
        int iterations = 10000;

        String enc = AES_V2.encryptCBC(plain, password, iterations);
        System.out.println("V2 CBC 加密 (前32=salt): " + enc.substring(0, 32) + " ...");
        String dec = AES_V2.decryptCBC(enc, password, iterations);
        System.out.println("V2 CBC 解密: " + dec);
        System.out.println(plain.equals(dec) ? "OK" : "FAIL");

        enc = AES_V2.encryptCBC(plain, password);
        dec = AES_V2.decryptCBC(enc, password);
        System.out.println("默认迭代解密: " + dec);
        System.out.println(plain.equals(dec) ? "OK" : "FAIL");
    }
}
