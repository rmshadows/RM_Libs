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
- IO_Utils——输入输出，文件读写
  - IO_Utils——文件读写
- RSA_Utils——RSA组件
  - com.github.xiangyuecn.rsajava——PKCS1的依赖
  - RSA_PKCS1_Utils——处理PKCS1的RSA组件
  - RSA_PKCS8_Utils——处理PKCS8的RSA组件
  - RSA_Tools——RSA公用函数

### AES_Utils

>AES模块，CBC模式和CFB模式

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

## Python

引入：`import 【脚本名称】`

- m_AES——AES模块
- m_ColorStdout——终端彩色字体输出(Windows(受限) & Linux)
- m_ConfigFiles——配置文件相关操作
- m_Datetime——时间模块
- m_Excel——Excel相关处理
- m_Image——图像处理相关
- m_PDF——PDF文件处理
- m_ProgressBar.py——终端进度条(Linux Only)
- m_QR——二维码生成、识别
- m_RSA——RSA模块
- m_System——系统信息相关
- m_Web——网络相关

### m_AES

>与Java无缝衔接，支持CFB（默认32-256位）、CBC模式（默认长度16-128位）

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

### m_Excel

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
- `averageSplitList(list2split:list, n:int)`——自动平均分配列表
- `splitListInto(list2split:list, n:int)`——将列表强制分为n个
- `displaySystemInfo()`——打印系统信息，仅Windows和Linux

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
- `hideSoftware(name, is64Bit=True, accurate=True)`——`to hide a software from regedit`, 添加`Dword SystemComponent 1`

## 更新日志

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



