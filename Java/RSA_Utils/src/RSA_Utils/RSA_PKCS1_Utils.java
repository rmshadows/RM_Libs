package RSA_Utils;

import com.github.xiangyuecn.rsajava.RSA_PEM;
import com.github.xiangyuecn.rsajava.RSA_Util;

import java.io.File;
import java.security.*;

public class RSA_PKCS1_Utils {
    /**
     * 加载私钥
     * @param f 私钥文件
     * @return 私钥
     */
    public static PrivateKey loadPKCS1_PRK(File f){
        try {
            byte[] read_b = RSA_Tools.readBytesUsingBufferedInputStream(f);
            String read_str = new String(read_b, RSA_Tools.CHARSET);
            return RSA_PEM.FromPEM(read_str).getRSAPrivateKey();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 加载PKCS1公钥
     * @param f 公钥文件
     * @return
     */
    public static PublicKey loadPKCS1_PUK(File f){
        byte[] read_b = RSA_Tools.readBytesUsingBufferedInputStream(f);
        String read_str = null;
        try {
            read_str = new String(read_b, RSA_Tools.CHARSET);
            return RSA_PEM.FromPEM(read_str).getRSAPublicKey();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 保存RSA密钥
     * @param pem RSA_PEM类
     * @param save_path 保存路径
     * @param isPRK 是否是私钥
     */
    public static void savePKCS1_RSA_Key(RSA_PEM pem, File save_path, boolean isPRK){
        try {
            if (isPRK){
                RSA_Tools.writeBytesUsingBufferedOutputStream(save_path, pem.ToPEM_PKCS1(false).getBytes(RSA_Tools.CHARSET));
            }else {
                RSA_Tools.writeBytesUsingBufferedOutputStream(save_path, pem.ToPEM_PKCS1(true).getBytes(RSA_Tools.CHARSET));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 返回密钥文本
     * @param pem PEM类
     * @param isPRK 是否是私钥
     * @param pkcs1 是否是pkcs1格式，否则返回PKCS8格式
     * @return 字符串文本
     */
    public static String pem2String(RSA_PEM pem, boolean isPRK, boolean pkcs1){
        try {
            if (isPRK){
                if (pkcs1){
                    return pem.ToPEM_PKCS1(false);
                }else {
                    return pem.ToPEM_PKCS8(false);
                }
            }else {
                if (pkcs1){
                    return pem.ToPEM_PKCS1(true);
                }else {
                    return pem.ToPEM_PKCS8(true);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 初始化PKCS1密钥
     * @return RSA_PEM
     */
    public static RSA_PEM generatePKCS1_RSAKey(int key_size){
        try {
            // 先生成PKCS8的密钥
            RSA_Util rsa = new RSA_Util(key_size);
            // 再转PEM类
            return rsa.ToPEM(false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}