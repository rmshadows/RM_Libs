#!/usr/bin/python3
"""
处理时间的模块
"""
import calendar
import datetime
import time

#### Get day
def getFirstAndLastDay(year, month):
    """
    获取某月的第一天和最后一天
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
    #  + datetime.timedelta(days=21)
    res = datetime.datetime(year, month, 1)
    return res


def getFirstDayofNextMonth(year, month):
    """
    获取下个月的第一天
    https://www.shuzhiduo.com/A/ke5jE41oJr/
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
    # + datetime.timedelta(days=21)
    res = datetime.datetime(year, month, 1)
    return res


#### Get Now
def getDateToday():
    """
    获取今天日期 2023-01-17
    Returns:
        今天
    """
    return datetime.date.today()


def getNow(utc=False, timestamp=False):
    """
    返回当前时间
    2023-01-18 05:08:11.937176
    2023-01-17 21:09:05.977123
    Returns:
    """
    dt = None
    if utc:
        dt = datetime.datetime.utcnow()
    else:
        dt = datetime.datetime.now()
    if timestamp:
        return dt.timestamp()
    else:
        return dt


def getStrTimeNow():
    """
    仅仅返回字符串时间 05:14:17
    Returns: 05:14:17
    """
    return str(datetime.datetime.today()).split(".")[0].split(" ")[1]


#### Date Calc
def daysDalta(dateTime, Days):
    """
    计算日期，加减天数
    Args:
        dateTime:
        Days:

    Returns:

    """
    return dateTime + datetime.timedelta(days=Days)


#### format
def datetime2Timestamp(args):
    """
    将datetime日期格式，先timetuple()转化为struct_time格式
    然后time.mktime转化为时间戳
    https://www.shuzhiduo.com/A/ke5jE41oJr/
    Args:
        args: datetime时间格式数据

    Returns:
        时间戳格式数据
    """
    res = time.mktime(args.timetuple())
    return res


def timestamp2Datetime(ts):
    """
    时间戳转日期时间，仅能精确到秒
    Args:
        ts: 时间戳

    Returns:

    """
    dt = time.localtime(ts)
    return str2Datetime(time.strftime("%Y-%m-%d %H:%M:%S", dt), 1)


def str2Datetime(strinput, format=0, datesplit="-", timesplit=":", datetimesplit=" "):
    """
    字符串转日期时间
%y 两位数的年份表示（00-99）
%Y 四位数的年份表示（000-9999）
%m 月份（01-12）
%d 月内中的一天（0-31）
%H 24小时制小时数（0-23）
%I 12小时制小时数（01-12）
%M 分钟数（00-59）
%S 秒（00-59）
%a 本地简化星期名称
%A 本地完整星期名称
%b 本地简化的月份名称
%B 本地完整的月份名称
%c 本地相应的日期表示和时间表示
%j 年内的一天（001-366）
%p 本地A.M.或P.M.的等价符
%U 一年中的星期数（00-53）星期天为星期的开始
%w 星期（0-6），星期天为 0，星期一为 1，以此类推。
%W 一年中的星期数（00-53）星期一为星期的开始
%x 本地相应的日期表示
%X 本地相应的时间表示
%Z 当前时区的名称
%% %号本身
    Args:
        strinput: 字符串时间
        format: 格式0: 仅日期 2022-01-01
        1:日期+时间 2022-01-13 10:12:33
        2: 完整的datetime: 2023-01-18 05:56:05.288924
        3. 其他：自定义格式
        datesplit: 日期分隔
        timesplit: 时间分隔
        datetimesplit: 日期时间分隔

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
        # 完整datetime 2023-01-18 05:56:05.288924
        dt = datetime.datetime.strptime(strinput, "%Y{0}%m{0}%d{2}%H{1}%M{1}%S.%f".format(datesplit,
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


#### Timer
def delayMsecond(t):
    """
    精确到1ms的延迟ms定时器 1s = 1000毫秒
    原文链接：https: // blog.csdn.net / m0_38076397 / article / details / 124751667
    https://blog.csdn.net/qq_52689354/article/details/124886569
    Args:
        t: 时间

    Returns:

    """
    start, end = 0, 0
    start = time.perf_counter() * pow(10, 7)
    while (end - start < t * pow(10, 3)):
        end = time.perf_counter() * pow(10, 7)


if __name__ == '__main__':
    today = getNow()
    print(today)
    a = str2Datetime("2023-01-18 05:56:05.288924", 2)
    a = formatOutput(a, None, ".", "/", "+")
    print(a)
    # 测试计时器
    for index in range(0, 50):
        start = time.perf_counter()
        delayMsecond(10000)
        end = time.perf_counter()
        dif = (end - start) * pow(10, 3)
        print(dif)
    # 测试时间计算
    today = getNow()
    lastmf = getFirstDayofLastMonth(2023, 1)
    # print(today)
    # print(lastmf)
    a = today-lastmf
    print(a)
    print(today<lastmf)









