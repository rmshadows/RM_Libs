package AES_Utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * AES V2：仅 CBC，32 字节密钥 + KDF（PBKDF2）派生。
 * 兼容（零填充、16/24 位）留给 v1（AES_CBC 等）。
 * <p>
 * 提供两类接口：
 * <ul>
 *   <li><b>String（hex）</b>：明文/密文为字符串，密文为大写十六进制，便于存配置/JSON/数据库。</li>
 *   <li><b>byte[]（二进制）</b>：明文/密文为字节数组，AES 原生形式，适合文件、二进制载荷。</li>
 * </ul>
 */
public final class AES_V2 {

    public static final int DEFAULT_ITERATIONS = 10000;
    /** 二进制密文前 16 字节为 salt，之后为 CBC 密文。 */
    public static final int SALT_LEN = AES_ToolsV2.SALT_LEN;

    private AES_V2() {}

    // ---------- String（hex）接口：密文 = salt_hex(32) + cipher_hex ----------

    /**
     * V2 CBC 加密：32 字节密钥，PBKDF2 派生。返回密文为大写十六进制字符串。
     *
     * @param plainText  明文
     * @param password   密码
     * @param iterations 迭代次数，建议 ≥ 10000
     */
    public static String encryptCBC(String plainText, String password, int iterations) {
        byte[] plain = plainText.getBytes(StandardCharsets.UTF_8);
        byte[] out = encryptCBC(plain, password, iterations);
        return AES_ToolsV2.bytes2hex(out);
    }

    /**
     * V2 CBC 解密：密文为十六进制字符串，前 32 个字符为 salt。
     */
    public static String decryptCBC(String cipherHex, String password, int iterations) {
        byte[] cipherBytes = AES_ToolsV2.hex2bytes(cipherHex == null ? "" : cipherHex);
        byte[] plainBytes = decryptCBC(cipherBytes, password, iterations);
        return new String(plainBytes, StandardCharsets.UTF_8);
    }

    public static String encryptCBC(String plainText, String password) {
        return encryptCBC(plainText, password, DEFAULT_ITERATIONS);
    }

    public static String decryptCBC(String cipherHex, String password) {
        return decryptCBC(cipherHex, password, DEFAULT_ITERATIONS);
    }

    // ---------- byte[]（二进制）接口：密文 = salt(16 字节) + CBC 密文 ----------

    /**
     * V2 CBC 加密，输入输出均为二进制。返回格式：前 16 字节 salt + 后续 AES 密文。
     *
     * @param plainBytes 明文字节数组（可含任意二进制）
     * @param password   密码
     * @param iterations 迭代次数
     * @return salt(16) + cipherBytes，总长度 16 + cipherLen
     */
    public static byte[] encryptCBC(byte[] plainBytes, String password, int iterations) {
        byte[] salt = AES_ToolsV2.randomSalt16();
        byte[][] kv = AES_ToolsV2.deriveKeyAndIV(password, salt, iterations);
        byte[] cipherBytes = doCBCEncrypt(plainBytes != null ? plainBytes : new byte[0], kv[0], kv[1]);
        byte[] result = new byte[SALT_LEN + cipherBytes.length];
        System.arraycopy(salt, 0, result, 0, SALT_LEN);
        System.arraycopy(cipherBytes, 0, result, SALT_LEN, cipherBytes.length);
        return result;
    }

    /**
     * V2 CBC 解密，输入输出均为二进制。输入格式：前 16 字节 salt + 后续 AES 密文。
     *
     * @param cipherBytes 密文（salt + 密文，至少 17 字节）
     * @param password    密码
     * @param iterations  迭代次数
     * @return 明文字节数组
     */
    public static byte[] decryptCBC(byte[] cipherBytes, String password, int iterations) {
        if (cipherBytes == null || cipherBytes.length <= SALT_LEN) {
            throw new IllegalArgumentException("密文过短，至少需要 " + (SALT_LEN + 1) + " 字节（salt + 至少 1 字节密文）");
        }
        byte[] salt = new byte[SALT_LEN];
        System.arraycopy(cipherBytes, 0, salt, 0, SALT_LEN);
        byte[] cipherOnly = new byte[cipherBytes.length - SALT_LEN];
        System.arraycopy(cipherBytes, SALT_LEN, cipherOnly, 0, cipherOnly.length);
        byte[][] kv = AES_ToolsV2.deriveKeyAndIV(password, salt, iterations);
        return doCBCDecrypt(cipherOnly, kv[0], kv[1]);
    }

    public static byte[] encryptCBC(byte[] plainBytes, String password) {
        return encryptCBC(plainBytes, password, DEFAULT_ITERATIONS);
    }

    public static byte[] decryptCBC(byte[] cipherBytes, String password) {
        return decryptCBC(cipherBytes, password, DEFAULT_ITERATIONS);
    }

    private static byte[] doCBCEncrypt(byte[] plain, byte[] keyBytes, byte[] ivBytes) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(ivBytes));
            return cipher.doFinal(plain);
        } catch (Exception e) {
            throw new RuntimeException("AES V2 CBC encrypt failed", e);
        }
    }

    private static byte[] doCBCDecrypt(byte[] cipherBytes, byte[] keyBytes, byte[] ivBytes) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(ivBytes));
            return cipher.doFinal(cipherBytes);
        } catch (Exception e) {
            throw new RuntimeException("AES V2 CBC decrypt failed", e);
        }
    }
}
