import Datetime_Utils.Datetime_Utils;
import IO_Utils.IO_Utils;
import System_Utils.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

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
        Path path0 = Paths.get(".");
        Path path1 = Paths.get("1.txt");
        Path path3 = Paths.get("2.txt");
        Path path2 = Paths.get("dir0/dir1/dir2");
        Path path4 = Paths.get("666");
        Path path5 = Paths.get("777");
//        System_Utils.touch(path1, true);
//        System_Utils.mkdir(path2, true);
//        System_Utils.copy(path1, path3);
        System_Utils.copy(path4, path5);

//        System_Utils.ls(path0);
//        Stream<Path> stream = Files.walk(path0);
//        Stream<Path> stream = Files.list(path0);
//        stream.forEach(path -> System.out.println(path));

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