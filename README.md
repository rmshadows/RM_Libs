# RM_Libs

## 简介

一些函数库罢了。

## Android

- RSA_Utils(停工)

### RSA_Utils

(TODO)(停工)

## Bash

引入：`source 【脚本路径】`

注意：返回值均使用变量：RETURN_VAR

- Profile.sh——Bash脚本头【所有脚本的基础依赖】
- System——系统组件相关【依赖Profile.sh】

### Profile.sh

- `prompt`——终端打印彩色文字
- `comfirmy`——默认选择Y的询问函数
- `comfirmn`——默认选择N的询问函数

使用：`prompt [参数] "文字"`

红色：警告、重点

黄色：警告、一般打印

绿色：执行日志

蓝色、白色：常规信息

例：

```bash
#!/bin/bash
source Profile.sh
testPrintln(){
    prompt -s "成功信息（绿色）"
    prompt -x "执行日志（绿色）"
    prompt -e "错误信息（红色）"
    prompt -w "警告信息（黄色）"
    prompt -i "普通信息（蓝色）"
    prompt -m "普通消息（蓝色）"
    prompt -k "一个参数（蓝色）" "两个参数（黄色）"
}
testPrintln
```

### System

- `checkRootPasswd`——检查root密码是否正确 `checkRootPasswd $1`
- `doAsRoot `——以root用户身份运行，需要提供root密码 `doAsRoot -p root密码 -c 命令`
- `checkForRoot`——检查是否有root权限
- `backupFile`——备份文件的方法，要求`sudo`没有密码才能备份系统文件
- `addFolder`——新建文件夹 $1 注意权限！系统文件需要sudo无密码
- `debianBullseyeDetect`——显示是否是Debian11，无返回值！
- `isMacOS`——检测是否为苹果系统
- `isGNOME`——是否是GNOME桌面

系统组件相关操作

## Java

引入：`import 【包名】`

- AES_Utils——AES加密
  - AES_CBC——CBC模式
  - AES_CFB——CFB模式
  - AES_Tools——AES组件共用的函数（十六进制字符串转Byte数组、Byte数组转十六进制字符串）
- Code_Utils——编码
  - Base64Bytes——Base64和Bytes
  - BytesHexString——十六进制字符串和Bytes
- Datetime_Utils——时间组件
- IO_Utils——输入输出，文件读写
  - IO_Utils——文件读写
- RSA_Utils——RSA组件
  - com.github.xiangyuecn.rsajava——PKCS1的依赖
  - RSA_PKCS1_Utils——处理PKCS1的RSA组件
  - RSA_PKCS8_Utils——处理PKCS8的RSA组件
  - RSA_Tools——RSA公用函数
- System_Utils——系统组件
- Tools_Utils——工具模块里面的每个包都是独立的，不允许依赖其他包（相互独立）
  - IpAddress——IP
  - RandomNumber——随机数
  - ScheduleTask——定时任务（可以用于代替`while(xx==true){Thread.sleep(1000)}`检测变量）
  


### AES_Utils

>AES模块，CBC模式和CFB模式
>
>注意：byte[] 数据
>
>Python3： 0~256
>
>java:     -127~128

- AES CBC
- AES CFB
- AES Tool
  - `public static byte[] hex2bytes(String inputHexString)`——十六进制字符串转Byte数组
  - `public static String bytes2hex(byte[] b)`——Byte数组转十六进制字符串
  - `public static byte[] padding(String key, int length)`——把所给的String密钥转为一定长度 的 Byte数组并填充

例：

```java
package AES_Utils;

public class test {
    public static void main(String[] args) {
        String todo = "妳好Hello@";
        String hex;
        /**
         * CFB模式 密码123456 填充；符号
         */
        System.out.printf("CFB加密前：%s\n", todo);
        AES_Utils.AES_CFB cfb = new AES_Utils.AES_CFB("123456", ";");
        hex = cfb.encrypt(todo);
        System.out.printf("CFB加密：%s\n", hex);
        System.out.printf("CFB解密：%s\n", cfb.decrypt(hex));
        /**
         * 临时更换密码
         */
        hex = cfb.encrypt("12345", todo);
        System.out.printf("CFB临时加密(PWD: 12345)：%s\n", hex);
        System.out.printf("CFB临时解密(PWD: 12345)：%s\n", cfb.decrypt("12345", hex));
        System.out.println();
        /**
         * CBC模式 密码123456 偏移量：4321 位数 16:128/32:256
         */
        AES_Utils.AES_CBC cbc = new AES_Utils.AES_CBC("123456", "4321", 32);
        System.out.printf("CBC加密前：%s\n", todo);
        hex = cbc.encrypt(todo);
        System.out.printf("CBC加密：%s\n", hex);
        System.out.printf("CBC解密：%s\n", cbc.decrypt(hex));
        /**
         * 临时更换密码
         */
        hex = cbc.encrypt("12345", "54321", todo);
        System.out.printf("CBC临时加密(PWD: 12345, IV: 54321)：%s\n", hex);
        System.out.printf("CBC临时解密(PWD: 12345, IV: 54321)：%s\n", cbc.decrypt("12345", "54321", hex));
    }
}
```

### Code_Utils

>类型转换类

- Base64Bytes——Base64和Bytes
  - `public static String bytes2base64(byte[] data)`——传入byte数组执行base64加密
  - `public static byte[] base642bytes(String data)`——传入String执行base64解密
- BytesHexString——十六进制字符串和Bytes
  - `public static byte[] hex2bytes(String inputHexString)`——十六进制字符串转Byte数组
  - `public static String bytes2hex(byte[] b)`——Byte数组转十六进制字符串

```java
package Code_Utils;

import java.io.UnsupportedEncodingException;

public class test {
    public static void main(String[] args) throws UnsupportedEncodingException {
        String test = "字符串";
        String str = "";
        System.out.printf("原始的字符串： %s\n", test);
        str = Base64Bytes.bytes2base64(test.getBytes("UTF-8"));
        System.out.printf("Base64-E: %s\n", str);
        System.out.printf("Base64-D: %s\n", new String(Base64Bytes.base642bytes(str), "UTF-8"));

        str = BytesHexString.bytes2hex(test.getBytes("UTF-8"));
        System.out.printf("HexString-E: %s\n", str);
        System.out.printf("HexString-D: %s\n", new String(BytesHexString.hex2bytes(str), "UTF-8"));
    }
}
```

### Datetime_Utils

