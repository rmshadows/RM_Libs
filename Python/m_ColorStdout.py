#!/usr/bin/python3
"""
终端彩色字体输出
"""
import ctypes
import os
import sys

IS_WINDOWS = os.sep == "\\"
# 特殊符号
BLANK = "　"
BLOCK = "▇"

"""For Windows See "http://msdn.microsoft.com/library/default.asp?url=/library/en-us/winprog/winprog
/windows_api_reference.asp" for information on Windows APIs. """
# 句柄号
STD_INPUT_HANDLE = -10
STD_OUTPUT_HANDLE = -11
STD_ERROR_HANDLE = -12
# 前景色
FOREGROUND_BLACK = 0x0  # 黑
FOREGROUND_BLUE = 0x01  # 蓝
FOREGROUND_GREEN = 0x02  # 绿
FOREGROUND_RED = 0x04  # 红
FOREGROUND_INTENSITY = 0x08  # 加亮
# 背景色
BACKGROUND_BLUE = 0x10  # 蓝0
BACKGROUND_GREEN = 0x20  # 绿1
BACKGROUND_RED = 0x40  # 红2
BACKGROUND_INTENSITY = 0x80  # 加亮
colors = [FOREGROUND_BLUE, # 蓝字3
		  FOREGROUND_GREEN,  # 绿字4
		  FOREGROUND_RED,  # 红字5
		  FOREGROUND_BLUE | FOREGROUND_INTENSITY,  # 蓝字(加亮)
		  FOREGROUND_GREEN | FOREGROUND_INTENSITY,  # 绿字(加亮)
		  FOREGROUND_RED | FOREGROUND_INTENSITY,  # 红字(加亮)
		  FOREGROUND_RED | FOREGROUND_INTENSITY | BACKGROUND_BLUE | BACKGROUND_INTENSITY]  # 红字蓝底6
texts = ['蓝字', '绿字', '红字', '蓝字(加亮)', '绿字(加亮)', '红字(加亮)', '红字蓝底']


def wprint(content, color=4):
	"""
	Windows命令行彩色输出
	非Windows平台将报错
	0：暗色蓝
	1：暗色绿
	2：暗色红
	3：亮色蓝
	4：亮色绿
	5：亮色红
	6：蓝底红字
	Args:
	content: 文字
	 color: 颜色
	"""
	# E
	std_out_handle = ctypes.windll.kernel32.GetStdHandle(STD_OUTPUT_HANDLE)
	if IS_WINDOWS:
		ctypes.windll.kernel32.SetConsoleTextAttribute(std_out_handle, color)
		sys.stdout.write('%s\n' % content)  # ==> print(text)
		ctypes.windll.kernel32.SetConsoleTextAttribute(std_out_handle, FOREGROUND_RED | FOREGROUND_GREEN | FOREGROUND_BLUE)
	else:
		raise Exception("wprint: Not a windows machine.")


def lprint(content, fg=32, bg=40, display=1):
	"""
	Linux终端彩色输出
	fg前景色　　bg背景色　　颜色
		------------------------
		30 　　　　40 　　 黑色(默认背景色)
		31 　　　　41 　　 红色
		32 　　　　42 　　 绿色
		33 　　　　43 　　 黃色
		34 　　　　44 　　 蓝色
		35 　　　　45 　　 紫红色
		36 　　　　46 　　 青蓝色
		37 　　　　47 　　 白色

		display显示方式代号：
		0:终端默认设置
		1:高亮显示(默认)
		4:使用下划线
		5:闪烁
		7:反白显示
		8:不可见
	ANSI控制码：
\\33[0m 　　　　　　　　关闭所有属性
\\33[1m 　　　　　　　　设置高亮度
\\33[4m 　　　　　　　　下划线
\\33[5m 　　　　　　　　闪烁
\\33[7m 　　　　　　　　反显
\\33[8m 　　　　　　　　消隐
\\33[30m -- \\33[37m 　 设置前景色
\\33[40m -- \\33[47m 　 设置背景色
\\33[nA 　　　　　　　　 光标上移n行
\\33[nB 　　　　　　　　 光标下移n行
\\33[nC 　　　　　　　　 光标右移n行
\\33[nD 　　　　　　　　 光标左移n行
\\33[y;xH　　　　　　　  设置光标位置
\\33[2J 　　　　　　　　  清屏
\\33[K 　　　　　　　　   清除从光标到行尾的内容
\\33[s 　　　　　　　　   保存光标位置
\\33[u 　　　　　　　　   恢复光标位置
\\33[?25l 　　　　　　　  隐藏光标
\\33[?25h 　　　　　　　 显示光标
	Args:
		content: 文字
		fg: 前景色
		bg: 背景色
		display: 显示方式
	"""
	print("\033[{0};{1};{2}m{3}\033[0m".format(display, fg, bg, content))


def cprint(content, mode=4):
	"""
	常规颜色打印
	Mode预置：
	0：暗色蓝
	1：暗色绿
	2：暗色红
	3：亮色蓝
	4：亮色绿
	5：亮色红
	6：蓝底红字
	Args:
		content: 文字
		color: 颜色
	"""
	if mode not in [0, 1, 2, 3, 4, 5, 6]:
		raise IndexError("cprint: Mode index out of range.Not in 0~6")
	if IS_WINDOWS:
		wprint(content, mode)
	else:
		if mode == 0:
			lprint(content, 34, 40, 0)
		elif mode == 1:
			lprint(content, 32, 40, 0)
		elif mode == 2:
			lprint(content, 31, 40, 0)
		elif mode == 3:
			lprint(content, 34, 40, 1)
		elif mode == 4:
			lprint(content, 32, 40, 1)
		elif mode == 5:
			lprint(content, 31, 40, 1)
		elif mode == 6:
			lprint(content, 31, 44, 1)


if __name__ == '__main__':
	# 打印Windows端可选颜色
	for text, color in zip(texts, colors):
		if IS_WINDOWS:
			wprint(text, color)
	for i in range(0, 7):
		cprint("cprint-测试字体", i)
