import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException {
        File f = new File("1.txt");
        // 测试Scanner
        // testScanner();
        testBufferedReader();







    }

    private static void testBufferedReader() {
        for (String s: IO_Utils.readUsingBufferedReader("1.txt")) {
            System.out.println("s = " + s);
        }
    }

    private static void testScanner(){
        for (String s: IO_Utils.readUsingScanner("1.txt")) {
            System.out.println("s = " + s);
        }
    }
}