- Datetime_Utils
  - `public static long getTimeStampNow(boolean isMillis)`——获取当前时间毫秒时间戳 ，此方法不受时区影响，1675374966417
  - `public static LocalTime getTimeNow(Clock zoneClock)`——返回当前时间，仅时间，04:42:06.249760
  - `public static LocalDate getDateNow(Clock zoneClock)`——返回今天日期（无时间），2023-02-17
  - `public static LinkedList<Object> periodLocalDate(LocalDate d1, LocalDate d2)`——返回d1， d2时间差数据，列表：1.int数组年月日 2.字符串时间差
  - `public static LinkedList<LocalDate> getFirstAndLastDayofMonth(LocalDate localDate)`——获取某个月的第一天和最后一天
  - `public static LinkedList<LocalDate> getFirstInAndLastInMonth(LocalDate localDate, int dayofWeek)`——获取某个月的第一个周几和最后一个周几
  - `public static LocalDateTime getDateTimeNow(Clock zoneClock)`——返回当前日期时间，2023-02-17T05:36:53.899425
  - `public static LinkedList<LocalDateTime> getDayMaxAndMin(LocalDate localDate)`——获取某天的零点和最大时刻
  - `public static LinkedList<Object> durationLocalDateTime(LocalDateTime d1, LocalDateTime d2)`——返回d1， d2时间差数据，列表：1.long数组 天、时、分、秒、毫秒、微秒 2.字符串时间差
  - `public static ZonedDateTime getZoneDateTimeNow(String szoneId)`——获取时区时间 ZoneDateTime 2023-03-13T04:23:39.688183+08:00[Asia/Shanghai]
  - `public static ZonedDateTime zonedDateTimeConversion(ZonedDateTime zonedDateTime, String szoneId)`——不同时区的时间转换，WARN:注意这两次转换后的纽约时间有1小时的夏令时时差。涉及到时区时，千万不要自己计算时差，否则难以正确处理夏令时。
  - `public static DateTimeFormatter getFormatter(String format)`——获取格式
  - `public static LocalDateTime localDate2LocalDateTime(LocalDate localDate)`——localDate转localDateTime
  - `public static ZonedDateTime LocalDateTime2zonedDateTime(LocalDateTime localDateTime, String szoneId)`——localDateTime转为ZonedDateTime
  - `public static LocalDateTime instant2ShanghaiLocalDateTime(Instant instant)`——瞬时时间Instant转为上海LocalDateTime
  - `public static LocalDateTime timestamp2LocalDateTime(long timestamp, String szoneId, boolean isEpochMilli)`——时间戳转为LocalDateTime
  - `public static long localDateTime2Timestamp(LocalDateTime localDateTime, ZoneOffset zoneOffset, boolean isEpochMilli)`——localDateTime转时间戳
  - `public static Instant localDateTime2Instant(LocalDateTime localDateTime, ZoneOffset zoneOffset)`——LocalDateTime转Instant
  - `public static Clock getZoneClock(String timezone)`——返回某时区的时钟，如：巴黎时区："Europe/Paris"
  - `public static ZoneId getSystemTimezone()`——获取系统默认时区

### IO_Utils

>注意：byte[] 数据
>
>Python3： 0~256
>
>java:     -127~128

- IO_Utils
  - `public static LinkedList<String> readUsingBufferedReader(File f)`——读取文件，以行位单位返回列表，仅适用于单行少的，返回每一行的列表。
  - `public static File writeUsingBufferedWriter(File f, LinkedList<String> lines, boolean appendMode)`——给定行的列表写入文件
  - `public static LinkedList<String> readUsingScanner(File f) `——使用Scanner读取较大的文件，但是花时也长
  - `public static LinkedList<String> readUsingFileFileChannel(File f)`——读取大文本文件
  - `public static LinkedList<String> readUsingFiles(File f)`——读取小的文本文件，本质上使用BufferedReader
  - `public static byte[] readBytesUsingBufferedInputStream(File f)`——读取二进制文件
  - `public static void writeBytesUsingBufferedOutputStream(File f, byte[] data, boolean appendMode)`——写入二进制文件
  - `public static byte[] readBytesUsingFileInputStream(File f)`——读取小的二进制文件

### RSA_Utils

>依赖com.github.xiangyuecn.rsajava

