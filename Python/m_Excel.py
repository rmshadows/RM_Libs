#!/usr/bin/python3
import itertools
import math


def permutation(lst, len):
    """
    对给定的列表进行排列组合
    Args:
        lst: list[str] e.g.:["A", "B", "C"]
        len: 长度 int e.g.:4

    Returns:
        list: 排列组合
    """
    result = []
    for x in itertools.product(*[lst] * len):
        result.append("".join(x))
    return result


def excelIndex2cell(row, col):
    """
    将索引转化为字符串定位，如（0，0）==> ‘A1’
    注意： 有局限性！
    Args:
        row: 行
        col: 列

    Returns:
        str: 字符串单元格名称 如（0，0）==> ‘A1’
    """
    result = []
    # 临时列表
    temp_col = []
    col_l = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
             'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
             'U', 'V', 'W', 'X', 'Y', 'Z']

    # 处理列
    # print(col)
    # 判断位数
    wei = col + 1
    n = 1
    while wei - math.pow(26, n) > 0:
        wei = wei - math.pow(26, n)
        n += 1
    # 余
    left = int(wei)
    wei = n
    # print("位数：{} 余：{}".format(wei, left))
    # 数量
    amo = int(math.pow(26, wei))
    # 列表
    lst = permutation(col_l, n)
    # print(len(lst))
    col = str(lst[left - 1])

    # 处理行
    row = str(row + 1)
    return col + row


def colname2num(colname):
    """
    Excel 表格单元格转换坐标
    A > 0
    AA > 26
    AAA > 702
    Args:
        colname: 单元格(列)

    Returns:
        int: 列(0开始)
    """
    if type(colname) is not str:
        return colname
    col = 0
    power = 1
    for i in range(len(colname) - 1, -1, -1):
        ch = colname[i]
        col += (ord(ch) - ord('A') + 1) * power
        power *= 26
    col = int(col)
    col = col - 1
    return col


def lines_writer(worksheet_ob, content_in_lines:list, start_row):
    """
    添加一行或者多行数据
    Args:
        worksheet_ob: worksheet对象
        content_in_lines: Excel表格的每一行 [[A1, B1, C1],[A2, B2, C2]]
        start_row: 起始行

    Returns:
        返回下次的起始行
    """
    for line in content_in_lines:
        start_col = 0
        for para in line:
            worksheet_ob.write(start_row, start_col, para)
            start_col += 1
        start_row += 1
    return start_row


if __name__ == '__main__':
    print("Hello World")










