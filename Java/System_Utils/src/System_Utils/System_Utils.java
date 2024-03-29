package System_Utils;

import Datetime_Utils.Datetime_Utils;
import IO_Utils.IO_Utils;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
     * <a href="http://lopica.sourceforge.net/os.html">...</a>
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

    /**
     * 是否以管理员运行
     * @return
     */
    public static boolean isRunAsAdministrator(){
        return AdministratorChecker.IS_RUNNING_AS_ADMINISTRATOR;
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
     * public static LinkedList<LinkedList<String>> execCommandByRuntime(String cmd,LinkedList<File> logfile,boolean optionIfWindowsSetCharsetToGBK,boolean optionIfLinuxSetGnomeTerminalVisible,int optionTimestampMode,boolean verbose,long timeout,TimeUnit timeUnit)
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
     * public static LinkedList<LinkedList<String>> execCommandByProcessBuilder(String cmd,File[] logfile,boolean redirectErrorStream,long timeout,TimeUnit timeUnit)
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
     * 使用ProcessBuilder运行命令 命令（允许空格）
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
     * mkdir 存在会报错
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
     * 复制文件或文件夹（覆盖）
     * @param src
     * @param dst
     * @return 返回tree dst文件夹内容
     */
    public static LinkedList<Path> copy(Path src, Path dst){
        try {
            // 符号链接也会被识别为文件夹
            if (Files.isDirectory(src)){
                if(Files.isSymbolicLink(src)){
                    // 如果是链接，直接复制
                    // 如果不是符号链接，删除
                    if(!Files.isSymbolicLink(dst)){rmAll(dst);}
                    Files.copy(src, dst, LinkOption.NOFOLLOW_LINKS, StandardCopyOption.REPLACE_EXISTING);
                }else {
                    if(!Files.exists(dst)){mkdirs(dst);}
                    la(src).stream().forEach(x -> copy(x, dst.resolve(x.getFileName())));
                }
            }else if(Files.isRegularFile(src)){
                // 如果是文件
//                Files.deleteIfExists(dst);// 可以忽略，因为下面StandardCopyOption.REPLACE_EXISTING
//                Files.copy(src, dst, StandardCopyOption.REPLACE_EXISTING);
                Files.copy(src, dst, LinkOption.NOFOLLOW_LINKS, StandardCopyOption.REPLACE_EXISTING);
//                System.out.printf("%s -> %s \n", src.toAbsolutePath(), dst.toAbsolutePath());
            }else{
                throw new Exception("请检查文件是否存在（非目录亦非常规文件）");
            }
            return tree(dst);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * 复制文件或文件夹（覆盖）
     * @param src
     * @param dst
     * @return 返回tree dst文件夹内容
     */
    public static LinkedList<Path> copyFollowLinks(Path src, Path dst){
        try {
            if (Files.isDirectory(src)){
                // 新建目标文件夹后遍历
                // 第一句用于覆盖符号链接
                if(Files.isSymbolicLink(dst)){Files.deleteIfExists(dst);}
                if(!Files.exists(dst)){mkdirs(dst);}
                la(src).stream().forEach(x -> copyFollowLinks(x, dst.resolve(x.getFileName())));
            }else if(Files.isRegularFile(src)){
                // 如果是文件
                Files.copy(src, dst, StandardCopyOption.REPLACE_EXISTING);
//                System.out.printf("%s -> %s \n", src.toAbsolutePath(), dst.toAbsolutePath());
            }else{
                throw new Exception("请检查文件是否存在（非目录亦非常规文件）");
            }
            return tree(dst);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 复制当前目录中除了点文件以外的文件(仅允许目录)，且不跟随符号链接
     * @param src
     * @param dst
     * @return
     */
    public static LinkedList<Path> copyExcludeDotfiles(Path src, Path dst){
        LinkedList<Path> paths = new LinkedList<>();
        try {
            // 不是目录或者是符号链接的无效
            if(! Files.isDirectory(src) || Files.isSymbolicLink(src)){
                throw new Exception("文件类型错误，仅允许文件夹");
            }
            // 列出当前目录
            ls(src).stream().forEach(x->paths.addAll(copy(x, dst.resolve(x.getFileName()))));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return paths;
    }

    /**
     *  mv 移动文件或文件夹（覆盖）
     * @param src
     * @param dst
     * @return 返回tree dst文件夹内容
     */
    public static LinkedList<Path> move(Path src, Path dst){
        try {
            if (Files.isDirectory(src)){
                if(Files.isSymbolicLink(src)){
                    Files.move(src, dst, LinkOption.NOFOLLOW_LINKS, StandardCopyOption.REPLACE_EXISTING);
                }else{
                    // 如果是目录，新建目标文件夹后遍历
                    if(!Files.exists(dst)){mkdirs(dst);}
                    la(src).stream().forEach(x -> move(x, dst.resolve(x.getFileName())));
                }
                Files.deleteIfExists(src);
            }else if(Files.isRegularFile(src)){
                // 如果是文件
                Files.move(src, dst, StandardCopyOption.REPLACE_EXISTING, LinkOption.NOFOLLOW_LINKS);
//                System.out.printf("%s -> %s \n", src.toAbsolutePath(), dst.toAbsolutePath());
            }else{
                throw new Exception("请检查文件是否存在（非目录亦非常规文件）");
            }
            return tree(dst);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 移动文件或文件夹（覆盖）
     * 注意：符号链接仅复制下属内容，不会删除原来的文件夹（所以有些报错无法避免）
     * @param src
     * @param dst
     * @return 返回tree dst文件夹内容
     */
    public static LinkedList<Path> moveFollowLinks(Path src, Path dst){
        try {
            if (Files.isDirectory(src)){
                if(Files.isSymbolicLink(dst)){Files.deleteIfExists(dst);}
                // 如果是目录，新建目标文件夹后遍历
                if(!Files.exists(dst)){mkdirs(dst);}
                la(src).stream().forEach(x -> {
                    if(Files.isSymbolicLink(x)){
                        // 符号链接采用复制
                        copyFollowLinks(x, dst.resolve(x.getFileName()));
                    }else {
                        moveFollowLinks(x, dst.resolve(x.getFileName()));
                    }
                });
                if(! Files.isSymbolicLink(src)){
                    // 如果文件夹中含有符号链接，报错属于正常
                    Files.deleteIfExists(src);
                }
            }else if(Files.isRegularFile(src)){
                // 如果是文件
                Files.move(src, dst, StandardCopyOption.REPLACE_EXISTING);
//                System.out.printf("%s -> %s \n", src.toAbsolutePath(), dst.toAbsolutePath());
            }else{
                throw new Exception("请检查文件是否存在（非目录亦非常规文件）");
            }
        } catch (Exception e) {
            System.out.println("moveFollowLinks e = " + e);
        }
        return tree(dst);
    }

    /**
     * 移动当前目录中除了点文件以外的文件(仅允许目录)，且不跟随符号链接
     * @param src
     * @param dst
     * @return
     */
    public static LinkedList<Path> moveExcludeDotfiles(Path src, Path dst){
        LinkedList<Path> paths = new LinkedList<>();
        try {
            // 不是目录或者是符号链接的无效
            if(! Files.isDirectory(src) || Files.isSymbolicLink(src)){
                throw new Exception("文件类型错误，仅允许文件夹");
            }
            // 列出当前目录
            ls(src).stream().forEach(x->paths.addAll(move(x, dst.resolve(x.getFileName()))));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return paths;
    }


    /**
     * rm 删除文件（不能删除文件夹和隐藏文件），如果是目录，仅会删除目录中的文件（没有递归）
     * @param path
     * @return
     */
    public static LinkedList<Path> rm(Path path){
        LinkedList<Path> paths = new LinkedList<>();
        try {
            // 是目录，且不是符号链接
            if(Files.isDirectory(path) && ! Files.isSymbolicLink(path)){
                la(path).stream().forEach(x -> {
                    try {
                        Files.deleteIfExists(x);
                        paths.add(x);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }else {
                Files.deleteIfExists(path);
                paths.add(path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return paths;
    }

    /**
     * 删除文件、文件夹
     * @param path
     * @param action 是否执行
     * @return
     */
    public static LinkedList<Path> rmAll(Path path, boolean action){
        LinkedList<Path> paths = new LinkedList<>();
        try {
            // 是目录，且不是符号链接
            if(Files.isDirectory(path) && ! Files.isSymbolicLink(path)){
                // 下面三句一致
                la(path).stream().forEach(x-> paths.addAll(rmAll(x, action)));
                if(action){
                    Files.deleteIfExists(path);
                }else {
                    System.out.println("拟删除: "+path);
                }
            }else {
                if(action){
                    Files.deleteIfExists(path);
                }else {
                    System.out.println("拟删除: "+path);
                }
            }
            paths.add(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return paths;
    }

    /**
     * rm -r递归删除删除文件(包括隐藏文件)、文件夹
     * @param path
     * @return 成功删除的
     */
    public static LinkedList<Path> rmAll(Path path){
        LinkedList<Path> paths = new LinkedList<>();
        try {
            // 是目录，且不是符号链接
            if(Files.isDirectory(path) && ! Files.isSymbolicLink(path)){
                // 下面三句一致
//                la(path).stream().forEach(x->rmAll(x).stream().forEach(y->paths.add(y)));
//                la(path).stream().forEach(x->rmAll(x).stream().forEach(paths::add));
                la(path).stream().forEach(x-> paths.addAll(rmAll(x)));
                Files.deleteIfExists(path);
            }else {
                Files.deleteIfExists(path);
            }
            paths.add(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return paths;
    }

    /**
     * 删除当前目录中除了点文件以外的文件(仅允许目录)
     * @param path
     * @return
     */
    public static LinkedList<Path> rmExcludeDotfiles(Path path){
        LinkedList<Path> paths = new LinkedList<>();
        try {
            // 不是目录或者是符号链接的无效
            if(! Files.isDirectory(path) || Files.isSymbolicLink(path)){
                throw new Exception("文件类型错误，仅允许文件夹");
            }
            // 列出当前目录
            ls(path).stream().forEach(x->paths.addAll(rmAll(x)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return paths;
    }

    /**
     * tree 列出所有文件
     * @param path 路径
     * @param level 遍历深度 （小于等于0无效）
     * @param includeHidden 是否包含隐藏文件
     * @param followLinks 是否跟随链接
     * @return 文件Path
     */
    public static LinkedList<Path> tree(Path path, int level, boolean includeHidden, boolean followLinks){
        LinkedList<Path> paths = new LinkedList<>();
        try {
            if(followLinks){
                if(level > 0){
                    paths = arrayList2LinkedList(Files.walk(path, level, FileVisitOption.FOLLOW_LINKS).collect(Collectors.toList()));
                }else {
                    paths = arrayList2LinkedList(Files.walk(path, FileVisitOption.FOLLOW_LINKS).collect(Collectors.toList()));
                }
            }else {
                if(level > 0){
                    paths = arrayList2LinkedList(Files.walk(path, level).collect(Collectors.toList()));
                }else {
                    paths = arrayList2LinkedList(Files.walk(path).collect(Collectors.toList()));
                }
            }
            if(! includeHidden){
//                paths.stream().filter(x->{if (! isDotfiles(x)) {return true;}return false;});
                paths = arrayList2LinkedList(paths.stream().filter(x->{
                    // 点文件和在点目录的会被剔除
                    if (isDotfiles(x) || isInDotDirectory(x)) {return false;}
                    return true;}).collect(Collectors.toList()));
            }
            paths.stream().forEach(System.out::println);
            return paths;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * tree 列出所有（不跟随Link）
     * @param path
     * @return
     */
    public static LinkedList<Path> tree(Path path){
        try {
            return arrayList2LinkedList(Files.walk(path).collect(Collectors.toList()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * ls -a 列出包括隐藏文件在内的文件
     * @param path
     * @return
     */
    public static LinkedList<Path> la(Path path){
        try {
            LinkedList<Path> paths = arrayList2LinkedList(Files.list(path).collect(Collectors.toList()));
//            paths.stream().forEach(System.out::println);
            return paths;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * ls列出文件（隐藏文件除外）
     * @param path
     * @return
     */
    public static LinkedList<Path> ls(Path path){
        try {
            // ./System_Utils.iml
            //./.idea
            //./out
            //./src
            LinkedList<Path> paths = arrayList2LinkedList(Files.list(path).filter(p->{
                // 去除.开头的隐藏文件
                if (isDotfiles(p)){return false;}
                return true;
            }).collect(Collectors.toList()));
//            paths.stream().forEach(System.out::println);
            return paths;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 是否是点开头文件（隐藏文件）
     * @param path
     * @return
     */
    public static boolean isDotfiles(Path path){
        if(path.getFileName().toString().substring(0, 1).equals(".")){
            return true;
        }
        return false;
    }

    /**
     * 是否在点目录中（隐藏目录），是的话返回true，注意：并不会因为是隐藏文件而true，只专注目录
     * @param path
     * @return
     */
    public static boolean isInDotDirectory(Path path){
        if(! Files.isDirectory(path)){
            // 如果不是目录 排除
            path = path.getParent();
        }
        List<Boolean> f = new ArrayList<Boolean>();
        f.add(false);
        path.spliterator().forEachRemaining(x->{
            // 要排除掉 "."
            if(isDotfiles(x) && ! x.toString().equals(".")){
                f.set(0, true);
            }
        });
        if(f.get(0)){
            return true;
        }
        return false;
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

    /**
     * List转LinkedList
     * @param e
     * @return
     */
    public static LinkedList arrayList2LinkedList(List e){
        LinkedList linkedList = new LinkedList(){};
        e.stream().forEach(x->linkedList.add(x));
        return linkedList;
    }
    
    
    /**
     * 私有方法，返回Python Bytes对应的JavaBytes 字典
     * @param pythonBytesKey key是Python的字节数组，否则是Java的字节数组
     * @return
     */
    private static HashMap<Integer, Integer> getPythonJavaByteMap(boolean pythonBytesKey){
        HashMap<Integer, Integer> hashMap = new HashMap<>();
        int b = -128;
        // 0~127相同
        for (int i = 0; i <= 127; i++) {
            hashMap.put(i, i);
        }
        if(pythonBytesKey){
            // Python 0~255 Java -128~127
            for (int i = 128; i < 256; i++) {
                hashMap.put(i, b);
                b++;
            }
        }else {
            for (int i = 128; i < 256; i++) {
                hashMap.put(b, i);
                b++;
            }
        }
        return hashMap;
    }


    /**
     * Python byte转 Java byte
     * @param pybyte
     * @return
     */
    public static byte pythonbyte2javabyte(int pybyte){
        return getPythonJavaByteMap(true).get(pybyte).byteValue();
    }

    /**
     * Java byte转 Python byte
     * @param jbyte
     * @return
     */
    public static int javabyte2pythonbyte(int jbyte){
        return getPythonJavaByteMap(false).get(jbyte).intValue();
    }

    /**
     * Pythonbyte数组转Javabyte数组
     * @param pybytes
     * @return
     */
    public static byte[] pythonbytes2javabytes(byte[] pybytes){
        byte[] r = new byte[pybytes.length];
        for (int i = 0; i < pybytes.length; i++) {
            r[i] = pythonbyte2javabyte(pybytes[i]);
        }
        return r;
    }

    /**
     * Javabyte数组转Pythonbyte数组
     * @param jbytes
     * @return
     */
    public static int[] javabytes2pythonbytes(byte[] jbytes){
        int[] r = new int[jbytes.length];
        for (int i = 0; i < jbytes.length; i++) {
            r[i] = javabyte2pythonbyte(jbytes[i]);
        }
        return r;
    }

}
