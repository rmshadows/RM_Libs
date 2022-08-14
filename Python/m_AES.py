#!/usr/bin/python3
"""
AES模块
"""
import os

from Crypto.Util.Padding import pad, unpad
from Crypto import Random
from Crypto.Cipher import AES

IS_WINDOWS = os.sep == "\\"
CHARACTER = "UTF-8"


class AES_CFB:
    # 密钥
    CFB_KEY = None
    # 密钥填充
    CFB_KEY_PADDING = None
    # 密码长度
    CFB_KEY_LEN = 32
    # 偏移量
    CFB_IV = None

    def padding(self, pwd, leng):
        """
        填充到指定位数
        Args:
            pwd: str：待填充的数据
            leng: int：长度

        Returns:
            byte[]： 填充后的数据
        """
        # 密钥过长的报错交给AES模块
        if len(pwd) >= leng:
            return pwd
        padding_len = leng - len(pwd)
        to_padding = ""
        for i in range(0, padding_len):
            to_padding += self.CFB_KEY_PADDING
        return (pwd + to_padding).encode(CHARACTER)

    def encrypt(self, content):
        """
        加密
        Args:
            content: 内容

        Returns:
            hex_str： 十六进制字符串
        """
        # segment_size=128与Java兼容
        cipher = AES.new(self.CFB_KEY, AES.MODE_CFB, self.CFB_IV, segment_size=128)
        result = cipher.encrypt(content.encode(CHARACTER)).hex().upper()
        return result

    def decrypt(self, content):
        """
        解密
        Args:
            content: 十六进制字符串

        Returns:
            解密后的字符串
        """
        cipher = AES.new(self.CFB_KEY, AES.MODE_CFB, self.CFB_IV, segment_size=128)
        result = cipher.decrypt(bytes.fromhex(content.lower())).decode(CHARACTER)
        return result

    def ex_encrypt(self, content, ex_passwd, ex_iv=""):
        """
        临时加密
        修改填充、密码长度请另行新建类
        Args:
            content: 内容
            ex_passwd: 密码
            ex_iv: 向量

        Returns:
            十六进制字符串
        """
        cipher = AES.new(self.padding(ex_passwd, self.CFB_KEY_LEN),
                         AES.MODE_CFB,
                         self.padding(ex_iv, 16),
                         segment_size=128)
        result = cipher.encrypt(content.encode(CHARACTER)).hex().upper()
        return result

    def ex_decrypt(self, content, ex_passwd, ex_iv=""):
        """
        临时解密
        Args:
            content: 十六进制字符串
            ex_passwd: str密码
            ex_iv: str向量

        Returns:
            解密后的字符串
        """
        cipher = AES.new(self.padding(ex_passwd, self.CFB_KEY_LEN),
                         AES.MODE_CFB,
                         self.padding(ex_iv, 16),
                         segment_size=128)
        result = cipher.decrypt(bytes.fromhex(content.lower())).decode(CHARACTER)
        return result

    def __init__(self, passwd, pwd_fill="0", iv="", key_len=32):
        if pwd_fill == "" or pwd_fill is None:
            raise Exception("pwd_fill canot be None!")
        # 以下是有顺序的
        self.CFB_KEY_PADDING = pwd_fill
        self.CFB_KEY = self.padding(passwd, self.CFB_KEY_LEN)
        self.CFB_IV = self.padding(iv, 16)
        self.CFB_KEY_LEN = key_len


