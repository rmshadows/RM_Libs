let crypto;
try {
    crypto = await import('node:crypto');
} catch (err) {
    console.error('crypto support is disabled!');
}
import { log } from "node:console";
// import crypto from 'crypto';
import * as tools from "./aes_tools.mjs"


/**
 * 返回加密器
 * @param {*} SecuritykeyStr 
 * @param {*} initVectorStr 
 * @param {*} pwdLength 16/24/32 = 128/192/256
 * @param {*} algorithm 
 * @returns 
 */
export const cfbCipher = (SecuritykeyStr, initVectorStr, pwdLength = 32) => {
    try {
        // 转字符串为byte[] padding
        let bSecuritykeyStr = tools.keyPadding(SecuritykeyStr, pwdLength);
        let binitVectorStr = tools.keyPadding(initVectorStr, 16);
        // 转array到arraybuffer
        let Securitykey = tools.arrayToArrayBuffer(bSecuritykeyStr);
        let initVector = tools.arrayToArrayBuffer(binitVectorStr);
        // console.log(Securitykey);
        // crypto.randomBytes(16);
        let algorithm = ""
        if (pwdLength == 16) {
            algorithm = "aes-128-cfb"
        } else if (pwdLength == 24) {
            algorithm = "aes-192-cfb"
        } else if (pwdLength == 32) {
            algorithm = "aes-256-cfb"
        }
        return crypto.createCipheriv(algorithm, Securitykey, initVector);
    } catch (error) {
        console.log("cfbCipher创建失败：" + error);
        return undefined
    }
}

/**
 * 返回解密器
 * @param {*} SecuritykeyStr 
 * @param {*} initVectorStr 
 * @param {*} pwdLength 
 * @returns 
 */
export const cfbDecipher = (SecuritykeyStr, initVectorStr, pwdLength = 32) => {
    try {
        // 转字符串为byte[] padding
        let bSecuritykeyStr = tools.keyPadding(SecuritykeyStr, pwdLength);
        let binitVectorStr = tools.keyPadding(initVectorStr, 16);
        // 转array到arraybuffer
        let Securitykey = tools.arrayToArrayBuffer(bSecuritykeyStr);
        let initVector = tools.arrayToArrayBuffer(binitVectorStr);
        // console.log(Securitykey);
        // crypto.randomBytes(16);
        let algorithm = ""
        if (pwdLength == 16) {
            algorithm = "aes-128-cfb"
        } else if (pwdLength == 24) {
            algorithm = "aes-192-cfb"
        } else if (pwdLength == 32) {
            algorithm = "aes-256-cfb"
        }
        return crypto.createDecipheriv(algorithm, Securitykey, initVector);
    } catch (error) {
        console.log("cfbDecipher创建失败：" + error);
        return undefined
    }
}


/**
 * 加密信息
 * @param {*} cipher 加密器
 * @param {*} message 要加密的信息
 * @returns 
 */
export const encrypt = (cipher, message) => {
    try {
        let encryptedData = cipher.update(message, "utf-8", "hex");
        encryptedData += cipher.final("hex");
        encryptedData = encryptedData.toUpperCase();
        // console.log("Encrypted message: " + encryptedData);
        return encryptedData;
    } catch (error) {
        console.log("CFB加密失败：" + error);
        return undefined
    }
}


/**
 * 解密的方法
 * @param {*} decipher 
 * @param {*} hexmessage 
 * @returns 
 */
export const decrypt = (decipher, hexmessage) => {
    try {
        // the decipher function
        let decryptedData = decipher.update(hexmessage.toLowerCase(), "hex", "utf-8");
        decryptedData += decipher.final("utf8");
        // console.log("Decrypted message: " + decryptedData);
        return decryptedData;
    } catch (error) {
        console.log("CFB解密失败：" + error);
        return undefined
    }
}

/**
 * 临时加密的方法
 * @param {*} message 
 * @param {*} key 
 * @param {*} iv 
 * @param {*} length 
 * @returns 
 */
export const tEncrypt = (message, key, iv, length = 32) => {
    try {
        let cipher = cfbCipher(key, iv, length);
        let encryptedData = cipher.update(message, "utf-8", "hex");
        encryptedData += cipher.final("hex");
        encryptedData = encryptedData.toUpperCase();
        // console.log("CFB Encrypted message: " + encryptedData);
        return encryptedData;
    } catch (error) {
        console.log("CFB临时加密失败：" + error);
        return undefined
    }
}


/**
 * 临时解密的方法
 * @param {*} decipher 
 * @param {*} hexmessage 
 * @returns 
 */
export const tDecrypt = (hexmessage, key, iv, length = 32) => {
    try {
        // the decipher function
        let decipher = cfbDecipher(key, iv, length);
        let decryptedData = decipher.update(hexmessage.toLowerCase(), "hex", "utf-8");
        decryptedData += decipher.final("utf8");
        // console.log("Decrypted message: " + decryptedData);
        return decryptedData;
    } catch (error) {
        console.log("CFB临时解密失败：" + error);
        return undefined
    }
}