- `com.github.xiangyuecn.rsajava`
  - 处理PKCS1的依赖，感谢[RSA-java](https://github.com/xiangyuecn/RSA-java)项目
- RSA_PKCS1_Utils
  - `public static PrivateKey loadPKCS1_PRK(File f)`——加载PKCS1私钥
  - `public static PublicKey loadPKCS1_PUK(File f)`——加载PKCS1公钥
  - `public static void savePKCS1_RSA_Key(RSA_PEM pem, File save_path, boolean isPRK)`——保存PKCS1 RSA密钥
  - `public static String pem2String(RSA_PEM pem, boolean isPRK, boolean pkcs1)`——返回密钥文本
  - `public static RSA_PEM generatePKCS1_RSAKey(int key_size)`——初始化PKCS1密钥
- RSA_PKCS8_Utils
  - `public static String sign(byte[] data, PrivateKey privateKey, boolean HexString_or_Base64)`——用私钥对信息生成数字签名
  - `public static boolean verify(byte[] data, PublicKey publicKey, String sign, boolean HexString_or_Base64) `——校验数字签名
  - `public static byte[] decryptByPrivateKey(byte[] data, PrivateKey privateKey)`——用私钥解密
  - `public static byte[] decryptByPublicKey(byte[] data, PublicKey publicKey)`——用公钥解密
  - `public static byte[] encryptByPublicKey(byte[] data, PublicKey publicKey)`——用公钥加密
  - `public static byte[] encryptByPrivateKey(byte[] data, PrivateKey privateKey)`——用私钥加密
  - `public static String getBase64PrivateKey(Map<String, Object> keyMap)`——取得Base64后的私钥
  - `public static String getBase64PublicKey(Map<String, Object> keyMap)`——取得Base64后的公钥
  - `public static PrivateKey getPrivateKey(Map<String, Object> keyMap)`——取得私钥
  - `public static PublicKey getPublicKey(Map<String, Object> keyMap)`——取得公钥
  - `public static PrivateKey loadPKCS8_PRK(File f)`——加载PKCS8私钥
  - `public static PublicKey loadPKCS8_PUK(File f)`——加载PKCS8公钥
  - `public static void savePKCS8_RSA_Key(String Base64_RSA_Key, File save_path, boolean isPRK)`——保存PKCS8 RSA密钥
  - `public static Map<String, Object> generatePKCS8_RSAKey(int key_size)`——初始化PKCS8密钥
  - `public static KeyPair generatePKCS8_RSAKeyPair(int key_size, String alg)`——生成PKCS8 RSA密钥对 (直接保存Encoder密钥Python不兼容)
  - `public static Map<String, Object> loadPKCS1_RSA_Key_as_PKCS8(File prk, File puk)`——直接读取PKCS1密钥并转为PKCS8密钥使用
- RSA_Tools
  - `public static byte[] readBytesUsingBufferedInputStream(File f)`——读取二进制文件
  - `public static void writeBytesUsingBufferedOutputStream(File f, byte[] data)`——写入二进制文件
  - `public static String bytes2base64(byte[] data)`——base64加密
  - `public static byte[] base642bytes(String data)`——base64解密
  - `public static byte[] hex2bytes(String inputHexString)`——十六进制字符串转Byte数组
  - `public static String bytes2hex(byte[] b)`——Byte数组转十六进制字符串
  - `public static Map<String, Object> PKCS1_2_PKCS8(RSA_PEM pem)`——PKCS1转PKCS8
  - `public static RSA_PEM PKCS8_2_PKCS1(PrivateKey prk, PublicKey puk)`——PKCS8 转 PKCS1

### System_Utils

>依赖：Datetime_Utils、IO_Utils、System_Utils

- System_Utils
  - `checkSystemType()`——判断系统是Windows(0)、Linux(1)或者MacOS(2)或者其他（-1）
  - `public static boolean isWindows()`——是否是Windows系统
  - `public static boolean isRunAsAdministrator()`——是否以管理员运行
  - `public static LinkedList<LinkedList<String>> execCommandByRuntime(String cmd,LinkedList<File> logfile,boolean optionIfWindowsSetCharsetToGBK,boolean optionIfLinuxSetGnomeTerminalVisible,int optionTimestampMode,boolean verbose,long timeout,TimeUnit timeUnit)`——使用Runtime运行命令。cmd 字符串命令、logfile 日志文件，如果不生成，请填写null。仅提供长度一位：标准、错误全部在一个文件。提供两位：标准和错误分开输出、optionIfWindowsSetCharsetToGBK 如果是Windows系统，启用GBK编码、optionIfLinuxSetGnomeTerminalVisible 如果是Linux系统，使用GNOME Terminal运行、optionTimestampMode 文件输出带时间戳的模式（其他: 不带时间，1：LocalDateTime 2023-03-20T21:57:15.676611，2:ZonedDateTime 2023-03-20T21:57:15.677782+08:00[Asia/Shanghai]，3:时间-中文-毫秒 2023年03月20日22时02分40.602148秒，4:毫秒时间戳) 1679320718584）、verbose 是否直接输出到命令行、timeout 超时 小于等于0忽略、timeUnit 时间单位，如果timeout小于等于0，这个参数无效、@return LinkedList<LinkedList<String>> 列表1(0):标准输出 列表2(1):标准错误 列表3(2):退出码
  - `public static LinkedList<LinkedList<String>> execCommandByRuntime(String cmd)`——仅运行命令
  - `public static String execCommandByProcessBuilder(String cmd)`——仅运行命令（不能带空格）
  - `public static String execCommandByProcessBuilder(String[] cmd)`——仅运行命令（允许空格）
  - `public static LinkedList<LinkedList<String>> execCommandByProcessBuilder(String cmd,File[] logfile,boolean redirectErrorStream,long timeout,TimeUnit timeUnit)`——使用ProcessBuilder运行命令。cmd 命令（不允许空格）、logfile null为无日志文件。每次运行日志都会被覆盖！如果重定向错误日志，请提供一个日志文件。如果没有重定向，请提供两个、redirectErrorStream 是否重定向错误（一般为是）、timeout 超时 小于等于0时无效、timeUnit 单位、 @return 标准输出 标准错误 退出码
  - `public static LinkedList<LinkedList<String>> execCommandByProcessBuilder(String[] cmd,File[] logfile,boolean redirectErrorStream,long timeout,TimeUnit timeUnit)`——使用ProcessBuilder运行命令 命令（允许空格）
  - `public static Path mkdir(Path path, boolean isMkdirs)`——创建文件夹
  - `public static Path mkdirs(Path path)`——mkdir -p
  - `public static Path mkdir(Path path)`——mkdir 存在会报错
  - `public static Path touch(Path path, boolean overwrite)`——创建文件
  - `public static Path touch(Path path)`——touch 直接覆盖
  - `public static LinkedList<Path> copy(Path src, Path dst)`——复制文件或文件夹（覆盖）
  - `public static LinkedList<Path> copyFollowLinks(Path src, Path dst)`——复制文件或文件夹（覆盖）
  - `public static LinkedList<Path> copyExcludeDotfiles(Path src, Path dst)`——复制当前目录中除了点文件以外的文件(仅允许目录)，且不跟随符号链接
  - `public static LinkedList<Path> move(Path src, Path dst)`——移动文件或文件夹（覆盖）
  - `public static LinkedList<Path> moveFollowLinks(Path src, Path dst)`——移动文件或文件夹（覆盖）
  - `public static LinkedList<Path> moveExcludeDotfiles(Path src, Path dst)`——移动当前目录中除了点文件以外的文件(仅允许目录)，且不跟随符号链接
  - `public static LinkedList<Path> rm(Path path)`——rm 删除文件（不能删除文件夹和隐藏文件），如果是目录，仅会删除目录中的文件（没有递归）
  - `public static LinkedList<Path> rmAll(Path path, boolean action)`——删除文件、文件夹
  - `public static LinkedList<Path> rmAll(Path path)`——rm -r递归删除删除文件(包括隐藏文件)、文件夹
  - `public static LinkedList<Path> rmExcludeDotfiles(Path path)`——删除当前目录中除了点文件以外的文件(仅允许目录)
  - `public static LinkedList<Path> tree(Path path, int level, boolean includeHidden, boolean followLinks)`——tree列出所有文件
  - `public static LinkedList<Path> tree(Path path)`——tree 列出所有（不跟随Link）
  - `public static LinkedList<Path> la(Path path)`——ls -a 列出包括隐藏文件在内的文件
  - `public static LinkedList<Path> ls(Path path)`——ls列出文件（隐藏文件除外）
  - `public static boolean isDotfiles(Path path)`——是否是点开头文件（隐藏文件）
  - `public static boolean isInDotDirectory(Path path)`——是否在点目录中（隐藏目录），是的话返回true，注意：并不会因为是隐藏文件而true，只专注目录
  - `public static Object[] mergeArrays(Object[] a, Object[] b)`——合并两个数组
  - `public static LinkedList arrayList2LinkedList(List e)`——List转LinkedList
  - `private static HashMap<Integer, Integer> getPythonJavaByteMap(boolean pythonBytesKey)`——私有方法，返回Python Bytes对应的JavaBytes 字典
  - `public static byte pythonbyte2javabyte(int pybyte)`——Python byte转 Java byte
  - `public static int javabyte2pythonbyte(int jbyte)`——Java byte转 Python byte
  - `public static byte[] pythonbytes2javabytes(byte[] pybytes)`——Pythonbyte数组转Javabyte数组
  - `public static int[] javabytes2pythonbytes(byte[] jbytes)`——Javabyte数组转Pythonbyte数组
- AdministratorChecker——辅助类

### Tools_Utils

>Tools项目中的工具包不准依赖其他项目，只能自己成一个文件，单文件, 可以是依赖，也可以是模板参考

