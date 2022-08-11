# RM_Libs

## 简介

一些函数库罢了。

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

### AES_Utils

>AES模块，CBC模式和CFB模式

- AES CBC
- AES CFB
- AES Tool
  - `public static byte[] hex2bytes(String inputHexString)`——十六进制字符串转Byte数组
  - `public static String bytes2hex(byte[] b)`——Byte数组转十六进制字符串

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

## Python

引入：`import 【脚本名称】`

- m_ColorStdout——终端彩色字体输出(Windows(受限) & Linux)
- m_ConfigFiles——配置文件相关操作
- m_ProgressBar.py——终端进度条(Linux Only)
- m_System——系统信息相关

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

### m_System

- `execCommand(cmd, debug=False)`——执行命令
- `checkAdministrator()`——检查是否有管理员权限
- `cpu_count()`——返回CPU核心数
- `isBomExist(text_file_path)`——检查文件（UTF-8文本文档）头部是否包含有UTF-BOM
- `removeBom(filepath)`——移除UTF-8文件的BOM字节
- `getSuffixFile(suffix, directory=".")`——返回文件夹下的带后缀的文件

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

## 更新日志

- 2022.08.10——0.0.3
  - 添加了Python终端彩色字体输出
  - 添加了Python系统信息模块
- 2022.08.08——0.0.2
  - 添加了Java AES
- 2022.08.07——0.0.1
  - 初始化仓库



