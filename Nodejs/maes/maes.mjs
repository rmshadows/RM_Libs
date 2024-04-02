import * as cfb from "./aes_cfb.mjs";
import * as cbc from "./aes_cbc.mjs";
import { log } from "node:console";

export class cfbCipher {
  constructor(key, iv, length=32){
    this.key = key;
    this.iv = iv;
    this.length =length;
  }

  encrypt(msg){
    let kp = createCFB(this.key, this.iv, this.length);
    return encryptCFB(kp, msg);
  }

  decrypt(msg){
    let kp = createCFB(this.key, this.iv, this.length);
    return decryptCFB(kp, msg);
  }
}

export class cbcCipher {
  constructor(key, iv, length=32){
    this.key = key;
    this.iv = iv;
    this.length =length;
  }

  encrypt(msg){
    let kp = createCBC(this.key, this.iv, this.length);
    return encryptCBC(kp, msg);
  }

  decrypt(msg){
    let kp = createCBC(this.key, this.iv, this.length);
    return decryptCBC(kp, msg);
  }
}

/**
 * 返回一对CFB加密器，解密器
 * @param {string} key 
 * @param {string} iv 
 * @param {number} length 
 * @returns 
 */
export const createCFB = (key, iv, length = 32) => {
  return [cfb.cfbCipher(key, iv, length), cfb.cfbDecipher(key, iv, length)]
}

/**
 * 返回一堆CBC加密器、解密器
 * @param {string} key 密钥 
 * @param {string} iv 偏移量
 * @param {number} length 密钥填充长度
 * @returns 
 */
export const createCBC = (key, iv, length = 16) => {
  return [cbc.cbcCipher(key, iv, length), cbc.cbcDecipher(key, iv, length)]
}

/**
 * CBC加密 
 * 如果cipherPair是undefined，则认为是临时加密，需要提供密钥向量等
 * @param {Array} cipherPair  密钥对
 * @param {string} msg 信息
 * @param {string} key 
 * @param {string} iv 
 * @param {number} length 
 * @returns 
 */
export const encryptCBC = (cipherPair = undefined, msg = "", key = undefined, iv = undefined, length = undefined) => {
  if (cipherPair == undefined || key != undefined) {
    // 未定义，则认为是临时加密
    return cbc.tEncrypt(msg, key, iv, length);
  } else {
    return cbc.encrypt(cipherPair[0], msg);
  }
}

/**
 * CBC解密
 * 如果cipherPair是undefined，则认为是临时加密，需要提供密钥向量等
 * @param {Array} cipherPair  密钥对
 * @param {string} msg 信息
 * @param {string} key 
 * @param {string} iv 
 * @param {number} length 
 * @returns 
 */
export const decryptCBC = (cipherPair = undefined, msg = "", key = undefined, iv = undefined, length = undefined) => {
  if (cipherPair == undefined || key != undefined) {
    // 未定义，则认为是临时解密
    return cbc.tDecrypt(msg, key, iv, length);
  } else {
    return cbc.decrypt(cipherPair[1], msg);
  }
}


/**
 * CFB加密 
 * 如果cipherPair是undefined，则认为是临时加密，需要提供密钥向量等
 * @param {Array} cipherPair  密钥对
 * @param {string} msg 信息
 * @param {string} key 
 * @param {string} iv 
 * @param {number} length 
 * @returns 
 */
export const encryptCFB = (cipherPair = undefined, msg = "", key = undefined, iv = undefined, length = undefined) => {
  if (cipherPair == undefined || key != undefined) {
    // 未定义，则认为是临时加密
    return cfb.tEncrypt(msg, key, iv, length);
  } else {
    return cfb.encrypt(cipherPair[0], msg);
  }
}

/**
 * CFB解密
 * 如果cipherPair是undefined，则认为是临时加密，需要提供密钥向量等
 * @param {Array} cipherPair  密钥对
 * @param {string} msg 信息
 * @param {string} key 
 * @param {string} iv 
 * @param {number} length 
 * @returns 
 */
export const decryptCFB = (cipherPair = undefined, msg = "", key = undefined, iv = undefined, length = undefined) => {
  if (cipherPair == undefined || key != undefined) {
    // 未定义，则认为是临时解密
    return cfb.tDecrypt(msg, key, iv, length);
  } else {
    return cfb.decrypt(cipherPair[1], msg);
  }
}

/**
 * 测试的方法（和Python、Jave匹配）
 */
function test() {
  const msg = "妳好Hello@";
  let cfbs = createCFB("123456", ";", 32);
  let cbcs = createCBC("123456", "4321", 32);
  console.log("CFB加密前：" + msg);
  let xmsg = encryptCFB(cfbs, msg);
  console.log("CFB加密：" + xmsg);
  console.log("CFB解密：" + decryptCFB(cfbs, xmsg));
  xmsg = encryptCFB(undefined, msg, "12345", "54321", 32);
  console.log("CFB临时加密(PWD: 12345;IV:54321)：" + xmsg);
  console.log("CFB临时解密(PWD: 12345;IV:54321)：" + decryptCFB(undefined, xmsg, "12345", "54321", 32));

  console.log("CBC加密前：" + msg);
  xmsg = encryptCBC(cbcs, msg);
  console.log("CBC加密：" + xmsg);
  console.log("CBC解密：" + decryptCBC(cbcs, xmsg));
  xmsg = encryptCBC(undefined, msg, "12345", "54321", 32);
  console.log("CBC临时加密(PWD: 12345;IV:54321)：" + xmsg);
  console.log("CBC临时解密(PWD: 12345;IV:54321)：" + decryptCBC(undefined, xmsg, "12345", "54321", 32));

  console.log("\n\n多次加密测试:");
  const cfb = new cfbCipher("12345", ";", 32);
  xmsg = cfb.encrypt(msg);
  console.log(xmsg);
  console.log(cfb.decrypt(xmsg));
  xmsg = cfb.encrypt(msg);
  console.log(xmsg);
  console.log(cfb.decrypt(xmsg));
  xmsg = cfb.encrypt(msg);
  console.log(xmsg);
  console.log(cfb.decrypt(xmsg));
}
test();
/**
CFB加密前：妳好Hello@
CFB加密：0DF262FE3A6F56282D6E5B9B
CFB解密：妳好Hello@
CFB临时加密(PWD: 12345;IV:54321)：601F9AD97802812B0787CAC6
CFB临时解密(PWD: 12345;IV:54321)：妳好Hello@

CBC加密前：妳好Hello@
CBC加密：8494872F6E4EFC08FBD8E3EC57F04A17
CBC解密：妳好Hello@
CBC临时加密(PWD: 12345, IV: 54321)：D0A99E4A590B06AE867B412094697E21
CBC临时解密(PWD: 12345, IV: 54321)：妳好Hello@
 */