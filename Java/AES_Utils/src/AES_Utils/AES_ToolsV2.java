package AES_Utils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * AES V2 工具：PBKDF2 派生、hex 转换等。与 Node/Python V2 一致。
 * 兼容（零填充）留给 v1。
 */
public final class AES_ToolsV2 {
    public static final String CHARSET = "UTF-8";
    public static final int CBC_KEY_LENGTH = 32;
    public static final int IV_LEN = 16;
    public static final int SALT_LEN = 16;

    private static final String PBKDF2_ALG = "PBKDF2WithHmacSHA256";

    private AES_ToolsV2() {}

    public static byte[] hex2bytes(String hex) {
        if (hex == null || hex.isEmpty()) return new byte[0];
        String h = hex.toLowerCase().replaceAll("\\s", "");
        if ((h.length() & 1) != 0) throw new IllegalArgumentException("hex 长度必须为偶数");
        int len = h.length() / 2;
        byte[] out = new byte[len];
        for (int i = 0; i < len; i++) {
            out[i] = (byte) (Integer.parseInt(h.substring(i * 2, i * 2 + 2), 16) & 0xFF);
        }
        return out;
    }

    public static String bytes2hex(byte[] b) {
        if (b == null || b.length == 0) return "";
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (byte x : b) {
            String t = Integer.toHexString(x & 0xFF);
            if (t.length() == 1) sb.append('0');
            sb.append(t);
        }
        return sb.toString().toUpperCase();
    }

    /**
     * PBKDF2-HMAC-SHA256 派生 key(32 字节) + iv(16 字节)，用于 V2 CBC。
     *
     * @param password  密码（UTF-8）
     * @param salt       盐值 16 字节
     * @param iterations 迭代次数
     * @return [0]=key(32), [1]=iv(16)
     */
    public static byte[][] deriveKeyAndIV(String password, byte[] salt, int iterations) {
        try {
            PBEKeySpec spec = new PBEKeySpec(
                    (password != null ? password : "").toCharArray(),
                    salt != null ? salt : new byte[0],
                    iterations,
                    (CBC_KEY_LENGTH + IV_LEN) * 8
            );
            SecretKeyFactory f = SecretKeyFactory.getInstance(PBKDF2_ALG);
            byte[] dk = f.generateSecret(spec).getEncoded();
            byte[] key = Arrays.copyOfRange(dk, 0, CBC_KEY_LENGTH);
            byte[] iv = Arrays.copyOfRange(dk, CBC_KEY_LENGTH, CBC_KEY_LENGTH + IV_LEN);
            return new byte[][]{key, iv};
        } catch (Exception e) {
            throw new RuntimeException("PBKDF2 derive failed", e);
        }
    }

    public static byte[] randomSalt16() {
        byte[] salt = new byte[SALT_LEN];
        new SecureRandom().nextBytes(salt);
        return salt;
    }
}
