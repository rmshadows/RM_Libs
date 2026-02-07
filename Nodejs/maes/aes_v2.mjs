/**
 * AES V2：仅 CBC，32 字节密钥 + KDF（PBKDF2）派生。
 * 兼容（零填充、16/24 位）留给 v1（maes）。
 * 规范见项目根目录 AES_V2_SPEC.md
 */
import crypto from "node:crypto";

const CHARSET = "utf8";
const IV_LEN = 16;
const CBC_KEY_LENGTH = 32;
const SALT_LEN = 16;
const DEFAULT_ITERATIONS = 10000;

function hex2bytes(hex) {
    if (!hex || typeof hex !== "string") return Buffer.alloc(0);
    const h = hex.toLowerCase().replace(/\s/g, "");
    if (h.length % 2 !== 0) throw new Error("hex 长度必须为偶数");
    return Buffer.from(h, "hex");
}

function bytes2hex(data) {
    const buf = Buffer.isBuffer(data) ? data : Buffer.from(data);
    return buf.toString("hex").toUpperCase();
}

function deriveKeyAndIV(password, salt, iterations) {
    const pwd = Buffer.from(password == null ? "" : password, CHARSET);
    const s = Buffer.isBuffer(salt) ? salt : Buffer.alloc(0);
    const dk = crypto.pbkdf2Sync(pwd, s, iterations, CBC_KEY_LENGTH + IV_LEN, "sha256");
    return {
        key: dk.subarray(0, CBC_KEY_LENGTH),
        iv: dk.subarray(CBC_KEY_LENGTH, CBC_KEY_LENGTH + IV_LEN),
    };
}

function randomSalt() {
    return crypto.randomBytes(SALT_LEN);
}

function doCBCEncrypt(plain, keyBuf, ivBuf) {
    const cipher = crypto.createCipheriv("aes-256-cbc", keyBuf, ivBuf);
    return Buffer.concat([cipher.update(plain), cipher.final()]);
}

function doCBCDecrypt(cipherBytes, keyBuf, ivBuf) {
    const decipher = crypto.createDecipheriv("aes-256-cbc", keyBuf, ivBuf);
    return Buffer.concat([decipher.update(cipherBytes), decipher.final()]);
}

/**
 * V2 CBC 加密（字符串）：返回密文为大写十六进制，格式 salt_hex(32) + cipher_hex。
 */
export function encryptCBC(plainText, password, iterations = DEFAULT_ITERATIONS) {
    const plain = Buffer.from(plainText, CHARSET);
    const out = encryptCBCBytes(plain, password, iterations);
    return bytes2hex(out);
}

/**
 * V2 CBC 解密（字符串）：密文为十六进制字符串，前 32 字符为 salt。
 */
export function decryptCBC(cipherHex, password, iterations = DEFAULT_ITERATIONS) {
    const raw = hex2bytes(cipherHex || "");
    if (raw.length <= SALT_LEN) throw new Error("密文过短，缺少 salt");
    const plainBytes = decryptCBCBytes(raw, password, iterations);
    return plainBytes.toString(CHARSET);
}

/**
 * V2 CBC 加密（二进制）：输入输出均为 Buffer/字节。返回：前 16 字节 salt + 后续 AES 密文。
 * @param {Buffer|Uint8Array} plainBytes
 * @param {string} password
 * @param {number} [iterations=10000]
 * @returns {Buffer}
 */
export function encryptCBCBytes(plainBytes, password, iterations = DEFAULT_ITERATIONS) {
    const plain = Buffer.isBuffer(plainBytes) ? plainBytes : Buffer.from(plainBytes || []);
    const salt = randomSalt();
    const { key, iv } = deriveKeyAndIV(password, salt, iterations);
    const cipherBytes = doCBCEncrypt(plain, key, iv);
    return Buffer.concat([salt, cipherBytes]);
}

/**
 * V2 CBC 解密（二进制）：输入为 Buffer（前 16 字节 salt + 密文），返回明文字节。
 */
export function decryptCBCBytes(cipherBytes, password, iterations = DEFAULT_ITERATIONS) {
    const buf = Buffer.isBuffer(cipherBytes) ? cipherBytes : Buffer.from(cipherBytes || []);
    if (buf.length <= SALT_LEN) throw new Error("密文过短，至少需要 " + (SALT_LEN + 1) + " 字节");
    const salt = buf.subarray(0, SALT_LEN);
    const cipherOnly = buf.subarray(SALT_LEN);
    const { key, iv } = deriveKeyAndIV(password, salt, iterations);
    return doCBCDecrypt(cipherOnly, key, iv);
}
