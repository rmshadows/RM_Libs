#!/usr/bin/python3
"""
AES V2：仅 CBC，32 字节密钥 + KDF（PBKDF2）派生。
兼容（零填充、16/24 位）留给 v1（m_AES）。
规范见项目根目录 AES_V2_SPEC.md
"""
import hashlib
import os
from Crypto.Util.Padding import pad, unpad
from Crypto.Cipher import AES

CHARSET = "UTF-8"
IV_LEN = 16
CBC_KEY_LENGTH = 32
SALT_LEN = 16
DEFAULT_ITERATIONS = 10000


def _hex2bytes(hex_str: str) -> bytes:
    if not hex_str:
        return b""
    h = hex_str.lower().replace(" ", "")
    if len(h) % 2 != 0:
        raise ValueError("hex 长度必须为偶数")
    return bytes.fromhex(h)


def _bytes2hex(data: bytes) -> str:
    return data.hex().upper()


def _derive_key_iv(password: str, salt: bytes, iterations: int) -> tuple[bytes, bytes]:
    pwd = (password or "").encode(CHARSET)
    salt = salt or b""
    dk = hashlib.pbkdf2_hmac("sha256", pwd, salt, iterations, dklen=CBC_KEY_LENGTH + IV_LEN)
    return dk[:CBC_KEY_LENGTH], dk[CBC_KEY_LENGTH : CBC_KEY_LENGTH + IV_LEN]


def _random_salt() -> bytes:
    return os.urandom(SALT_LEN)


def encrypt_cbc(plain_text: str, password: str, iterations: int = DEFAULT_ITERATIONS) -> str:
    """V2 CBC 加密：返回密文为大写十六进制字符串，格式 salt_hex(32) + cipher_hex。"""
    out = encrypt_cbc_bytes(plain_text.encode(CHARSET), password, iterations)
    return _bytes2hex(out)


def decrypt_cbc(cipher_hex: str, password: str, iterations: int = DEFAULT_ITERATIONS) -> str:
    """V2 CBC 解密：密文为十六进制字符串，前 32 字符为 salt。"""
    h = (cipher_hex or "").lower().replace(" ", "")
    if len(h) < 32:
        raise ValueError("密文过短，缺少 salt")
    plain_bytes = decrypt_cbc_bytes(_hex2bytes(h), password, iterations)
    return plain_bytes.decode(CHARSET)


# ---------- 二进制接口：密文 = salt(16 字节) + CBC 密文 ----------

def encrypt_cbc_bytes(
    plain_bytes: bytes, password: str, iterations: int = DEFAULT_ITERATIONS
) -> bytes:
    """V2 CBC 加密，输入输出均为字节。返回：前 16 字节 salt + 后续 AES 密文。"""
    plain_bytes = plain_bytes or b""
    salt = _random_salt()
    key_bytes, iv_bytes = _derive_key_iv(password, salt, iterations)
    plain_bytes = pad(plain_bytes, 16)
    cipher_bytes = AES.new(key_bytes, AES.MODE_CBC, iv_bytes).encrypt(plain_bytes)
    return salt + cipher_bytes


def decrypt_cbc_bytes(
    cipher_bytes: bytes, password: str, iterations: int = DEFAULT_ITERATIONS
) -> bytes:
    """V2 CBC 解密，输入输出均为字节。输入：前 16 字节 salt + 后续密文。"""
    if not cipher_bytes or len(cipher_bytes) <= SALT_LEN:
        raise ValueError(f"密文过短，至少需要 {SALT_LEN + 1} 字节（salt + 至少 1 字节密文）")
    salt = cipher_bytes[:SALT_LEN]
    cipher_only = cipher_bytes[SALT_LEN:]
    key_bytes, iv_bytes = _derive_key_iv(password, salt, iterations)
    raw = AES.new(key_bytes, AES.MODE_CBC, iv_bytes).decrypt(cipher_only)
    return unpad(raw, 16)


if __name__ == "__main__":
    s = "妳好Hello@"
    enc = encrypt_cbc(s, "123456", 10000)
    print("V2 CBC 加密 (前32=salt):", enc[:32], "...")
    dec = decrypt_cbc(enc, "123456", 10000)
    print("V2 CBC 解密:", dec)
    assert dec == s
    print("OK")
