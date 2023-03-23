package System_Utils;

import Datetime_Utils.Datetime_Utils;
import IO_Utils.IO_Utils;

import java.io.*;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * Java 系统类
 */
public class System_Utils {
    // Opration System
    /**
     * 判断系统是Windows(0)、Linux(1)或者MacOS(2)或者其他（-1）
     * Linux
     * 6.0.0-0.deb11.6-amd64
     * amd64
     * http://lopica.sourceforge.net/os.html
     * @return 系统编号(String)+系统(String)+版本(String)+架构(String)
     */
    public static LinkedList<String> checkSystemType(){
        LinkedList<String> result = new LinkedList<>();
        String st = System.getProperty("os.name").toUpperCase();
        if (st.contains("LINUX")){
            result.add("1");
        } else if (st.contains("WINDOWS")) {
            result.add("0");
        } else if (st.contains("MAC")) {
            result.add("2");
        }else {
            result.add("-1");
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

    /**
     * 是否是Windows系统
     * @return
     */
    public static boolean isWindows(){
        if(Integer.valueOf(checkSystemType().get(0)) == 0){
            return true;
        }else {
            return false;
        }
    }

    // Execute Command
    /**
     * 使用Runtime运行命令
     * 注意：无法运行 ls && cat xxxx 这一类连续的命令
     * @param cmd 字符串命令
     * @param logfile 日志文件，如果不生成，请填写null。仅提供长度一位：标准、错误全部在一个文件。提供两位：标准和错误分开输出
     * @param optionIfWindowsSetCharsetToGBK 如果是Windows系统，启用GBK编码
     * @param optionIfLinuxSetGnomeTerminalVisible 如果是Linux系统，使用GNOME Terminal运行
     * @param optionTimestampMode 文件输出带时间戳的模式
     *                            其他: 不带时间
     *                            1：LocalDateTime 2023-03-20T21:57:15.676611
     *                            2:ZonedDateTime 2023-03-20T21:57:15.677782+08:00[Asia/Shanghai]
     *                            3:时间-中文-毫秒 2023年03月20日22时02分40.602148秒
     *                            4:毫秒时间戳) 1679320718584
     * @param verbose 是否直接输出到命令行
     * @param timeout 超时 小于等于0忽略
     * @param timeUnit 时间单位，如果timeout小于等于0，这个参数无效
     * @return LinkedList<LinkedList<String>> 列表1(0):标准输出 列表2(1):标准错误 列表3(2):退出码
     *
     * 其他： 使用Runtime执行命令感觉是比较方便的，直接可以将命令写入exec()中。如果仅仅是这样写的话，转码是会一直卡死在那里的。而你将命令直接放到终端中执行，又不会出现问题。
     * 原因就在转码时候，会输出大量的信息(标准输出和标准错误)，如果不清理java的缓存区，就会导致缓存区满而命令无法继续执行的情况。
     * 那么解决办法肯定就是清理缓存区，而使用Runtime清理的话，你至少得再开另外一个线程，才能同时getInputStream和getErrorStream，这样并不是我喜欢的，所以便采用了ProcessBuilder
     */
    public static LinkedList<LinkedList<String>> execCommandByRuntime(String cmd,
                                                          LinkedList<File> logfile,
                                                          boolean optionIfWindowsSetCharsetToGBK,
                                                          boolean optionIfLinuxSetGnomeTerminalVisible,
                                                          int optionTimestampMode,
                                                          boolean verbose,
                                                                      long timeout,
                                                                      TimeUnit timeUnit){

        // 标准输出 标准错误 执行结果
        LinkedList<LinkedList<String>> result = new LinkedList<>();
        LinkedList<String> stdout = new LinkedList<>();
        LinkedList<String> stderr = new LinkedList<>();
        LinkedList<String> exit_code = new LinkedList<>();
        boolean setLog = logfile != null;
        // 检查logfile参数是否错误（不允许多位）
        if (setLog){
            if (logfile.size() == 0){
                try {
                    throw new FileNotFoundException("请提供日志文件列表，最多两位：标准输出日志、错误日志；最少一位：日志文件");
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            } else if (logfile.size() > 2){
                throw new IndexOutOfBoundsException("给定列表超出指定范围，最多两位：标准输出日志、错误日志");
            }
        }

        // 如果是Windows
        if (Integer.valueOf(checkSystemType().get(0)) == 0){
            cmd = "cmd.exe /c " + cmd;
        }
        // 如果是Linux 且使用GNOME弹窗运行
        else if (Integer.valueOf(checkSystemType().get(0)) == 1 && optionIfLinuxSetGnomeTerminalVisible) {
            cmd = "gnome-terminal -- " + cmd;
        }
        String charset = "UTF-8";
        // 部分系统需要使用gbk编码解决输出乱码问题
        // 如果是Windows系统，启用GBK
        if (Integer.valueOf(checkSystemType().get(0)) == 0 && optionIfWindowsSetCharsetToGBK){
            charset = "gbk";
        }
        try {
            String line;
            Process process = Runtime.getRuntime().exec(cmd);
            System.out.println("输出流：");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(),charset));
            while ((line = bufferedReader.readLine()) != null) {
                // 修改下面时间格式的，请同步修改错误输出部分
                if (optionTimestampMode == 1){
                    // 日期时间
                    line = Datetime_Utils.getDateTimeNow(null) + ": " + line;
                } else if (optionTimestampMode == 2) {
                    // 日期时区时间
                    line = Datetime_Utils.getZoneDateTimeNow(null) + ": " + line;
                } else if (optionTimestampMode == 3) {
                    // 日期时间 中文
                    LocalDateTime localDateTime = Datetime_Utils.getDateTimeNow(null);
                    localDateTime.format(Datetime_Utils.getFormatter("yyyy年MM月dd日HH时mm分ss.SSSSSS秒"));
                    line = Datetime_Utils.getDateTimeNow(null) + ": " + line;
                } else if (optionTimestampMode == 4) {
                    // 时间戳
                    line = Datetime_Utils.getTimeStampNow(true) + ": " + line;
                }
                stdout.add(line);
                if(verbose){
                    System.out.println("【" + cmd + "】 STDOUT: "+ line);
                }
            }
            System.out.println("错误流：");
            bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream(),charset));
            while ((line = bufferedReader.readLine()) != null) {
                if (optionTimestampMode == 1){
                    // 日期时间
                    line = Datetime_Utils.getDateTimeNow(null) + ": " + line;
                } else if (optionTimestampMode == 2) {
                    // 日期时区时间
                    line = Datetime_Utils.getZoneDateTimeNow(null) + ": " + line;
                } else if (optionTimestampMode == 3) {
                    // 日期时间 中文
                    LocalDateTime localDateTime = Datetime_Utils.getDateTimeNow(null);
                    localDateTime.format(Datetime_Utils.getFormatter("yyyy年MM月dd日HH时mm分ss.SSSSSS秒"));
                    line = Datetime_Utils.getDateTimeNow(null) + ": " + line;
                } else if (optionTimestampMode == 4) {
                    // 时间戳
                    line = Datetime_Utils.getTimeStampNow(true) + ": " + line;
                }
                stderr.add(line);
                if(verbose){
                    System.out.println("【" + cmd + "】 STDERR: " + line);
                }
            }
            // https://www.cnblogs.com/bencakes/p/6139477.html
            // 其中waitFor()方法会阻塞当前进程，直到命令执行结束。而exitValue不会阻塞进程，但是调用exitValue的时候，如果命令没有执行完成就会报错，感觉这样设计挺奇怪的。
            if (timeout <= 0){
                process.waitFor();
            }else {
                process.waitFor(timeout, timeUnit);
            }
            exit_code.add(String.valueOf(process.exitValue()));
            if (verbose){
                System.out.printf("$?: %d", process.exitValue());
            }
        }catch (Exception e){
            if(verbose){
                System.out.printf("====>>>>Runtme exec error: %s", cmd);
                System.out.println(e.getLocalizedMessage());
            }
            stderr.add("【" + cmd + "】Runtime error: " + e.getLocalizedMessage());
        }
        // 写入日志
        if (setLog){
            LinkedList<String> headers = new LinkedList<>();
            headers.add(String.format("====>>>>【%s】 的标准输出 %s", cmd, ": "));
            headers.add(String.format("====>>>>【%s】 的标准错误 %s", cmd, ": "));
            headers.add(String.format("====>>>>【%s】 的退出代码 %s", cmd, ": "));
            if (logfile.size() == 1){
                // 输出、错误都在一个文件
                // 写入标准输出
                // 写入头(仅第一行)
                IO_Utils.writeUsingBufferedWriter(logfile.get(0),
                        IO_Utils.returnLinkedListString(headers.get(0)), true, true);
                IO_Utils.writeUsingBufferedWriter(logfile.get(0), stdout, true, true);
                // 写入标准错误
                IO_Utils.writeUsingBufferedWriter(logfile.get(0),
                        IO_Utils.returnLinkedListString(headers.get(1)), true, true);
                IO_Utils.writeUsingBufferedWriter(logfile.get(0), stderr, true, true);
                // 最后写入退出代码
                IO_Utils.writeUsingBufferedWriter(logfile.get(0),
                        IO_Utils.returnLinkedListString(headers.get(2)), true, false);
                IO_Utils.writeUsingBufferedWriter(logfile.get(0), exit_code, true, true);
            }// 下面的其他情况默认读取1 2位了
            else {
                IO_Utils.writeUsingBufferedWriter(logfile.get(0),
                        IO_Utils.returnLinkedListString(headers.get(0)), true, true);
                IO_Utils.writeUsingBufferedWriter(logfile.get(0), stdout, true, true);
                // 写入标准错误
                IO_Utils.writeUsingBufferedWriter(logfile.get(1),
                        IO_Utils.returnLinkedListString(headers.get(1)), true, true);
                IO_Utils.writeUsingBufferedWriter(logfile.get(1), stderr, true, true);
                // 最后写入退出代码
                IO_Utils.writeUsingBufferedWriter(logfile.get(0),
                        IO_Utils.returnLinkedListString(headers.get(2)), true, false);
                IO_Utils.writeUsingBufferedWriter(logfile.get(0), exit_code, true, true);
                IO_Utils.writeUsingBufferedWriter(logfile.get(1),
                        IO_Utils.returnLinkedListString(headers.get(2)), true, false);
                IO_Utils.writeUsingBufferedWriter(logfile.get(1), exit_code, true, true);
            }
        }
        result.add(stdout);
        result.add(stderr);
        result.add(exit_code);
        return result;
    }

    /**
     * 仅运行命令
     * 使用Runtime运行命令
     * 注意：无法运行 ls && cat xxxx 这一类连续的命令
     * @param cmd 字符串命令
     */
    public static LinkedList<LinkedList<String>> execCommandByRuntime(String cmd){
        // 标准输出 标准错误 执行结果
        LinkedList<LinkedList<String>> result = new LinkedList<>();
        LinkedList<String> stdout = new LinkedList<>();
        LinkedList<String> stderr = new LinkedList<>();
        LinkedList<String> exit_code = new LinkedList<>();
        // 如果是Windows
        if (Integer.parseInt(checkSystemType().get(0)) == 0){
            cmd = "cmd.exe /c " + cmd;
        }
        String charset = "UTF-8";
        try {
            String line;
            Process process = Runtime.getRuntime().exec(cmd);
            System.out.println("输出流：");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(),charset));
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
                stdout.add(line);
            }
            System.out.println("错误流：");
            bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream(),charset));
            while ((line = bufferedReader.readLine()) != null) {
                stderr.add(line);
                System.out.println(line);
            }
            // 超时5分钟
            process.waitFor(5, TimeUnit.MINUTES);
            exit_code.add(String.valueOf(process.exitValue()));
        }catch (Exception e){
            System.out.printf("====>>>>Runtme exec error: %s", cmd);
            System.out.println(e.getLocalizedMessage());
            stderr.add("【" + cmd + "】Runtime error: " + e.getLocalizedMessage());
        }
        result.add(stdout);
        result.add(stderr);
        result.add(exit_code);
        return result;
    }

    /**
     * 仅运行命令（不能带空格）
     * @param cmd
     */
    public static String execCommandByProcessBuilder(String cmd){
        String r = "";
        try {
            ProcessBuilder pb = null;
            // 如果是Windows
            if (isWindows()){
                pb = new ProcessBuilder().command("cmd.exe", "/c", cmd);
            }else {
                // .inheritIO()将直接输出，line为空
//                pb = new ProcessBuilder().command(cmd).inheritIO();
                pb = new ProcessBuilder().command(cmd);
            }
            pb.redirectErrorStream(true);
            BufferedReader br = null;
            Process process = pb.start();
            InputStream in = process.getInputStream();
            br = new BufferedReader(new InputStreamReader(in));
            String line = null;
            while((line = br.readLine()) != null) {
                System.out.println(line);
                r += (line + "\n");
            }
            process.waitFor();
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return r;
    }

    /**
     * 仅运行命令（允许空格）
     * @param cmd
     * @return
     */
    public static String execCommandByProcessBuilder(String[] cmd){
        String r = "";
        try {
            ProcessBuilder pb = null;
            // 如果是Windows
            if (isWindows()){
                String[] c = new String[]{"cmd.exe", "/c"};
                cmd = (String[])mergeArrays(c, cmd);
            }
            pb = new ProcessBuilder().command(cmd);
            pb.redirectErrorStream(true);
            Process process = pb.start();
            process.waitFor();
            BufferedReader br = null;
            InputStream in = process.getInputStream();
            br= new BufferedReader(new InputStreamReader(in));
            String line = null;
            while((line = br.readLine()) != null) {
                System.out.println(line);
                r += (line + "\n");
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return r;
    }


    /**
     * 使用ProcessBuilder运行命令
     * @param cmd 命令（不允许空格）
     * @param logfile null为无日志文件。每次运行日志都会被覆盖！如果重定向错误日志，请提供一个日志文件。如果没有重定向，请提供两个
     * @param redirectErrorStream 是否重定向错误（一般为是）
     * @param timeout 超时 小于等于0时无效
     * @param timeUnit 单位
     * @return 标准输出 标准错误 退出码
     */
    public static LinkedList<LinkedList<String>> execCommandByProcessBuilder(String cmd,
                                                                             File[] logfile,
                                                                             boolean redirectErrorStream,
                                                                             long timeout,
                                                                             TimeUnit timeUnit){
        boolean delLog = false;
        LinkedList<LinkedList<String>> r = new LinkedList<>();
        LinkedList<String> stdo = new LinkedList<>();
        LinkedList<String> stde = new LinkedList<>();
        LinkedList<String> exitcode = new LinkedList<>();
        try {
            BufferedReader br = null;
            if (logfile == null){
                delLog = true;
                if(redirectErrorStream){
                    logfile = new File[1];
                    logfile[0] = new File("tmpExec.log");
                }else {
                    logfile = new File[2];
                    logfile[0] = new File("tmpExecStdout.log");
                    logfile[1] = new File("tmpExecError.log");
                }
            }
            //新建一个用来存储结果的缓存文件
            for (File f: logfile) {
                if(!f.exists()) {
                    if(! f.createNewFile()){
                        throw new FileNotFoundException("临时日志文件创建失败");
                    }
                }
            }
            ProcessBuilder pb = new ProcessBuilder().command(cmd);
            if (redirectErrorStream){
                //这里是把控制台中的红字变成了黑字，用通常的方法其实获取不到，控制台的结果是pb.start()方法内部输出的。
                pb.redirectErrorStream(true);
                pb.redirectOutput(logfile[0]);//把执行结果输出。
            }else{
                pb.redirectOutput(logfile[0]);
                pb.redirectError(logfile[1]);
            }
            Process process = pb.start();
            //等待语句执行完成，否则可能会读不到结果。
            if (timeout <= 0){
                process.waitFor();
            }else {
                process.waitFor(timeout, timeUnit);
            }
            InputStream in = new FileInputStream(logfile[0]);
            br= new BufferedReader(new InputStreamReader(in));
            String line = null;
            while((line = br.readLine()) != null) {
                stdo.add(line);
                System.out.println("stdout:" + line);
            }
            if(!redirectErrorStream){
                in = new FileInputStream(logfile[1]);
                br= new BufferedReader(new InputStreamReader(in));
                while((line = br.readLine()) != null) {
                    stde.add(line);
                    System.out.println("stderr:" + line);
                }
            }
            br.close();
            //卸磨杀驴。
            if (delLog){
                for (File f: logfile) {
                    if (! f.delete()){
                        throw new Exception("临时日志文件删除失败");
                    };
                }
            };
            exitcode.add(String.valueOf(process.exitValue()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        r.add(stdo);
        r.add(stde);
        r.add(exitcode);
        return r;
    }

    /**
     * 使用ProcessBuilder运行命令
     * @param cmd 命令（允许空格）
     * @param logfile null为无日志文件。每次运行日志都会被覆盖！如果重定向错误日志，请提供一个日志文件。如果没有重定向，请提供两个
     * @param redirectErrorStream 是否重定向错误（一般为是）
     * @param timeout 超时 小于等于0时无效
     * @param timeUnit 单位
     * @return 标准输出 标准错误 退出码
     */
    public static LinkedList<LinkedList<String>> execCommandByProcessBuilder(String[] cmd,
                                                                             File[] logfile,
                                                                             boolean redirectErrorStream,
                                                                             long timeout,
                                                                             TimeUnit timeUnit){
        boolean delLog = false;
        LinkedList<LinkedList<String>> r = new LinkedList<>();
        LinkedList<String> stdo = new LinkedList<>();
        LinkedList<String> stde = new LinkedList<>();
        LinkedList<String> exitcode = new LinkedList<>();
        try {
            BufferedReader br = null;
            if (logfile == null){
                delLog = true;
                if(redirectErrorStream){
                    logfile = new File[1];
                    logfile[0] = new File("tmpExec.log");
                }else {
                    logfile = new File[2];
                    logfile[0] = new File("tmpExecStdout.log");
                    logfile[1] = new File("tmpExecError.log");
                }
            }
            //新建一个用来存储结果的缓存文件
            for (File f: logfile) {
                if(!f.exists()) {
                    if(! f.createNewFile()){
                        throw new FileNotFoundException("临时日志文件创建失败");
                    }
                }
            }
            ProcessBuilder pb = new ProcessBuilder().command(cmd);
            if (redirectErrorStream){
                //这里是把控制台中的红字变成了黑字，用通常的方法其实获取不到，控制台的结果是pb.start()方法内部输出的。
                pb.redirectErrorStream(true);
                pb.redirectOutput(logfile[0]);//把执行结果输出。
            }else{
                pb.redirectOutput(logfile[0]);
                pb.redirectError(logfile[1]);
            }
            Process process = pb.start();
            //等待语句执行完成，否则可能会读不到结果。
            if (timeout <= 0){
                process.waitFor();
            }else {
                process.waitFor(timeout, timeUnit);
            }
            InputStream in = new FileInputStream(logfile[0]);
            br= new BufferedReader(new InputStreamReader(in));
            String line = null;
            while((line = br.readLine()) != null) {
                stdo.add(line);
                System.out.println("stdout:" + line);
            }
            if(!redirectErrorStream){
                in = new FileInputStream(logfile[1]);
                br= new BufferedReader(new InputStreamReader(in));
                while((line = br.readLine()) != null) {
                    stde.add(line);
                    System.out.println("stderr:" + line);
                }
            }
            br.close();
            //卸磨杀驴。
            if (delLog){
                for (File f: logfile) {
                    if (! f.delete()){
                        throw new Exception("临时日志文件删除失败");
                    };
                }
            };
            exitcode.add(String.valueOf(process.exitValue()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        r.add(stdo);
        r.add(stde);
        r.add(exitcode);
        return r;
    }

    // 文件、文件夹操作
    /**
     *  创建文件夹
     * @param path
     * @param isMkdirs 是否连续创建（mkdir -p） false时，如果文件夹存在，会报错
     */
    public static Path mkdir(Path path, boolean isMkdirs){
        try {
            if (isMkdirs){
                return Files.createDirectories(path);
            }else {
                return Files.createDirectory(path);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * mkdir -p
     * @param path
     * @return
     */
    public static Path mkdirs(Path path){
        try {
            return Files.createDirectories(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * mkdir
     * @param path
     * @return
     */
    public static Path mkdir(Path path){
        try {
            return Files.createDirectory(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 创建文件
     * @param path
     * @param overwrite 是否覆盖 不覆盖时，如果文件存在，会报错
     */
    public static Path touch(Path path, boolean overwrite){
        try {
            if (overwrite){
                Files.deleteIfExists(path);
                return Files.createFile(path);
            }else {
                return Files.createFile(path);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * touch 直接覆盖
     * @param path
     * @return
     */
    public static Path touch(Path path){
        try {
            Files.deleteIfExists(path);
            return Files.createFile(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param src
     * @param dst
     * @return
     */
    public static Path copy(Path src, Path dst){
        try {
            if (Files.isDirectory(src)){
                // 如果是目录
                Stream<Path> stream = Files.walk(src);
                stream.forEach(source -> copy(source, dst.resolve(source.relativize(source))));
                stream.close();
            }else if(Files.isRegularFile(src)){
                // 如果是文件
//                Files.deleteIfExists(dst);
                Files.copy(src, dst, StandardCopyOption.REPLACE_EXISTING);
            }else{
                throw new Exception("请检查文件是否存在（非目录亦非常规文件）");
            }
            return dst;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void la(Path path){
        try {
            Stream<Path> stream = Files.list(path);
            stream.forEach(p -> System.out.println(p));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void ls(Path path){
        try {
            Stream<Path> stream = Files.list(path);
            stream.forEach(p -> System.out.println(p));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    // 其他
    /**
     * 合并两个数组
     * @param a 数组1
     * @param b 数组2
     * @return 合并的数组
     */
    public static Object[] mergeArrays(Object[] a, Object[] b){
        Object[] c;
        int al = a.length;
        int bl = b.length;
        // 首先把a数组复制给c，且增加长度
        c = Arrays.copyOf(a,al+bl);
        // 将b数组从0开始复制给c数组，c数组从索引sl开始，复制b到bl
        // @1:要被复制的数组b，内容来源  src
        // @2:从b的0索引开始复制  src
        // @3:复制到c数组  dst
        // @4:从c数组的al索引开始添加  dst
        // @5:添加bl位（不得超过来源数组长度） src
        System.arraycopy(b, 0, c, al, bl);
        return c;
    }

}
