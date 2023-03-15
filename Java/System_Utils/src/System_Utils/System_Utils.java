package System_Utils;

import Datetime_Utils.Datetime_Utils;
import IO_Utils.IO_Utils;

import javax.swing.*;
import java.io.*;
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


    public static LinkedList<LinkedList<String>> execCommandByRuntime(String cmd,
                                                          File logfile,
                                                          boolean windowsOptionSetCharsetToGBK,
                                                          boolean linuxOptionSetGnomeTerminalVisible,
                                                          int optionTimestampMode,
                                                          boolean verbose){
        // 标准输出 标准错误 执行结果
        LinkedList<LinkedList<String>> result = new LinkedList<>();
        LinkedList<String> stdo = new LinkedList<>();
        LinkedList<String> stde = new LinkedList<>();
        LinkedList<String> exit_code = new LinkedList<>();

        // 如果是Windows
        if ((int)checkSystemType().get(0) == 0){
            cmd = "cmd.exe /c " + cmd;
        }
        // 如果是Linux 且使用GNOME弹窗运行
        else if ((int)checkSystemType().get(0) == 1 && linuxOptionSetGnomeTerminalVisible) {
            cmd = "gnome-terminal -- " + cmd;
        }
        String charset = "UTF-8";
        if (windowsOptionSetCharsetToGBK){
            // 部分系统需要使用gbk编码解决输出乱码问题
            charset = "gbk";
        }
        try {
            String line;
            Process process = Runtime.getRuntime().exec(cmd);
            System.out.println("输出流：");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(),charset));
            while ((line = bufferedReader.readLine()) != null) {
                if (optionTimestampMode == 1){
                    // 时间戳
                    line = String.valueOf(Datetime_Utils.getTimeStampNow(true)) + ": " + line;
                } else if (optionTimestampMode == 2) {
                    // 日期时间
                    line = String.valueOf(Datetime_Utils.getDateTimeNow(null)) + ": " + line;
                } else if (optionTimestampMode == 3) {
                    // 日期时区时间
                    line = String.valueOf(Datetime_Utils.getZoneDateTimeNow(null)) + ": " + line;
                }
                stdo.add(line);
                if(verbose){//TODO
                    System.out.println("Stdout: " + line);
                }
            }
            System.out.println("错误流：");
            bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream(),charset));
            while ((line = bufferedReader.readLine()) != null) {
                if (optionTimestampMode == 1){
                    // 时间戳
                    line = String.valueOf(Datetime_Utils.getTimeStampNow(true)) + ": " + line;
                } else if (optionTimestampMode == 2) {
                    // 日期时间
                    line = String.valueOf(Datetime_Utils.getDateTimeNow(null)) + ": " + line;
                } else if (optionTimestampMode == 3) {
                    // 日期时区时间
                    line = String.valueOf(Datetime_Utils.getZoneDateTimeNow(null)) + ": " + line;
                }

                stde.add(line);
                if(verbose){
                    System.out.println("ERROR: " + line);
                }
            }
            process.waitFor();
            exit_code.add(String.valueOf(process.exitValue()));
            if (verbose){
                System.out.printf("$?: %d", process.exitValue());
            }
            // 写入日志
            if (logfile != null){
                LinkedList<String> headers = new LinkedList<>();
                headers.add(String.format("%s 的标准输出: %s", cmd, File.separator));
                headers.add(String.format("%s 的标准错误: %s", cmd, File.separator));
                headers.add(String.format("%s 的退出代码: %s", cmd, File.separator));
                IO_Utils.writeUsingBufferedWriter(logfile,
                        IO_Utils.returnLinkedListString(headers.get(0)), true);
                IO_Utils.writeUsingBufferedWriter(logfile, stdo, true);
                IO_Utils.writeUsingBufferedWriter(logfile,
                        IO_Utils.returnLinkedListString(headers.get(1)), true);
                IO_Utils.writeUsingBufferedWriter(logfile, stde, true);
                IO_Utils.writeUsingBufferedWriter(logfile,
                        IO_Utils.returnLinkedListString(headers.get(2)), true);
                IO_Utils.writeUsingBufferedWriter(logfile, exit_code, true);
            }
        }catch (Exception e){
            if(verbose){
                System.out.printf("====ERROR: %s", cmd);
                System.out.println(e.getLocalizedMessage());
            }
            stde.add(e.getLocalizedMessage());
        }
        result.add(stdo);
        result.add(stde);
        result.add(exit_code);
        return result;
    }

    public static void execCommandByProcessBuilder(String stam, File logfile){
        BufferedReader br = null;
        try {
            File file = new File("daemonTmpF");
            File tmpFile = new File("daemonTmpF" + File.pathSeparator + "tempf.tmp"); //新建一个用来存储结果的缓存文件
            if (!file.exists()){
                file.mkdirs();
            }
            if(!tmpFile.exists()) {
                tmpFile.createNewFile();
            }
            ProcessBuilder pb = new ProcessBuilder().command("cmd.exe", "/c", stam).inheritIO();
            pb.redirectErrorStream(true);//这里是把控制台中的红字变成了黑字，用通常的方法其实获取不到，控制台的结果是pb.start()方法内部输出的。
            pb.redirectOutput(tmpFile);//把执行结果输出。
            pb.start().waitFor();//等待语句执行完成，否则可能会读不到结果。
            InputStream in = new FileInputStream(tmpFile);
            br= new BufferedReader(new InputStreamReader(in));
            String line = null;
            while((line = br.readLine()) != null) {
                System.out.println(line);
            }
            br.close();
            br = null;
            tmpFile.delete();//卸磨杀驴。
            System.out.println("执行完成");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 文件、文件夹操作
    public static void mkdir(){

    }

}
