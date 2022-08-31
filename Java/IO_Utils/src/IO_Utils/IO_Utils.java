package IO_Utils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * IO 工具类, 读写文件
 *
 * @author xietansheng
 */
public class IO_Utils {
    public static final String CHARSET = "UTF-8";

    /**
     * 读取文件，以行位单位返回列表
     * 仅适用于单行少的
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


    /**
     * 使用Scanner读取较大的文件，但是花时也长
     * 更多请阅读：https://www.yiibai.com/java/java-read-text-file.html
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

    /**
     * 读取大文本文件
     * https://zhuanlan.zhihu.com/p/142029812
     * @param f
     */
    public static LinkedList<String> readUsingFileFileChannel(File f) {
        LinkedList<String> lls = new LinkedList<>();
        try (FileInputStream fileInputStream = new FileInputStream(f);) {
            FileChannel fileChannel = fileInputStream.getChannel();
            int capacity = 1024 * 1024;//1M (1 * 1024 * 1024)
            ByteBuffer byteBuffer = ByteBuffer.allocate(capacity);
            StringBuilder buffer = new StringBuilder();
            while(fileChannel.read(byteBuffer) != -1) {
                //读取后，将位置置为0，将limit置为容量, 以备下次读入到字节缓冲中，从0开始存储
                byteBuffer.clear();
                byte[] bytes = byteBuffer.array();
                String str = new String(bytes, CHARSET);
                str = str.replace(new String(new byte[1]), "");
                lls.add(str);
                //System.out.println(str);
                // 处理字符串,并不会将字符串保存真正保存到内存中
                // 这里简单模拟下处理操作.
//                buffer.append(str.substring(0,1));
            }
            return lls;
//            System.out.println("buffer.length:"+buffer.length());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 读取小的文本文件
     * 本质上使用BufferedReader
     * @param f
     * @return
     */
    public static LinkedList<String> readUsingFiles(File f){
        try{
            return (LinkedList<String>) Files.readAllLines(Paths.get(f.getAbsolutePath()));
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 读取二进制文件
     * @param f
     * @return
     */
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


    /**
     * 写入二进制文件
     * @param f
     * @param data
     */
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


    /**
     * 读取小的二进制文件
     * @param f
     * @return
     */
    public static byte[] readBytesUsingFileInputStream(File f){
        try{
            return new FileInputStream(f).readAllBytes();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}