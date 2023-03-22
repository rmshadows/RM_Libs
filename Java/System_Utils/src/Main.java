import Datetime_Utils.Datetime_Utils;
import IO_Utils.IO_Utils;
import System_Utils.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Begin");
          // 使用Runtime运行命令
//        testRuntime();
        // 使用ProcessBuilder
        testProcessBuilder();

    }

    public static void testProcessBuilder() throws IOException {
        Process p = new ProcessBuilder("ls", "-a", "&&", "ls").start();
        byte[] b = p.getErrorStream().readAllBytes();
        String s = new String(b);
        System.out.println("s = " + s);
    }
    public static void testRuntime(){
        LinkedList<File> linkedList = new LinkedList<>();
        linkedList.add(new File("1.txt"));
        linkedList.add(new File("2.txt"));
        System_Utils.execCommandByRuntimeEz("ls");
        System_Utils.execCommandByRuntime("cat",linkedList, false, false, 1, true, 0, null);
    }
}