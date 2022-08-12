#!/usr/bin/python3
"""
AES模块
"""
import os

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

    def pwdPadding(self, pwd):
        """
        填充密码到指定位数
        Args:
            pwd: 待填充的密码

        Returns:
            填充后的密码
        """
        # 密钥过长的报错交给AES模块
        if len(pwd) >= self.CFB_KEY_LEN:
            return pwd
        padding_len = 32 - len(pwd)
        to_padding = ""
        for i in range(0, padding_len):
            to_padding += self.CFB_KEY_PADDING
        return pwd + to_padding

    def encrypt(self, content):
        cipher = AES.new(self.CFB_KEY, AES.MODE_CFB, bytes.fromhex("00000000000000000000000000000000"))
        result = cipher.encrypt(content.encode(CHARACTER)).hex().upper()
        return result

    def decrypt(self, content):
        cipher = AES.new(self.CFB_KEY, AES.MODE_CFB)
        result = cipher.decrypt(bytes.fromhex(content.lower())).decode(CHARACTER)
        return result

    def ex_encrypt(self, content, ex_passwd, ex_padding = "0"):
        return None

    def ex_decrypt(self, content, ex_passwd, ex_padding = "0"):
        return None

    def __init__(self, passwd, pwd_fill="0"):
        if pwd_fill == "" or pwd_fill == None:
            raise Exception("pwd_fill canot be None!")
        self.CFB_KEY_PADDING = pwd_fill
        self.CFB_KEY = self.pwdPadding(passwd).encode(CHARACTER)
        # print("AES_CFB init: \nPassword: {}(Length: {})\n Padding: {}".format(self.CFB_KEY, len(self.CFB_KEY), self.CFB_KEY_PADDING))


class AES_CBC:
    # 密钥
    CFB_KEY = None
    # 偏移量
    CFB_IV = None
    # 密钥填充
    CFB_KEY_PADDING = None
    # 密码长度
    CFB_KEY_LEN = 16

    def check_data(self, data):
        """
        检测加密的数据类型
        Args:
            data: 加密的数据
        Returns:
            要加密的数据
        """
        if isinstance(data, int):
            data = str(data)
        elif isinstance(data, bytes):
            data = data.decode()
        elif isinstance(data, str):
            pass
        else:
            raise Exception(f'加密的数据必须为str或bytes,不能为{type(data)}')
        return data

    def check_key(self, key):
        """
        检测key的长度是否为16,24或者32bytes的长度
        Args:
            key: 密钥
        """
        try:
            if isinstance(key, bytes):
                assert len(key) in [16, 24, 32]
                return key
            elif isinstance(key, str):
                assert len(key.encode()) in [16, 24, 32]
                return key.encode()
            else:
                raise Exception(f'密钥必须为str或bytes,不能为{type(key)}')
        except AssertionError:
            print('输入的长度不正确')


    def pwdPadding(self, pwd):
        """
        填充密码到指定位数
        Args:
            pwd: 待填充的密码

        Returns:
            填充后的密码
        """
        # 密钥过长的报错交给AES模块
        if len(pwd) >= self.CFB_KEY_LEN:
            return pwd
        padding_len = 32 - len(pwd)
        to_padding = ""
        for i in range(0, padding_len):
            to_padding += self.CFB_KEY_PADDING
        return pwd + to_padding

    def encrypt(self, content):
        result = ""
        result = content
        print(result)
        return result

    def decrypt(self, content):
        return None

    def ex_encrypt(self, content, ex_passwd, ex_padding = "0"):
        return None

    def ex_decrypt(self, content, ex_passwd, ex_padding = "0"):
        return None

    def __init__(self, passwd, pwd_fill="0"):
        if pwd_fill == "" or pwd_fill == None:
            raise Exception("pwd_fill canot be None!")
        self.CFB_KEY_PADDING = pwd_fill
        self.CFB_KEY = self.pwdPadding(passwd)
        print("AES_CFB init: \nPassword: {}(Length: {})\n Padding: {}".format(self.CFB_KEY, len(self.CFB_KEY), self.CFB_KEY_PADDING))


def encrypt(data, password):
    bs = AES.block_size
    pad = lambda s: s + (bs - len(s) % bs) * chr(bs - len(s) % bs)
    iv = Random.new().read(bs)
    cipher = AES.new(password, AES.MODE_CBC, iv)
    data = cipher.encrypt(pad(data))
    data = iv + data
    return (data)


def decrypt(data, password):
    bs = AES.block_size
    if len(data) <= bs:
        return (data)
    unpad = lambda s: s[0:-ord(s[-1])]
    iv = data[:bs]
    cipher = AES.new(password, AES.MODE_CBC, iv)
    data = unpad(cipher.decrypt(data[bs:]))
    return (data)


if __name__ == '__main__':
    key = "123456"
    fill = ";"
    s = "妳好Hello@"
    a = AES_CFB(key, fill)
    """
    89673EF7EEE10E60897B2C71
    PWD: 12345
    CD88C411A0958F729364034F
    8494872F6E4EFC08FBD8E3EC57F04A17
    PWD: 12345, IV: 54321
    D0A99E4A590B06AE867B412094697E21
    """
    en = a.encrypt(s)
    # de = a.decrypt(en)
    print(en)
    # print(de)

    # passwd = "01234567890123456789012345678912"
    # s = "ABC"
    # cipher = AES.new(passwd.encode(CHARACTER), AES.MODE_CFB)
    # print(cipher.encrypt(s.encode(CHARACTER)))

