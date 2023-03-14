package System_Utils;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.LinkedList;

/**
 * Java 系统类
 */
public class System_Utils {

    /**
     * 判断系统是Windows(0)、Linux(1)或者MacOS(2)或者其他（-1）
     * Linux
     * 6.0.0-0.deb11.6-amd64
     * amd64
     * http://lopica.sourceforge.net/os.html
     * @return 系统编号(int)+系统(String)+版本(String)+架构(String)
     */
    public static LinkedList<Object> checkSystemType(){
        LinkedList<Object> result = new LinkedList<>();
        String st = System.getProperty("os.name").toUpperCase();
        if (st.contains("LINUX")){
            result.add(1);
        } else if (st.contains("WINDOWS")) {
            result.add(0);
        } else if (st.contains("MAC")) {
            result.add(2);
        }else {
            result.add(-1);
        }
        result.add(System.getProperty("os.name"));
        result.add(System.getProperty("os.version"));
        result.add(System.getProperty("os.arch"));
//        System.getProperties();
//        System.out.println( System.getProperty("os.name") );
//        System.out.println( System.getProperty("os.version") );
//        System.out.println( System.getProperty("os.arch") );
        return result;
    }

    public static LinkedList<String> execCommandByRuntime(String cmd, boolean setGBK, boolean verbose){
        LinkedList<String> result = new LinkedList<>();
        // 如果是Windows
        if ((int)checkSystemType().get(0) == 0){
            cmd = "cmd.exe /c " + cmd;
        }
        String charset = "UTF-8";
        if (setGBK){
            // 部分系统需要使用gbk编码解决输出乱码问题
            charset = "gbk";
        }
        try {
            String line;
            Process process = Runtime.getRuntime().exec(cmd);
            System.out.println("输出流：");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(),charset));
            while ((line = bufferedReader.readLine()) != null) {
                result.add(line);
                if(verbose){
                    System.out.println(line);
                }
            }
            System.out.println("错误流：");
            bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream(),charset));
            while ((line = bufferedReader.readLine()) != null) {
                result.add(line);
                if(verbose){
                    System.out.println(line);
                }
            }
            process.waitFor();
            result.add(String.valueOf(process.exitValue()));
            if (verbose){
                System.out.printf("$?: %d", process.exitValue());
            }
        }catch (Exception e){
            if(verbose){
                System.out.printf("====ERROR: %s", cmd);
                System.out.println(e.getLocalizedMessage());
            }
            result.add(e.getLocalizedMessage());
        }
        return result;
    }




}
