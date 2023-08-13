let crypto;
try {
    crypto = await import('node:crypto');
} catch (err) {
    console.error('crypto support is disabled!');
}
// import crypto from 'crypto';
import * as tools from "./aes_tools.mjs"


/**
 * 
 * @param {*} SecuritykeyStr 
 * @param {*} initVectorStr 
 * @param {*} pwdLength 16/24/32 = 128/192/256
 * @param {*} algorithm 
 * @returns 
 */
const cfbCipher = (SecuritykeyStr, initVectorStr, pwdLength) => {
    // 转字符串为byte[] padding
    let bSecuritykeyStr = tools.keyPadding(SecuritykeyStr, pwdLength);
    let binitVectorStr = tools.keyPadding(initVectorStr, 16);

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
}

// protected data
const message = "妳好Hello@";

// const cipher = crypto.createCipheriv(algorithm, Securitykey, initVector);
const cipher = cfbCipher("123456", ";", 16);

let encryptedData = cipher.update(message, "utf-8", "hex");
encryptedData += cipher.final("hex");
console.log("Encrypted message: " + encryptedData);



