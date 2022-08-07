# RM_Libs

## 简介

一些函数库罢了。

## Bash

引入：`source 【脚本路径】`

- Println.sh——终端字体颜色输出

### Println.sh

使用：`prompt [参数] "文字"`

红色：警告、重点

黄色：警告、一般打印

绿色：执行日志

蓝色、白色：常规信息

例：

```bash
#!/bin/bash
source Println.sh
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

## Java

引入：`import 【包名】`

- AES_Utils
  - AES_CBC
  - AES_CFB

### AES_Utils

>AES模块，CBC模式和CFB模式

(TODO)

## Python

引入：`import 【脚本名称】`

- m_ProgressBar.py——终端进度条(Linux Only)

### m_ProgressBar

>参考： 来源：https://blog.csdn.net/Lingdongtianxia/article/details/76359555
>
>(Linux Only)

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

## 更新日志

- 2022.08.07——0.0.1
  - 初始化仓库



