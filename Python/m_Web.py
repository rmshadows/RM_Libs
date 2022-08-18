#!/usr/bin/python3
import http.client
import time


def getWebservertime(host):
    """
    返回网络日期、时间
    Args:
        host: 主机

    Returns:
        网络日期、时间
        ['2022-06-23', '03:53:01']
    """
    conn = http.client.HTTPConnection(host)
    conn.request("GET", "/")
    r = conn.getresponse()
    # r.getheaders() #获取所有的http头
    ts = r.getheader('date')  # 获取http头date部分
    # 将GMT时间转换成北京时间
    ltime = time.strptime(ts[5:25], "%d %b %Y %H:%M:%S")
    # print(ltime)
    ttime = time.localtime(time.mktime(ltime) + 8 * 60 * 60)
    # print(ttime)
    dat = "%u-%02u-%02u" % (ttime.tm_year, ttime.tm_mon, ttime.tm_mday)
    tm = "%02u:%02u:%02u" % (ttime.tm_hour, ttime.tm_min, ttime.tm_sec)
    # print(dat, tm)
    webtime = [dat, tm]
    # print(webtime)
    return webtime


if __name__ == '__main__':
    t = getWebservertime('www.baidu.com')
    # ['2022-08-19', '04:03:31']
    print("获取当前时间：{}".format(t))
    print("Hello World")










