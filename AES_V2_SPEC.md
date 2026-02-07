# AES V2 跨语言统一规范

**V2 = 安全路径：AES CBC 固定 32 字节密钥 + KDF（PBKDF2）派生。兼容（零填充、16/24 位等）留给 v1（原 AES_CBC / m_AES / maes）。**

## 1. V2 定位

- **V2**（Java：AES_V2 / AES_ToolsV2，Python：m_AES_v2，Node：aes_v2.mjs）：仅提供 **CBC + 32 字节密钥 + PBKDF2**。
- **v1**（AES_CBC、m_AES、maes）：保留原有零填充、可调 key/iv/keyLength，用于兼容旧数据与三端对齐。

## 2. V2 CBC 约定

| 项目       | 约定 |
|------------|------|
| 密钥长度   | **固定 32 字节**（256 位） |
| 密钥/IV 来源 | **PBKDF2-HMAC-SHA256** 从密码派生，不再补 0 |
| 迭代次数   | 默认 10000，可配置 |
| Salt       | 每次加密 16 字节随机，密文前 32 个 hex 字符为 salt |
| 密文格式   | `salt_hex(32) + cipher_hex` |
| 算法       | AES/CBC/PKCS5Padding（块大小 16） |

## 3. 两类接口（三端一致）

AES 本身是二进制运算；为方便不同场景，V2 提供两种形式：

| 形式 | 说明 | 密文格式 |
|------|------|----------|
| **String（hex）** | 明文/密文为字符串，便于存配置、JSON、数据库 | 密文 = 大写十六进制，前 32 字符 = salt_hex，后为 cipher_hex |
| **byte[]（二进制）** | 明文/密文为字节数组，AES 原生形式，适合文件、二进制载荷 | 密文 = salt(16 字节) + CBC 密文 |

- **String**：`encryptCBC(plainText, password)` / `decryptCBC(cipherHex, password)`。
- **byte[]**：Java `encryptCBC(byte[], password)` → `byte[]`，Python `encrypt_cbc_bytes` / `decrypt_cbc_bytes`，Node `encryptCBCBytes` / `decryptCBCBytes`。

## 4. 文件位置

| 语言   | V2 文件 |
|--------|---------|
| Java   | `AES_Utils/AES_ToolsV2.java`、`AES_Utils/AES_V2.java` |
| Python | `Python/m_AES_v2.py` |
| Node   | `Nodejs/maes/aes_v2.mjs` |
