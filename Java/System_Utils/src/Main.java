import System_Utils.*;

import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException{
          // 使用Runtime运行命令
//        testRuntime();
        // 使用ProcessBuilder
//        testProcessBuilder();
        System.out.println("System_Utils.isRunAsAdministrator() = " + System_Utils.isRunAsAdministrator());
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