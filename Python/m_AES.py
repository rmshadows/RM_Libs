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
    CFB_KEY = None
    CFB_KEY_FILL = None
    
    def __init__(self):
        pass


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
    # key = "0123456789123456"
    key = "妳好"
    key = bytes(key, CHARACTER)
    cipher = AES.new(key, AES.MODE_CFB)