- IpAddress——IP识别
  - `IpAddressFilter.java`——IP识别
    - `public static boolean isValidIPv4(String ipv4Address) {`——是否是ipv4
    - `public static boolean isValidIPv6(String ipv6Address) {`——是否是ipv6
    - `public static boolean isValidIPv4AddressWithPort(String ipWithPort) {`——是否是ipv4+port
    - `public static boolean isValidIPv6AddressWithPort(String ipWithPort) {`——是否是ipv6+port
    - `public static int getIpType(String ip) {`——检测ip是哪一类（注意：ipv6需要中括号！）：`"IPv4":1/"IPv6":2/"IPv4 with Port":3/"IPv6 with Port":4/"Invalid IP":5`
    - `public static Pair<String, String> splitIpAndPort(String ipAddressWithPort) {`——返回ip和端口

  - `Pair`——模拟Kotlin pair

- RandomNumber
  - RandomNumber——随机数
    - `public static int randomInt(int min, int max) {`
    - `public static int secureRandomInt(int min, int max) {`
    - `public static int mathRandomInt(int min, int max) {`
- ScheduleTask——用来代替While true检查元素变化的周期任务
  - `public class ScheduleTask {`
    - `public void setExecAsScheduleAtFixedRate(boolean execAsScheduleAtFixedRate) {`——设置模式是 scheduleAtFixedRate scheduleWithFixedDelay
    - `public ScheduleTask(Runnable task, long initialDelaySecond, long periodSecond, AtomicBoolean control) {`——条件超时
    - `public ScheduleTask(Runnable task, long initialDelaySecond, long periodSecond, AtomicBoolean control, TimeUnit timeUnit) {`——条件超时
    - `public ScheduleTask(Runnable task, long initialDelaySecond, long periodSecond, AtomicBoolean control, long checkIinitialDelayMicroSecond, long checkPeriodMicroSecond, TimeUnit timeUnit) {`——条件超时构造方法
    - `public ScheduleTask(Runnable task, long initialDelaySecond, long periodSecond) {`——会一直运行此任务，除非外部调用cancle方法
    - `public ScheduleTask(Runnable task, long initialDelaySecond, long periodSecond, TimeUnit timeUnit) {`——会一直运行此任务，除非外部调用cancle方法
    - `public ScheduleTask(Runnable task, long initialDelaySecond, long periodSecond, long taskTimeout) {`——运行超时
    - `public ScheduleTask(Runnable task, long initialDelaySecond, long periodSecond, long taskTimeout, TimeUnit timeUnit) {`——超时后会自动退出
    - `public void startTask() {`——开始任务
    - `private void startWithControl(AtomicBoolean control) {`——检测控制元素来决定是否停止
    - `private void startWithoutControl() {`——超时控制
    - `public void cancelTask() {`——取消任务

## Node.js——ESM

引入：复制文件夹到项目中，`npm i`

```
// 按需引入
import { prompts as green } from './msystem/msystem.mjs'
green("终端绿色字体");

// 整体引入
import * as msystem from './msystem/msystem.mjs';
msystem.prompte("终端红色字体");
```

- maes——AES加密模块
- mHashcode——对象数字Hashcode生成（不是Hex!）
- mqrcode——二维码生成与扫描
- msystem——系统相关操作

### maes

>AES加密模块

- aes_tools.mjs——工具模块
  - `export const bytes2hex=(bytes)=>{`——bytes[]=>hex
  - `export const hex2bytes=(hex)=>{`——hex=>bytes[]
  - `export const keyPadding = (stringKey, length) => {`——密钥长度补全
  - `export const bytesToString = (arr) => {`——读取UTF8编码的字节，并专为Unicode的字符串
  - `export const stringToBytes = (str, isGetBytes = true) => {`——将字符串格式化为UTF8编码的字节
  - `export const bytesToString1 = (arr) => {`——Byte数组 (UTF-8) 转字符串
  - `export const stringToBytes1 = (str) => {`——字符串转Byte数组 (UTF-8) 
  - `CHARACTER`——"UTF-8"
- aes_cfb.mjs——CFB模块
  - `export const cfbCipher = (SecuritykeyStr, initVectorStr, pwdLength = 32) => {`——返回加密器
  - `export const cfbDecipher = (SecuritykeyStr, initVectorStr, pwdLength = 32) => {`——返回解密器
  - `export const encrypt = (cipher, message) => {`——加密信息
  - `export const decrypt = (decipher, hexmessage) => {`——解密的方法
  - `export const tEncrypt = (message, key, iv, length = 32) => {`——临时加密的方法
  - `export const tDecrypt = (hexmessage, key, iv, length = 32) => {`——临时解密的方法
- aes_cbc.mjs——CBC模块
  - `export const cbcCipher = (SecuritykeyStr, initVectorStr, pwdLength = 16) => {`——返回加密器
  - `export const cbcDecipher = (SecuritykeyStr, initVectorStr, pwdLength = 16) => {`——返回解密器
  - `export const encrypt = (cipher, message) => {`——加密信息
  - `export const decrypt = (decipher, hexmessage) => {`—— 解密的方法
  - `export const tEncrypt = (message, key, iv, length = 16) => {`——临时加密的方法
  - `export const tDecrypt = (hexmessage, key, iv, length = 16) => {`——临时解密的方法
- maes.mjs——用于引用导出的模块
  - `export class cfbCipher {`——可以反复使用的CFB类
  - `export class cbcCipher {`——可以反复使用的CBC类
  - `export const createCFB = (key, iv, length = 32) => {`——返回一对CFB加密器，解密器
  - `export const createCBC = (key, iv, length = 16) => {`——返回一堆CBC加密器、解密器
  - `export const encryptCBC = (cipherPair = undefined, msg = "", key = undefined, iv = undefined, length = undefined) => {`——CBC加密 如果cipherPair是undefined，则认为是临时加密，需要提供密钥向量等
  - `export const decryptCBC = (cipherPair = undefined, msg = "", key = undefined, iv = undefined, length = undefined) => {`——CBC解密 如果cipherPair是undefined，则认为是临时加密，需要提供密钥向量等
  - `export const encryptCFB = (cipherPair = undefined, msg = "", key = undefined, iv = undefined, length = undefined) => {`——CFB加密 
  - `export const decryptCFB = (cipherPair = undefined, msg = "", key = undefined, iv = undefined, length = undefined) => {`——CFB解密
  - `function test() {`——测试的方法（和Python、Jave匹配）

### mHashcode

>类似Java的Hashcode() 但是不是很可靠，可能会重复
>
>https://github.com/m3talstorm/hashcode/tree/master
>
>https://stackoverflow.com/questions/194846/is-there-hash-code-function-accepting-any-object-type

- `const hashCodeObject = (value) => {`——Hashcode对象（可能重复！不像Java那样可靠）
- `const hashCodeString = (string) => {`——返回字符串的Hashcode数字

### mqrcode

>二维码模块

