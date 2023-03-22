import Datetime_Utils.Datetime_Utils;
import IO_Utils.IO_Utils;
import System_Utils.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
          // 使用Runtime运行命令
//        testRuntime();
        // 使用ProcessBuilder
//        testProcessBuilder();
        // 测试文件复制粘贴等
        testFilesystem();

    }

    public static void testFilesystem() throws IOException, InterruptedException {
//        System_Utils.mkdir();
        Path path = Paths.get("1.txt");
//        Files.createFile(path);
        Path path1 = Files.createTempFile(Paths.get("."),"1","1");
        System.out.println(path1.toAbsolutePath());
    }

    public static void testProcessBuilder()  {
//        String string = System_Utils.execCommandByProcessBuilder(new String[]{"ls", "1 1.txt"});
        System_Utils.execCommandByProcessBuilder(new String[]{"ls", "-a"}, null, false, 0, null);
    }
    public static void testRuntime(){
        LinkedList<File> linkedList = new LinkedList<>();
        linkedList.add(new File("1.txt"));
        linkedList.add(new File("2.txt"));
        System_Utils.execCommandByRuntime("ls");
        System_Utils.execCommandByRuntime("cat",linkedList, false, false, 1, true, 0, null);
    }
}