import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException {
        File f = new File("1.txt");
        // 测试Scanner
        // testScanner();
        // 测试Reader
        // testBufferedReader();
        // 测试Buffered input Stream
        testBufferedInputStream();






    }

    private static void testBufferedInputStream() throws UnsupportedEncodingException {
//        IO_Utils.readUsingBufferedStream("1.txt");
        byte[] bytes = IO_Utils.readBytesUsingBufferedInputStream(new File("1.txt"));
        System.out.println(bytes.length);
    }

    private static void testBufferedReader() {
        for (String s: IO_Utils.readUsingBufferedReader(new File("1.txt"))) {
            System.out.println("s = " + s);
        }
    }

    private static void testScanner(){
        for (String s: IO_Utils.readUsingScanner(new File("1.txt"))) {
            System.out.println("s = " + s);
        }
    }
}