- `const asyncGenerateQR = async (text, width, type) => {`——内部函数，生成二维码 With async/await
- `export const createQRSync = (text, savepath, width = "500", type = "svg")`——生成二维码图像并保存
- `export const generateQRBase64Promises = (text, width = 500, type = "image/png") => {`——With promises 返回Base64图片
- `export const generateQRBase64Async = async (text, width = 500, type = "image/png") => {`—— With async/await 返回Base64图片
- `export const decodejpg2Uint8Array = (jpgFile) => {`——读取jpg图片转为Uint8Array
- `export const decodepng2Uint8Array = (pngFile) => {`——读取png图片转为Uint8Array
- `export const readQRCodeFromFileSync = async (filepath, width = undefined, height = undefined) => {`——读取二维码 目前仅支持JPG和PNG

### msystem

- `export const getExecFunction = () => {`——获取当前运行的函数名称
- `const defaultCallback = (err, data, funcname) => {`——默认回调函数 （**弃用**）
- `const onSuccess = (fn) => {`——默认的用于Promise的函数 （**弃用**）
- `const onFailed = (fn) => {`——默认的用于Promise的函数（**弃用**）
- `export const print = (str, fg = 31, bg = 0, display = 1) => {`——简单的终端颜色
- `export const prompt = (str, mode = 4) => {`——常用显示 
  - `export const prompts = (str) => {`
  - `export const prompte = (str) => {`
  - `export const prompti = (str) => {`
  - `export const promptw = (str) => {`
  - `export const promptm = (str) => {`
- `export const getFileSeparator = () => {`——获取文件分隔符
- `export const isWindows = () => {`——是否是Windows系统
- `export const setClipboard = (content, sync = true) => {`——clipboardy设置粘贴板
- `export const getClipboard = (sync = true) => {`——clipboardy获取系统粘贴板信息
- `export const prlst = (list, promptMode = 0) => {`——打印列表
- `export const getPathSeparator = () => {`——返回环境变量分隔符
- `export const getPath = () => {`——获取环境变量
- `export const arrayRemoveDuplicates = (arr) => {`——去除重复数组元素
- `export const fileType = (filepath, sync = true) => {`——判断文件类型 文件：1 文件夹：2 链接文件：3
- `export const ls = (filepath, sync = true, showHidden = false, followLinks = true, absolutePath = true) => {`——ls
- `export const la = (filepath, sync = true, followLinks = false, absolutePath = true) => {`—— 模拟la
- `export const lsFileExtfilter = (filepath, ext) => {`——返回给定扩展名的文件
- `export const fileLinkedto = (filepath) => {`——返回链接文件指向 [链接地址， 链接好坏]
- `export const tree = (filepath, showHidden = false, followLinks = false) => {`——tree 返回目录下所有文件，包括空文件夹(包含当前文件夹) 仅有同步方法
- `export const treeSync = (filepath, showHidden = false, followLinks = false) => {`——目前同上
- `export const mkdir = (dir, sync = true, overwrite = false, recursive = false, mode = "0700") => {`——新建文件夹 注意Overwrite！会覆盖的！
- `export const mkdirs = (path, sync = true, overwrite = false, mode = "0700") => {`——mkdir -p 注意Overwrite！会覆盖的！
- `export const rm = async (filepath, sync = true, recursive = false) => {`——删除文件
- `export const rmClearDirectory = async (dir, sync = true) => {`——清空目录
- `export const rmFD = async (filepath, sync = true) => {`——删除文件或者文件夹
- `export const cp = (src, dst, sync = true, recursive = false,  overwrite = true, errorOnExist = false,   dereference = false, eserveTimestamps = false,   filter = (src, dest) => {    return true;  })`——复制文件 如果recursive是false，除了src dst，其他Option选项无效
- `export const cpFD = (src, dst, sync = true,   overwrite = true, errorOnExist = false,   dereference = false, preserveTimestamps = false,   filter = (s, d) => {      return true;   })`——复制文件、文件夹
- `export const mv = (src, dst, sync = true) => {`——移动文件（重命名）
- `export const mvCPRM = (src, dst,    overwrite = true, errorOnExist = false,    dereference = false, preserveTimestamps = false,    filter = (s, d) => {        return true;    })`——先复制再删除
- `export const readFileContent = (filepath, readFile = false, sync = true, fsopenflag = 'r',    encoding = 'utf-8', callback = undefined, fsopenmode = 0o666,    fsreadoffset = 0, fsreadbufflength = 0, fsreadlength = undefined, fsreadposition = null)`——读取文件
- `export const readFileNReadlines = (filepath, splitor = undefined, code_comment = undefined, sync = true, encoding = "utf-8", ignoreNull = true) => {`——逐行读取文件
- `export const readConfFileSync = (filepath, splitor = "=", code_comment = "#") => {`——默认配置文件设置
- `export const writeFileContent = (filepath, towrite, sync = true, encoding = "utf-8", flag = "w", overwrite = true, mode = 0o666) => {`——写入文件
- `export const writeFileInAppendMode = (filepath, towrite, sync = true, encoding = "utf-8", overwrite = true, mode = 0o666) => {`——追加写入
- `export const writeByLinesAsync = (filepath, linesToWrite, flags = "w", encoding = "utf-8", EOL = "\r\n", overwrite = true) => {`——逐行写入（仅异步）
- `export const sleep = async (msecond) => {`——设置延时，使用:`await sleep(xxxx);`
- `export const getIpAddr = (type=0) => {`——返回本机IP地址
- `const randomNum = (minNum, maxNum) => {`——生成从minNum到maxNum的随机数

## Python

引入：`import 【脚本名称】`

- m_AES——AES模块
- m_ColorStdout——终端彩色字体输出(Windows(受限) & Linux)
- m_ConfigFiles——配置文件相关操作
- m_Datetime——时间模块
- m_ExcelOpenpyxl——Excel相关处理(Openpyxl)（读写）
- m_ExcelXlsxWriter——Excel相关处理(XlsxWriter)（只写）
- m_Image——图像处理相关
- m_PDF——PDF文件处理
- m_ProgressBar.py——终端进度条(Linux Only)
- m_QR——二维码生成、识别
- m_RSA——RSA模块
- m_System——系统信息相关
- m_VCF3——通讯录VCF3.0文件读取
- m_Web——网络相关
- m_Winreg——Windows注册表相关

### m_AES

>与Java无缝衔接，支持CFB（默认32-256位）、CBC模式（默认长度16-128位）
>
>注意：byte[] 数据
>
>Python3： 0~256
>
>java:     -127~128

- `class AES_CFB`
  - `def padding(self, pwd, leng)`——填充到指定位数
  - `def encrypt(self, content)`——加密
  - `def decrypt(self, content)`——解密
  - `def ex_encrypt(self, content, ex_passwd, ex_iv="")`——临时加密
  - `def ex_decrypt(self, content, ex_passwd, ex_iv="")`——临时解密
