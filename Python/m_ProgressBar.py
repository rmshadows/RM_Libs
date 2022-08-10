#!/usr/bin/python3
# -*- coding:utf-8 -*-
"""
来源：https://blog.csdn.net/Lingdongtianxia/article/details/76359555
	前景色　　背景色　　颜色
	------------------------
	30 　　　　40 　　 黑色(默认背景色)
	31 　　　　41 　　 红色
	32 　　　　42 　　 绿色
	33 　　　　43 　　 黃色
	34 　　　　44 　　 蓝色
	35 　　　　45 　　 紫红色
	36 　　　　46 　　 青蓝色
	37 　　　　47 　　 白色

	显示方式代号：
	0:终端默认设置
	1:高亮显示(默认)
	4:使用下划线
	5:闪烁
	7:反白显示
	8:不可见
"""

import time
import sys


def timeBar(float_sec, curr, total, color=32):
	"""
	返回时间进度条
	Args:
		float_sec: float 秒
		curr: 当前百分比
		total: 总百分比
		color: 默认进度条绿色
	"""
	# 得到现在的比率，0<rate<1
	rate = curr / total
	# 将比率百分化，0<rate_num<100
	rate_num = int(rate * 100)
	# 进度条封装
	r = '\r[%s%s]' % ("\033[1;{0};40m█\033[0m".format(color)*curr, " "*(100-curr))
	# 显示进度条
	sys.stdout.write(r)
	sys.stdout.write(" - 还有\033[1;31;40m{0}\033[0m秒。".format(format(float_sec, ".2f")))
	# 显示进度百分比
	# sys.stdout.write(str(curr)+'%')
	# 使输出变得平滑
	sys.stdout.flush()


def rateBar(curr, total, color=32, shows=None):
	"""
	显示进度条百分比
	Args:
		curr: 当前进度 int
		total: 总量 int
		color: 进度条颜色
		shows: 进度条后缀默认显示百分比
	"""
	# 得到现在的比率，0<rate<1
	rate = curr / total
	# 将比率百分化，0<rate_num<100
	rate_num = int(rate * 100)
	# 进度条封装
	r = '\r[%s%s]' % ("\033[1;{0};40m█\033[0m".format(color)*rate_num, " "*(100-rate_num))
	# 显示进度条
	sys.stdout.write(r)
	if shows is None:
		# 显示进度百分比
		sys.stdout.write("{:^7.1f}%".format(rate*100))
	else:
		# 显示进度条
		sys.stdout.write(shows)
	# 使输出变得平滑
	sys.stdout.flush()


def timeWaitfor(sec):
	"""
	等待时间的进度条(百分之一为间隔)
	Args:
		sec: 等待时间 秒
	"""
	ti = sec/100
	for i in range(0, 101):
		time.sleep(ti)
		if i >= 99:
			sec = 0
		else:
			sec -= ti
		timeBar(sec, i, 100)
	print("\n")


if __name__ == '__main__':
	# 显示一条时长3秒的进度条
	timeWaitfor(3)
	# 显示一条红色的时间进度条 时间剩余30秒、进度11% 总量100%、红色
	timeBar(30, 11, 100, color=31)
	print()
	# 显示一条百分比进度条 进度10% 总量100%、绿色
	rateBar(10, 100)
	print()
	# 显示一条白色的百分比进度条 进度34% 总量100%、白色 后缀“5号进程”
	rateBar(34, 100, 38, " - 5号进程")

