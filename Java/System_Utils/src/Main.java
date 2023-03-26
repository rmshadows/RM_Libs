import Datetime_Utils.Datetime_Utils;
import IO_Utils.IO_Utils;
import System_Utils.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.LinkOption;
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

        Path p1 = Paths.get(".");
        Path p2 = Paths.get("111");
        Path p3 = Paths.get("222");
        Path p4 = Paths.get("111/到 bak 的链接");
        Path p5 = Paths.get("222/到 bak 的链接");
        Path p6 = Paths.get("222（复件）");

//        System_Utils.rm(p6);
//        System.out.println("p6 = " + p6);

//        System_Utils.copy(p2, p3).stream().forEach(System.out::println);
//        System_Utils.copyFollowLinks(p2, p3).stream().forEach(System.out::println);

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