- `class AES_CBC`
  - `def padding(self, pwd, leng)`——填充到指定位数
  - `def encrypt(self, content)`——加密
  - `def decrypt(self, content)`——解密
  - `def ex_encrypt(self, content, ex_passwd, ex_iv="")`——临时加密
  - `def ex_decrypt(self, content, ex_passwd, ex_iv="")`——临时解密

例:

```python
if __name__ == '__main__':
    s = "妳好Hello@"
    cipher = AES_CFB("123456", ";")
    es = cipher.encrypt(s)
    print("KEY:123456 IV:; KEY_SIZE: 32 加密：" + es)
    print("KEY:123456 IV:; KEY_SIZE: 32 解密：" + cipher.decrypt(es))
    es = cipher.ex_encrypt(s, "12345", "54321")
    print("KEY:12345 IV:54321 KEY_SIZE: 32 加密：" + es)
    print("KEY:12345 IV:54321 KEY_SIZE: 32 解密：" + cipher.ex_decrypt(es, "12345", "54321"))
    print("CBC Test: ")
    cipher = AES_CBC("123456", "4321", 32)
    es = cipher.encrypt(s)
    print("KEY:123456 IV:4321 KEY_SIZE: 32 加密：" + es)
    print("KEY:123456 IV:4321 KEY_SIZE: 32 解密：" + cipher.decrypt(es))
    es = cipher.ex_encrypt(s, "12345", "54321")
    print("KEY:12345 IV:54321 KEY_SIZE: 32 加密：" + es)
    print("KEY:12345 IV:54321 KEY_SIZE: 32 解密：" + cipher.ex_decrypt(es, "12345", "54321"))
```

### m_Array

> 列表、字典等的处理

- `averageSplitList(list2split:list, n:int)`——自动平均分配列表
- `splitListInto(list2split:list, n:int)`——将列表强制分为n个
- `permutation(lst, len)`——对给定的列表进行排列组合
- `sortListBy(list, byIndex = 0, reverse = False)`——列表排序
- `sortDictByValue(dict, reverse = False)`——按值排序字典
- `sortDictByKey(dict, reverse = False)`——按key排序字典
- `def hasDuplicates(lst):`——判断给定列表中是否有重复元素
- `def find_duplicate_indexes(lst, verbose=False):`——返回重复元素+索引
- `def modify_string_if_duplicate(string, lst):`——检查是否在给定列表中，并返回字符串，python判断给定新字符串是否包含在原列表中，如果有就加上后缀(1)，比如给定x，原来列表中有x，就返回x(1)，如果原列表中有x(1)，就返回x(2)

### m_ColorStdout

- `wprint(content, color=4)`——Windows端打印彩色文字(颜色可选受限，可自行修改源码)
- `lprint(content, fg=32, bg=40, display=1)`——Linux端打印彩色文字(颜色可选多)
- `cprint(content, mode=4`——跨平台常用颜色打印(颜色不多)

终端彩色字体输出，支持Windows Powershell、GNOME Terminal

Windows端颜色受限（可以自己修改源代码补充）

例:

```python
#!/usr/bin/python3
"""
	Mode预置：
	0：暗色蓝
	1：暗色绿
	2：暗色红
	3：亮色蓝
	4：亮色绿
	5：亮色红
	6：蓝底红字
"""
if __name__ == '__main__':
	# 打印Windows端可选颜色
	for text, color in zip(texts, colors):
		if IS_WINDOWS:
			wprint(text, color)
	for i in range(0, 7):
		cprint("cprint-测试字体", i)
```

### m_ConfigFiles

- `write_JSON(json_save_path, json_body)`——保存JSON到文本文档
- `load_JSON(json_path`——从文本文档加载JSON

加载JSON配置文件和保存JSON对象到文本

### m_Datetime

- `getFirstAndLastDay(year, month)`——获取某月的第一天和最后一天
- `getFirstDayofLastMonth(year, month)`——获取上个月第一天的日期，然后加21天就是22号的日期
- `getFirstDayofNextMonth(year, month)`——获取下个月的第一天
- `getDateToday()`——获取今天日期 2023-01-17
- `getNow(utc=False, timestamp=False)`——返回当前时间 2023-01-18 05:08:11.937176
- `getStrTimeNow()`——仅仅返回字符串时间 05:14:17
- `daysDalta(dateTime, Days)`——计算日期，加减天数
- `datetime2Timestamp(args)`——将datetime日期格式，先timetuple()转化为struct_time格式，然后time.mktime转化为时间戳
- `timestamp2Datetime(ts)`——时间戳转日期时间，仅能精确到秒
- `str2Datetime(strinput, format=0, datesplit="-", timesplit=":", datetimesplit=" ")`——字符串转日期时间
- `getDate(dateTime)`——返回日期
- `getTime(dateTime)`——返回时间
- `formatOutput(dateTime, format=None ,datesplit="-", timesplit=":", datetimesplit=" ")`——格式化输出
- `delayMsecond(t)`——精确到1ms的延迟ms定时器 1s = 1000毫秒

### m_Debug

- `def log(data, debugMode=True, note="", abfilepath=False)`——记录日志

### m_ExcelOpenpyxl

>依赖m_System和m_Arrays

- `def createNewWorkbook(firstSheetName=None):`——新建workbook但不是新建一个实际的文件
- `def createSheet(wb, sheetName, position=None):`——新增表格
- `def getSheetNames(wb):`——返回Sheets名字
- `def getSheetByIndex(wb, index):`——返回Sheet
- `def copyWorksheet(wb, ws):`——复制工作表
- `def readRows(ws, min_row, max_row, min_col, max_col, valueOnly=True):`——读行
- `def readColumns(ws, min_col, max_col, min_row, max_row, valueOnly=True):`——读列
- `def getCellRange(ws, cell1, cell2):`—— 返回一个范围内的单元格
- `def getRowOrColumns(ws, name):`——返回行或者列
- `def isnumeric(value):`—— 检查给定参数类型
- `def justReadOneRow(ws, row, deep=60, valueOnly=True):`——读1行,50列
- `def excel_column_to_number(column_name):`——列名转换为对应的列索引
- `def justReadOneColumn(ws, col, deep=50, valueOnly=True):`——读列
- `def loadExcel(filename):`——加载现有的Excel表格
- `def replaceOneCellValue(wb, ws, cell, checkValue, replacement, whenEqual=True):`——替换单元格值
- `def merge_xlsx_files(src_folder, output_file):`——简单的创建一个新的工作簿作为合并后的文件(仅合并到一张worksheet！)
- `def delete_worksheets_except_index(input_file, index_to_keep, saveAs=None):`——删除除了指定索引的worksheet以外的其他表格
- `def appendData(ws, list):`——添加一行数据
- `def worksheetSplitInto(inputFile, outputDir=None, prefix=None):`——将一个Excel中的多个worksheet拆成单个文件
- `def cellBold(ws, cellID):`——字体加粗
- `def barChart(title="标题", x="X轴", y="Y轴", type="col"):`——返回柱状图（空的）
- `def reference(worksheet=None, range_string=None, min_col=None,min_row=None,max_col=None,max_row=None):`——返回引用区间
- `def barChartAppendData(chart, data, categories):`——为柱状统计图添加数据
- `def deleteWorksheet(wb, worksheetNameOrIndex):`——删除指定的worksheet
- `def is_openpyxl_workbook(obj):`——判断是不是workbook对象
- `def copyWorksheetIntoBase(wbSrcf, wsSrc, wbDstf, savePath="", copyTitle=None):`——复制某Excel建议用另一个
- `def copyWorksheetInto(wbSrcf, wsSrc, wbDstf, savePath="", copyTitle=None):`——复制某Excel到另一个Excel，如果savePath是None，返回wb

