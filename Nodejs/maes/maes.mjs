var toCrypt = "妳好Hello@";
var cbfpasswd = "123456"; // 0DF262FE3A6F56282D6E5B9B
var cfbiv = ";";
var cbcpasswd = "123456"; // 8494872F6E4EFC08FBD8E3EC57F04A17
var cbciv = "4321";
var cbcpwdsize = 32;

let crypto;
try {
  crypto = await import('node:crypto');
} catch (err) {
  console.error('crypto support is disabled!');
} 
// import crypto from 'crypto';

// https://www.section.io/engineering-education/data-encryption-and-decryption-in-node-js-using-crypto/
// https://medium.com/@abhishek.sinha132/aes-encryption-decryption-using-nodejs-3d3457c39bbf
const algorithm = "aes-256-cbc"; 

// generate 16 bytes of random data
const initVector = crypto.randomBytes(16);

// protected data
const message = "This is a secret message";

// secret key generate 32 bytes of random data
const Securitykey = crypto.randomBytes(32);

// the cipher function
const cipher = crypto.createCipheriv(algorithm, Securitykey, initVector);
// const cipher = crypto.createCipheriv("");

// encrypt the message
// input encoding
// output encoding
let encryptedData = cipher.update(message, "utf-8", "hex");

encryptedData += cipher.final("hex");

console.log("Encrypted message: " + encryptedData);

// the decipher function
const decipher = crypto.createDecipheriv(algorithm, Securitykey, initVector);

let decryptedData = decipher.update(encryptedData, "hex", "utf-8");

decryptedData += decipher.final("utf8");

console.log("Decrypted message: " + decryptedData);