class AES_CBC:
    # 密钥
    CBC_KEY = None
    # 密钥填充
    CBC_KEY_PADDING = None
    # 密码长度
    CBC_KEY_LEN = 16
    # 偏移量
    CBC_IV = None

    def padding(self, pwd, leng):
        """
        填充到指定位数
        Args:
            pwd: str：待填充的数据
            leng: int：长度

        Returns:
            byte[]： 填充后的数据
        """
        # 密钥过长的报错交给AES模块
        if len(pwd) >= leng:
            return pwd
        padding_len = leng - len(pwd)
        to_padding = ""
        for i in range(0, padding_len):
            to_padding += self.CFB_KEY_PADDING
        return (pwd + to_padding).encode(CHARACTER)

    def encrypt(self, content):
        """
        加密
        Args:
            content: 内容

        Returns:
            hex_str： 十六进制字符串
        """
        # segment_size=128与Java兼容
        cipher = AES.new(self.CBC_KEY, AES.MODE_CBC, self.CBC_IV, segment_size=128)
        result = cipher.encrypt(content.encode(CHARACTER)).hex().upper()
        return result

    def decrypt(self, content):
        """
        解密
        Args:
            content: 十六进制字符串

        Returns:
            解密后的字符串
        """
        cipher = AES.new(self.CBC_KEY, AES.MODE_CBC, self.CBC_IV, segment_size=128)
        result = cipher.decrypt(bytes.fromhex(content.lower())).decode(CHARACTER)
        return result

    def ex_encrypt(self, content, ex_passwd, ex_iv=""):
        """
        临时加密
        修改填充、密码长度请另行新建类
        Args:
            content: 内容
            ex_passwd: 密码
            ex_iv: 向量

        Returns:
            十六进制字符串
        """
        cipher = AES.new(self.padding(ex_passwd, self.CBC_KEY_LEN),
                         AES.MODE_CBC,
                         self.padding(ex_iv, 16),
                         segment_size=128)
        result = cipher.encrypt(content.encode(CHARACTER)).hex().upper()
        return result

    def ex_decrypt(self, content, ex_passwd, ex_iv=""):
        """
        临时解密
        Args:
            content: 十六进制字符串
            ex_passwd: str密码
            ex_iv: str向量

        Returns:
            解密后的字符串
        """
        cipher = AES.new(self.padding(ex_passwd, self.CBC_KEY_LEN),
                         AES.MODE_CBC,
                         self.padding(ex_iv, 16),
                         segment_size=128)
        result = cipher.decrypt(bytes.fromhex(content.lower())).decode(CHARACTER)
        return result

    def __init__(self, passwd, pwd_fill="0", iv="", key_len=32):
        if pwd_fill == "" or pwd_fill is None:
            raise Exception("pwd_fill canot be None!")
        # 以下是有顺序的
        self.CBC_KEY_PADDING = pwd_fill
        self.CBC_KEY = self.padding(passwd, self.CBC_KEY_LEN)
        self.CBC_IV = self.padding(iv, 16)
        self.CBC_KEY_LEN = key_len


if __name__ == '__main__':
    s = "妳好Hello@"
    cipher = AES_CFB("123456", ";")
    es = cipher.encrypt(s)
    print(es)
    print(cipher.decrypt(es))
    es = cipher.ex_encrypt(s, "12345", "54321")
    print(es)
    print(cipher.ex_decrypt(es, "12345", "54321"))
    print("CBC Test: ")
    cipher = AES_CBC("123456", ";")
    # TODO
    es = cipher.encrypt(s)
    # print(es)
    # print(cipher.decrypt(es))
    # es = cipher.ex_encrypt(s, "12345", "54321")
    # print(es)
    # print(cipher.ex_decrypt(es, "12345", "54321"))
    
    
    
    
    
    
    
    
    
    
    # ss = "1234567890123456你"
    # # BLOCK_SIZE = AES.block_size
    # # 不足BLOCK_SIZE的补位(s可能是含中文，而中文字符utf-8编码占3个位置,gbk是2，所以需要以len(s.encode())，而不是len(s)计算补码)
    # # pad = lambda s: s + (BLOCK_SIZE - len(s.encode()) % BLOCK_SIZE) * chr(BLOCK_SIZE - len(s.encode()) % BLOCK_SIZE)
    # # 去除补位
    # # unpad = lambda s: s[:-ord(s[len(s) - 1:])]
    # key = "12345678901234567890123456789012"
    # iv = bytes("1234567890123456", "UTF-8")
    # cipher = AES.new(bytes(key, "UTF-8"), AES.MODE_CFB, iv, segment_size=128)
    # # ss = pad(ss)
    # # cipher = AES.new(bytes(key, "UTF-8"), AES.MODE_CBC)
    # result = cipher.encrypt(ss.encode("UTF-8")).hex().upper()
    # print(result)