### m_ExcelXlsxWriter

- `def permutation(lst, len)`——对给定的列表进行排列组合
- `def excelIndex2cell(row, col)`——将索引转化为字符串定位，如（0，0）==> ‘A1’
- `def colname2num(colname)`——Excel 表格单元格转换坐标（A -> 0、AA -> 26）
- `def lines_writer(worksheet_ob, content_in_lines:list, start_row)`——添加一行或者多行数据

### m_Image

- `def draw_English_text(image, x, y, string, font_size=20, color=(0, 0, 0), word_css="en.ttf", direction=None)`——添加英文文字（字体限制英文, 可自行修改中文字体）

### m_PDF

>依赖m_System

- `def add_content(pdf_in_name:str,pdf_out_name:str,content_dict:dict)`——添加PDF注释（目录）
- `def mergePdfs(directory, output_pdf_file)`——合并PDF（提供目录）
- `def image2pdf(directory, output_pdf_name, content:bool=True):`——将所给文件夹的jpg图片转为PDF文档（提供目录）
- `def pdf2images(pdfFile, dpi=200, format='png', toDir="2images"):`——拆分PDF到图片
- `def jpg_to_individual_pdf(directory):`——将指定文件夹中的每张 JPG 图片转换为单独的 PDF 文件
- `def split_pdf(input_pdf_path, output_dir, export_menu=False):`——将PDF拆分成单页的PDF文件
- `def get_bookmarks(pdf_path, outlines=None, parent_name=""):`——获取PDF书签
- `def export_bookmarks(input_pdf_path, output_txt_path, delimiter="\t", ignoreTheSame=False):`——导出PDF目录与页码的关系
- `def rotate_pdf_pages(directory, rotation_angle):`——将指定文件夹中的所有PDF文件按照指定角度顺时针旋转
- `def get_pdf_page_sizes(pdf_file):`——获取 PDF 文件中每一页的大小（宽度和高度）。

### m_QR

- `def readQR(qr_path)`——识别二维码
- `def generateQR(data, save_file="qr.png", view_only=False)`——生成二维码

### m_ProgressBar

>参考： 来源：https://blog.csdn.net/Lingdongtianxia/article/details/76359555
>
>(Linux Only)

- `timeBar(float_sec, curr, total, color=32)`——返回时间进度条(静态)
- `rateBar(curr, total, color=32, shows=None)`——显示进度条百分比(静态)
- `timeWaitfor(sec)`——等待时间的进度条(百分之一为间隔，动态)

```
[███████████████████████████████████████████████████████████████] - 还有0.00秒。

[███████████                                                    ] - 还有30.00秒。
[██████████                                                     ] 10.0  %
[██████████████████████████████████                             ] - 5号进程
```

例：

```python
import m_ProgressBar

if __name__ == '__main__':
	# 显示一条时长3秒的进度条
	m_ProgressBar.timeWaitfor(3)
	# 显示一条红色的时间进度条 时间剩余30秒、进度11% 总量100%、红色
	m_ProgressBar.timeBar(30, 11, 100, color=31)
	print()
	# 显示一条百分比进度条 进度10% 总量100%、绿色
	m_ProgressBar.rateBar(10, 100)
	print()
	# 显示一条白色的百分比进度条 进度34% 总量100%、白色 后缀“5号进程”
	m_ProgressBar.rateBar(34, 100, 38, " - 5号进程")
```

### m_RSA

>依赖m_System

- `def loadPRK(rsa_private_key_path)`——加载RSA私钥
- `def loadPUK(rsa_public_key_path)`——加载RSA公钥
- `def sign_msg(prk, msg)`——私钥签名 用私钥签名msg信息
- `def verify_msg(puk, sig_msg, msg)`——公钥验证签名, 返回是否验证
- `def encrypt_msg(puk, msg)`——加密信息, 使用公钥
- `def decrypt_msg(prk, crypto_msg)`——解密信息，使用私钥
- `def generateRSA(key_name, key_path, key_length=2048)`——用于生成RSA密钥

例:

```python
if __name__ == '__main__':
    RSA_NAME = "test"
    # 生成RSA密钥
    generateRSA(RSA_NAME, key_length=512)
    prk = loadPRK(RSA_NAME+".pem")
    puk = loadPUK(RSA_NAME+".pub")
    print(prk)
    print(puk)
    # TEST
    sign = sign_msg(prk, "妳好")
    print("私钥签名信息：{}".format(sign.hex().upper()))
    print("公钥验证信息：{}".format(verify_msg(puk, sign, "妳好")))
    hex = encrypt_msg(puk, "妳好").hex().upper()
    print("公钥加密信息：{}".format(hex))
    print("私钥解密信息：{}".format(decrypt_msg(prk, bytes.fromhex(hex))))
    # 删除RSA密钥
    os.remove(RSA_NAME+".pem")
    os.remove(RSA_NAME+".pub")
```

### m_System

- `execCommand(cmd, debug=False)`——执行命令
- `checkAdministrator()`——检查是否有管理员权限
- `cpu_count()`——返回CPU核心数
- `isBomExist(text_file_path)`——检查文件（UTF-8文本文档）头部是否包含有UTF-BOM
- `removeBom(filepath)`——移除UTF-8文件的BOM字节
- `fdExisted(file_or_dir, expect=0)`——判断文件、目录是否以期待的方式存在(0:不做约束 1:文件 2:文件夹)
- `fileOrDirectory(file_or_dir)`——判断文件还是目录（-1:other (可能不存在) 1:file 2:dir）
- `rmFD(file_or_dir, expect=0)`——删除文件
- `copyFD(src, dst)`——复制文件或者文件夹
- `moveFD(src, dst)`——移动文件或者文件夹
- `getSuffixFile(suffix, directory=".")`——返回文件夹下的带后缀的文件
- `displaySystemInfo()`——打印系统信息，仅Windows和Linux
- `javabyte2pythonbyte(javabyte)`——Python byte转Java byte
- `javabytes2pythonbytes(javabytes)`——Java byte数组转PythonByte数组
- `pythonbytes2javabytes(pythonbytes)`——Python byte数组转Java byte数组
- `pythonbyte2javabyte(pythonbyte)`——Python byte转Java byte
- `inputTimeout(str_msg, int_timeout_second)`——输入超时模块
- `def remove_newlines(text):`——去换行
- `def trim_spaces(string):`——使用 strip() 方法去除字符串前后的空格
- `def splitFilePath(file_path):`——给定路径分离出文件夹、文件名、扩展名,如果是文件夹返回None
- `def renameFile(src, dst, copyFile=False, prefix=None, suffix=None, dstWithExt=False, ext=None):`——重命名文件（可复制）
- `def editFilename(src, dst, prefix=None, suffix=None, dstWithExt=False, ext=None):`——编辑文件名
- `def check_prefix_suffix(string, prefix=None, suffix=None):`——检查字符串是否以特定前缀或后缀开头或结尾。
- `def readFileAsList(filepath, separator="\t", comment="#", ignoreNone=True, encoding="UTF-8"):`——读取文件到列表

