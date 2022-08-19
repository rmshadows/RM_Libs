import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.stream.Stream;

/**
 * IO 工具类, 读写文件
 *
 * @author xietansheng
 */
public class IO_Utils {
    public static final String CHARSET = "UTF-8";

    public static LinkedList<String> readUsingBufferedReader(String filepath){
        Path path = Paths.get(filepath);
        LinkedList<String> lines = new LinkedList<>();
        try (// https://www.cnblogs.com/jpfss/p/9789390.html
             InputStreamReader isr = new InputStreamReader(new FileInputStream(path.toFile()), CHARSET);
                BufferedReader br = new BufferedReader(isr);) {
            String line = null;
            while((line = br.readLine()) != null){
                lines.add(line);
            }
            return lines;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void writeFile(String data, File file) throws IOException {
        OutputStream out = null;
        try {
            out = new FileOutputStream(file);
            out.write(data.getBytes());
            out.flush();
        } finally {
            close(out);
        }
    }

    public static String readFile(File file){
        try (InputStream in = new FileInputStream(file);
             ByteArrayOutputStream out = new ByteArrayOutputStream();){
            byte[] buf = new byte[1024];
            int len = -1;
            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            out.flush();
            byte[] data = out.toByteArray();
            return new String(data);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void close(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
                // nothing
            }
        }
    }

    /**
     * 使用Scanner读取较大的文件
     * //更多请阅读：https://www.yiibai.com/java/java-read-text-file.html
     * @param fileName
     * @return LinkedList<String>
     */
    public static LinkedList<String> readUsingScanner(String fileName) {
        Path path = Paths.get(fileName);
        LinkedList<String> lines = new LinkedList<>();
        try (Scanner scanner = new Scanner(path, CHARSET);){
            //逐行读取
            while(scanner.hasNextLine()){
                //逐行处理
                String line = scanner.nextLine();
//                System.out.println(line);
                lines.add(line);
            }
            return lines;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}