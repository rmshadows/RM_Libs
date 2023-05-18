#!/usr/bin/python3
"""
输出日志
"""
import datetime
import inspect
import os


def log(data, debugMode=True, note="", abfilepath=False):
    """
    显示日志
    Args:
        data: 日志
        debugMode: True显示，False不显示
        note: 备注
        abfilepath：是否使用绝对路径

    Returns:

    """
    ii = inspect.stack()
    t = datetime.datetime.now()
    td = type(data)

    # 来源文件
    f = ""
    # 行数
    fn = ""
    # 调用关系
    caller = ""
    tc = []
    tf = []
    tfn = []
    for i in range(len(ii)):
        tc.append(ii[i].function)
        if abfilepath:
            tf.append(ii[i].filename)
        else:
            tf.append(os.path.basename((ii[i].filename)))
        tfn.append(ii[i].lineno)
        # if i != len(ii)-1:
        #     c.append(ii[i].function)
    tc.reverse()
    tf.reverse()
    tfn.reverse()
    for i in tc:
        caller += i
        caller += " -> "
    for i in range(len(tf)):
        f += "{}（行{}）".format(tf[i], tfn[i])
        f += " -> "
    # 去掉尾巴
    caller = caller[:-4]
    f = f[:-4]
    # 调用函数名
    n = tc[-2]

    if debugMode:
        print("\n\033[1;38;40m{}\033[0m：    "
              "函数名：\033[1;32;40m{} ：\033[1;31;40m {} \033[0m"
              "备注：\033[1;33;40m{}\033[0m "
              "类型：\033[1;36;40m{}\033[0m "
              "内容：\033[1;38;40m{}\033[0m "
              "文件：\033[1;33;40m{}\033[0m ".format(t, n, caller, note, td, data, f))


if __name__ == '__main__':
    log("666")