例:

```python
if __name__ == '__main__':
    print("是否是管理员：{}".format(checkAdministrator()))
    execCommand("ls", True)
    print("CPU核心数: {}".format(cpu_count()))
    print("gitignore文件是否有UTF-8 BOM: {}".format(isBomExist("gitignore")))
    # 列出py文件
    for f in getSuffixFile("py"):
        print(f)
```

### m_VCF3

- `def getFieldnames(vcards):`——获取字段名称
- `def getFieldnamesAndParams(vcards):`——获取字段名+属性名称
- `def readVCF(vcf_file, validate=False, ignoreUnreadable=False, verbose=False):`——读取 VCF 文件并收集所有唯一的字段名
- `def process_address(adr):`——内部函数
- `def process_name(n):`——内部函数
- `def process_list(lst):`——内部函数
- `def process_bytes(data):`——内部函数
- `def vcards_to_csv(vcards, csv_file):`——将 VCF 转换为 CSV 文件
- `def csv_to_vcards(csv_file, delimiter=','):`——从 CSV 文件读取数据并转换为 vCard 对象列表。
- `def vcards_to_vcf(vcards, vcf_file, Escaping=False):`——将 vCard 对象列表导出为 VCF 文件。
- `def quoted_printable_to_utf8(quoted_printable_string):`——将 QUOTED-PRINTABLE 编码的字符串转换为 UTF-8 编码的字符串
- `def decode_vcard_lines(vcard_lines):`——将 vCard 文件中的 QUOTED-PRINTABLE 编码的行转换为 UTF-8

### m_Web

- `def getWebservertime(host)`——返回网络日期、时间['2022-06-23', '03:53:01']

### m_Winreg

>此模块Windows Only

- `createValue(key, subname, type, value)`——创建值
- `deleteValue(key, value)`——删除值
- `deleteValueEx(fullpath, value)`——删除值
- `splitRootPath(fullpath)`——分解全路径为root和子路径
- `getKeyInfo(reg, query)`——查询Key信息
- `getValue(reg, valuename)`——返回值、类型
- `getValueEx(fullpath, valuename)`——返回值、类型
- `createSubkey(reg, subname)`——创建注册表子键
- `createKey(fullpath)`——创建注册表键
- `existedSubkey(key, subname)`——是否有注册表子键
- `deleteSubkey(reg, subname)`——删除注册表子键
- `deleteKey(fullpath)`——删除注册表键
- `existedKey(fullpath)`——全路径是否存在
- `getKey(root, Path, permission=winreg.KEY_ALL_ACCESS)`——打开注册表
- `getKeyEx(fullpath, permission=winreg.KEY_ALL_ACCESS)`——打开注册表
- `getSubkey(key, mode=0)`——给定注册表，返回子键名称列表
- `hideSoftware(name, is64Bit=True, accurate=True, hide=True)`—— 是否隐藏软件卸载入口 to hide a software from regedit,  添加`Dword SystemComponent 1` 

## 更新日志

- 2024.6.19——0.3.4
  - Python PDF模块优化、新增功能、修复

- 2024.6.4——0.3.3
  - Python新增VCF3.0通讯录读取模块

- 2024.4.17——0.3.2
  - Python更新了Excel、System、Array模块

- 2024.4.13——0.3.1
  - Python更新了`m_System`

- 2024.4.9——0.3.0
  - Python更新了Excel表格拆分功能

- 2024.4.2——0.2.9
  - Python更新了Excel读取

- 2024.4.1——0.2.8
  - TODO：Python `m_ExcelXlsxWriter`分离出，需要一个可以读取Excel的

- 2024.3.21——0.2.7
  - Python PDF模块新增jpg转pdf

- 2024.1.28——0.2.6
  - Java Tool_Utils新增IP相关

- 2023.9.21——0.2.5
  - Python PDF模块新增转图像功能

- 2023.8.31——0.2.4
  - Java新增Tools工具模块

- 2023.8.26——0.2.3
  - Node新增mHashcode模块

- 2023.8.16——0.2.2
  - 修复了Node  AES模块加密功能无法复用的问题
- 2023.8.15——0.2.1
  - Node新增AES加密模块，支持CBC、CFB
- 2023.7.30——0.2.0
  - Node qrcode二维码模块基本完成
- 2023.7.23——0.1.9
  - Node system模块新增读写功能
- 2023.7.22——0.1.8(Under Dev)
  - Node模块写入文件
- 2023.7.6——0.1.7(Under Dev)
  - 新增Node.js项目库
- 2023.5.19——0.1.6
  - Python新增日志模块`m_Debug`
- 2023.4.25——0.1.5
  - Python新增`m_Array`
- 2023.4.6——0.1.4
  - Java AES模块由类调用改为示例调用
  - Python AES模块由类调用改为示例调用
  - 补充了Java项目依赖关系
- 2023.4.5——0.1.3
  - Python System模块新增Java Python byte数组转换
  - Java System模块新增Java Python byte数组转换
- 2023.03.27——0.1.2
  - Java新增System_Utils模块
- 2023.03.16——0.1.1
  - Java优化了IO模块，支持追加写入
- 2023.03.13——0.1.0
  - Java新增Datetime_Utils时间模块
- 2023.02.01——0.0.9
  - Python新增时间模块
- 2023.01.14——0.0.8
  - Python更新了System模块
  - Python正在新增Windows注册表模块
- 2023.01.03——0.0.7
  - Python更新了System模块
- 2022.08.31——0.0.6
  - 更新了Java的RSA模块，支持PKCS1(已集成第三方依赖到项目中)和PKCS8
- 2022.08.19——0.0.5
  - Python添加了二维码模块、PDF模块、图像处理模块、Excel模块、网络相关模块
- 2022.08.18——0.0.4
  - 新增RSA模块
  - 对接了Python和Java的AES模块
  - 优化了AES模块
- 2022.08.10——0.0.3
  - 添加了Python终端彩色字体输出
  - 添加了Python系统信息模块
- 2022.08.08——0.0.2
  - 添加了Java AES
- 2022.08.07——0.0.1
  - 初始化仓库



