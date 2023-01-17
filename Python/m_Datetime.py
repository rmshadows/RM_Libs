#!/usr/bin/python3
"""
处理时间的模块
"""
import calendar
import datetime
import time


def getFirstAndLastDay(year, month):
    """
    获取第一天和最后一天
    https://www.fyovo.com/6606.html
    Args:
        year: 年份
        month: 月份

    Returns:

    """
    # 获取当前月的第一天的星期和当月总天数
    weekDay, monthCountDay = calendar.monthrange(year, month)
    # 获取当前月份第一天
    firstDay = datetime.date(year, month, day=1)
    # 获取当前月份最后一天
    lastDay = datetime.date(year, month, day=monthCountDay)
    # 返回第一天和最后一天
    return firstDay, lastDay


def getFirstDayofLastMonth(year, month):
    """
    获取上个月第一天的日期，然后加21天就是22号的日期
    https://www.shuzhiduo.com/A/ke5jE41oJr/
    Args:
        year:
        month:

    Returns:

    """
    if month == 1:
        month = 12
        year -= 1
    else:
        month -= 1
    res = datetime.datetime(year, month, 1) + datetime.timedelta(days=21)
    return res


def getFirstDayofNextMonth(year, month):
    """
    获取下个月的第一天
    Args:
        year: 年
        month: 月

    Returns:

    """
    if month == 12:
        month = 1
        year += 1
    else:
        month += 1
    res = datetime.datetime(year, month, 1) + datetime.timedelta(days=21)
    return res


def timeS2Stamp(args):
    """
    将datetime日期格式，先timetuple()转化为struct_time格式
    然后time.mktime转化为时间戳
    :param args:    datetime时间格式数据
    :return:    时间戳格式数据
    """
    res = time.mktime(args.timetuple())
    return res


#### Now
def getDateToday():
    """
    获取今天日期 2023-01-17
    Returns:
        今天
    """
    return datetime.date.today()


def getNow(utc=False):
    """
    返回当前时间
    2023-01-18 05:08:11.937176
    2023-01-17 21:09:05.977123
    Returns:
    """
    if utc:
        return datetime.datetime.utcnow()
    else:
        return datetime.datetime.now()


def getStrTimeNow():
    """
    仅仅返回字符串时间 05:14:17
    Returns: 05:14:17
    """
    return str(datetime.datetime.today()).split(".")[0].split(" ")[1]


#### Date Calc
def daysDalta(dateTime, Days):
    """
    计算日期
    Args:
        dateTime:
        Days:

    Returns:

    """
    return dateTime + datetime.timedelta(days=Days)


#### format
def str2Datetime(strinput, format=0, datesplit="-", timesplit=":", datetimesplit=" "):
    """
    字符串转日期时间
    Args:
        strinput: 字符串时间
        format: 格式0: 仅日期 2022-01-01
        1:日期+时间 2022-01-13 10:12:33
        2: 完整的datetime:
        其他：自定义格式
        datesplit:
        timesplit:
        datetimesplit:

    Returns:

    """
    dt = None
    if format == 0:
        # 仅日期 2022.01.01
        dt = datetime.datetime.strptime(strinput, "%Y{0}%m{0}%d".format(datesplit))
    elif format == 1:
        # 日期+时间
        dt = datetime.datetime.strptime(strinput, "%Y{0}%m{0}%d{2}%H{1}%M{1}%S".format(datesplit,
                                                                                       timesplit,
                                                                                       datetimesplit))
    elif format == 2:
        # 完整datetime 2023-01-18 05:56:05.288924 TODO
        dt = datetime.datetime.strptime(strinput, "%Y{0}%m{0}%d{2}%H{1}%M{1}%S.%p".format(datesplit,
                                                                                       timesplit,
                                                                                       datetimesplit))
    else:
        # 自定义格式
        dt = datetime.datetime.strptime(strinput, format)
    return dt


def getDate(dateTime):
    """
    返回日期
    Args:
        dateTime:

    Returns:

    """
    return dateTime.date()


def getTime(dateTime):
    """
    返回时间
    Args:
        dateTime:

    Returns:

    """
    return dateTime.time()


def formatOutput(dateTime, format=None ,datesplit="-", timesplit=":", datetimesplit=" "):
    """
    格式化输出
    Args:
        dateTime: 日期
        format: 自定义格式
        datesplit: 日期分隔
        timesplit: 时间分隔
        datetimesplit: 日期时间分隔

    Returns:

    """
    if format is None:
        return dateTime.strftime("%Y{0}%m{0}%d{2}%H{1}%M{1}%S".format(datesplit, timesplit, datetimesplit))
    else:
        return dateTime.strftime(format)


if __name__ == '__main__':
    today = getNow()
    print(today)
    a = str2Datetime("2023-01-18 05:56:05.288924", 2)
    # a = formatOutput(a, ".", "/", "+")
    print(a)










