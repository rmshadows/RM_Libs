import javax.imageio.metadata.IIOMetadata;
import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
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

    /**
     * 读取文件，以行位单位返回列表
     * @param f 文件类
     * @return 每一行的列表
     */
    public static LinkedList<String> readUsingBufferedReader(File f){
        Path path = Paths.get(f.getAbsolutePath());
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

    /**
     * 给定行的列表写入文件
     * @param f 文件类
     * @param lines 行内容列表
     * @return 返回输出文件
     */
    public static File writeUsingBufferedWriter(File f, LinkedList<String> lines){
        Path path = Paths.get(f.getAbsolutePath());
        try (// https://www.cnblogs.com/jpfss/p/9789390.html
             OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(path.toFile()), CHARSET);
                BufferedWriter bw = new BufferedWriter(osw);) {
            for (String line: lines) {
                bw.write(String.format("%s\n", line));
            }
            bw.flush();
            return path.toFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static byte[] readBytesUsingBufferedInputStream(File f){
        Path path = Paths.get(f.getAbsolutePath());
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(path.toFile()));
             ByteArrayOutputStream baos = new ByteArrayOutputStream();){
            byte[] read = new byte[4];
            int r_len = -1;
            while ((r_len = bis.read(read)) != -1){
                baos.write(read, 0, r_len);
            }
            baos.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void writeBytesUsingBufferedOutputStream(File f, byte[] data){
        Path path = Paths.get(f.getAbsolutePath());
        try (BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(data));
             BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path.toFile()));){
            int read = -1;
            byte[] buf = new byte[1024];
            while ((read = bis.read(buf)) != -1){
                bos.write(buf, 0, read);
            }
            bos.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static byte[] readBytesUsingFileInputStream(File f){
        try{
            return new FileInputStream(f).readAllBytes();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 使用Scanner读取较大的文件
     * 更多请阅读：https://www.yiibai.com/java/java-read-text-file.html
     * 超大文件TODO https://blog.51cto.com/u_9597987/3485702
     * @param f 文件类
     * @return LinkedList<String>
     */
    public static LinkedList<String> readUsingScanner(File f) {
        Path path = Paths.get(f.getAbsolutePath());
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