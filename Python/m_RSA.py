#!/usr/bin/python3
"""
RSA模块
https://stuvel.eu/python-rsa-doc/usage.html#signing-and-verification
"""

import os.path as op
import rsa
import os

# 是否是Windows
import m_System

WINDOWS = (os.sep == "\\")
CHARSET="UTF-8"

def loadPRK(rsa_private_key_path):
    """
    加载RSA私钥
    Args:
        rsa_private_key_path: RSA路径

    Returns:
        私钥
    """
    # 加载私钥
    if WINDOWS:
        m_System.removeBom(rsa_private_key_path)
    with open(rsa_private_key_path, mode='rb') as privatefile:
        data = privatefile.read()
    prk = rsa.PrivateKey.load_pkcs1(data)
    print("私钥加载完毕")
    return prk


def loadPUK(rsa_public_key_path):
    """
    加载RSA公钥
    Args:
        rsa_public_key_path: 文件路径

    Returns:
        公钥
    """
    if WINDOWS:
        m_System.removeBom(rsa_public_key_path)
    with open(rsa_public_key_path, mode='rb') as pubfile:
        data = pubfile.read()
    try:
        return rsa.PublicKey.load_pkcs1(data)
    except Exception as e:
        print("\t\t公钥加载失败: {} \n\t\t尝试以load_pkcs1_openssl_pem重新加载.....".format(e))
        with open(rsa_public_key_path, mode='rb') as pubfile:
            data = pubfile.read()
        return rsa.PublicKey.load_pkcs1_openssl_pem(data)
    finally:
        print("公钥加载完毕")


def sign_msg(prk, msg:str, alg='SHA-256'):
    """
    私钥签名 用私钥签名msg信息
    Args:
        prk: 私钥
        msg: 信息
        alg: 算法

    Returns:
        bytes: 签名后的数据
    """
    message = msg.encode(CHARSET)
    # 下面等效
    # hash = rsa.compute_hash(message, 'SHA-1')
    # print(hash)
    # signature = rsa.sign_hash(hash, prk, 'SHA-256')
    return rsa.sign(message, prk, alg)


def verify_msg(puk, sig_msg:bytes, msg:str):
    """
    公钥验证签名, 返回是否验证
    Args:
        puk: 公钥
        sig_msg: 签名信息(RSA加密后的)
        msg: 要验证的字符串文字

    Returns:
        boolean
    """
    message = msg.encode(CHARSET)
    try:
        rsa.verify(message, sig_msg, puk)
        print("Verification success.")
        return True
    except Exception as e:
        print(e)
        return False


def encrypt_msg(puk, msg:bytes):
    """
    加密信息, 使用公钥
    Args:
        puk: 公钥
        msg: 明文

    Returns:
        加密后的数据byte
    """
    return rsa.encrypt(msg, puk)


def decrypt_msg(prk, crypto_msg:bytes):
    """
    解密信息，使用私钥
    Args:
        prk: 私钥
        crypto_msg: byte密文

    Returns:

    """
    message = rsa.decrypt(crypto_msg, prk)
    return message


def generateRSA(key_name, key_path=".", key_length=2048):
    """
    用于生成RSA密钥
    Args:
        key_name: 密钥文件名
        key_path: 保存路径
        key_length: 长度
    """
    (pubkey, privkey) = rsa.newkeys(key_length, poolsize=10)
    with open(op.join(key_path, key_name+".pub"), "wb") as f:
        f.write(pubkey.save_pkcs1())
    with open(op.join(key_path, key_name+".pem"), "wb") as f:
        f.write(privkey.save_pkcs1())


def __testRSA():
    RSA_NAME = "test"
    # 生成RSA密钥
    generateRSA(RSA_NAME, key_length=512)
    prk = loadPRK(RSA_NAME + ".pem")
    puk = loadPUK(RSA_NAME + ".pub")
    print(prk)
    print(puk)
    # TEST
    sign = sign_msg(prk, "妳好")
    print("私钥签名信息：{}".format(sign.hex().upper()))
    print("公钥验证信息：{}".format(verify_msg(puk, sign, "妳好")))
    hex = encrypt_msg(puk, bytes("妳好", CHARSET)).hex().upper()
    print("公钥加密信息：{}".format(hex))
    print("私钥解密信息：{}".format(decrypt_msg(prk, bytes.fromhex(hex))))
    # 删除RSA密钥
    os.remove(RSA_NAME + ".pem")
    os.remove(RSA_NAME + ".pub")


if __name__ == '__main__':
    __testRSA()








