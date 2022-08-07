package AES_Utils;

public class AES_Tools {
    /**
     * 十六进制字符串转Byte数组
     * @param inputHexString 十六进制字符串
     * @return byte[] 字节数组
     */
    public static byte[] hex2bytes(String inputHexString) {
        if (inputHexString == null || inputHexString.length() < 2) {
            return new byte[0];
        }
        inputHexString = inputHexString.toLowerCase();
        int l = inputHexString.length() / 2;
        byte[] result = new byte[l];
        for (int i = 0; i < l; ++i) {
            String tmp = inputHexString.substring(2 * i, 2 * i + 2);
            result[i] = (byte) (Integer.parseInt(tmp, 16) & 0xFF);
        }
        return result;
    }

    /**
     * Byte数组转十六进制字符串
     * @param b 字节数组
     * @return String hex
     */
    public static String bytes2hex(byte[] b) { // 一个字节的数，
        StringBuilder sb = new StringBuilder(b.length * 2);
        String tmp;
        for (byte aB : b) {
            // 整数转成十六进制表示
            tmp = (Integer.toHexString(aB & 0XFF));
            if (tmp.length() == 1) {
                sb.append("0");
            }
            sb.append(tmp);
        }
        return sb.toString().toUpperCase(); // 转成大写
    }
}
