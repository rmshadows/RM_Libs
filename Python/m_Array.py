#!/usr/bin/python3
"""
列表、字典处理
"""
import itertools


def averageSplitList(list2split:list, n:int):
    """
    平均分配到列表
    e.g.:
    [1,2,3,4,5,6,7] 3
    [1,2,3] [4,5,6] [7]
    Args:
        list2split: 列表
        n: 平分后每份列表包含的个数n

    Returns:

    """
    for i in range(0, len(list2split), n):
        yield list2split[i:i + n]


def splitListInto(list2split:list, n:int):
    """
    将列表强制分为n个
    e.g.:
    [1,2,3,4,5,6,7,8,9] 4
    [[1, 2], [3, 4], [5, 6], [7, 8, 9]]
    https://www.pythonheidong.com/blog/article/1090214/7731b9881faa69629e0d/
    Args:
        list2split: 列表
        n: 份数

    Returns:
        分隔后的列表
    """
    if not isinstance(list2split, list) or not isinstance(n, int):
        return []
    ls_len = len(list2split)
    if n <= 0 or 0 == ls_len:
        return []
    if n > ls_len:
        return []
    elif n == ls_len:
        return [[i] for i in list2split]
    else:
        j = ls_len // n
        k = ls_len % n
        ### j,j,j,...(前面有n-1个j),j+k
        # 步长j,次数n-1
        ls_return = []
        for i in range(0, (n - 1) * j, j):
            ls_return.append(list2split[i:i + j])
        # 算上末尾的j+k
        ls_return.append(list2split[(n - 1) * j:])
        return ls_return


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
        result.append("".join(str(x)))
    return result


def sortListBy(list, byIndex = 0, reverse = False):
    """
    列表排序
    Args:
        list:
        byIndex: 0为默认排序，指明则按索引，如：
        [[1,2,3,4], [1,2,5,4]] 索引2
        则按第三位来排序 3<5
        reverse: 从小-大排序

    Returns:

    """
    return sorted(list, key=lambda x:x[byIndex], reverse=reverse)


def sortDictByValue(dict, reverse = False):
    """
    按值排序字典
    Args:
        dict:
        reverse: 是否反向 从大-小

    Returns:

    """
    # 按Value排序  {0: 65, 2: 5} -> [(2, 5), (0, 65)] 这里是元组
    temp_sorted_dict_list = sorted(dict.items(), key=lambda kv: (kv[1], kv[0]))
    # 元组转列表 ii:(2, 5) iii:2 5
    tempsortedlist = []
    result_dict = {}
    for i in temp_sorted_dict_list:
        temp_lst = []
        for j in i:
            temp_lst.append(j)
        tempsortedlist.append(temp_lst)
    if reverse:
        tempsortedlist.reverse()
    for i in tempsortedlist:
        result_dict[i[0]] = i[1]
    return result_dict


def sortDictByKey(dict, reverse = False):
    """
    按key排序字典
    Args:
        dict:
        reverse: 是否反向 从大-小

    Returns:

    """
    # 按Value排序  {0: 65, 2: 5} -> [(2, 5), (0, 65)] 这里是元组
    temp_sorted_dict_list = sorted(dict.items(), key=lambda kv: (kv[0], kv[1]))
    # 元组转列表 ii:(2, 5) iii:2 5
    tempsortedlist = []
    result_dict = {}
    for i in temp_sorted_dict_list:
        temp_lst = []
        for j in i:
            temp_lst.append(j)
        tempsortedlist.append(temp_lst)
    if reverse:
        tempsortedlist.reverse()
    for i in tempsortedlist:
        result_dict[i[0]] = i[1]
    return result_dict


if __name__ == '__main__':
    l1 = [1,4,2,3,5,9,1]
    l2 = [3,3,2,4,5,5,2]
    l3 = [2,2,2,4,5,5,3]
    l = [l1,l2,l3]
    d1 = {1:3, 4:2, 3:5}

