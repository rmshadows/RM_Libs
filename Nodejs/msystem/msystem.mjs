"use strict";
import clipboard from 'clipboardy';
import { log } from 'console';
import { randomFill } from 'crypto';
import { resolve } from 'dns';
import fs from 'fs';
import { endianness } from 'os';
var fsPromise = fs.promises;

/**
 * 模块内部函数
 */
/**
 * 获取当前运行的函数名称
 * @returns type:function 函数名
 */
export const getExecFunction = () => {
    let names = new Error().stack.match(/at (.*?) /g);
    let name = names[1].replace('at ', '').trim();
    return eval(name);
}
/**
 * 默认回调函数
 * let name = getExecFunction().name;
 * fs.mkdir(dir, { recursive: recursive, mode: mode }, (err, data) => defaultCallback(err, data, name));
 * @param {*} err 
 * @param {*} data 
 * @param {string} funcname 函数名 
 */
const defaultCallback = (err, data, funcname) => {
    if (err) {
        console.log(err);
    } else {
        // process.stdout.write(funcname);
        console.log("Default callback: Function " + funcname + "() has reached callback.");
    }
}
/**
 * 默认的用于Promise的函数
 * @param {*} fn 参数列表 这里默认函数名
 */
const onSuccess = (fn) => {
    console.log(fn[0] + " Success.");
}
const onFailed = (fn) => {
    console.log(fn[0] + " Failed.");
}


/**
 * clipboardy粘贴板操作
 */
/**
 * clipboardy设置粘贴板
 * @param {string} content content 内容
 * @param {boolean} sync 是否同步， if true Doesn't work in browsers.
 * @returns 返回字符串内容
 */
export const setClipboard = async (content, sync = true) => {
    let result = false;
    if (sync) {
        // Doesn't work in browsers.
        try {
            clipboard.writeSync(content);
            if (clipboard.readSync() === content) {
                result = true;
            } else {
                console.log("setClipboard: 似乎失败了");
            }
            console.log("setClipboard:设置粘贴板:" + content);
        } catch (error) {
            console.log("setClipboard failed:" + error);
        }
    } else {
        clipboard.write(content).then(async () => {
            if (await clipboard.read() === content) {
                result = true;
            } else {
                console.log("setClipboard: 似乎失败了");
            }
            console.log("setClipboard:设置粘贴板:" + content);
        }).catch((err) => {
            console.log("setClipboard failed:" + error);
        });
    }
    return result;
};


/**
 * clipboardy获取系统粘贴板信息
 * @param {boolean} sync 是否同步，If false, Doesn't work in browsers.
 * @returns 返回字符串内容
 */
export const getClipboard = async (sync = true) => {
    let content = "";
    if (sync) {
        try {
            content = clipboard.readSync();
            console.log("getClipboard:粘贴板读取:" + content);
        } catch (error) {
            console.log("getClipboard failed:" + error);
        }
    } else {
        clipboard.read().then((data) => {
            console.log("getClipboard:粘贴板读取:" + (content = data));
        }).catch((err) => {
            console.log("getClipboard(): " + err);
        });
    }
    return content;
}


/**
 * 文件IO操作
 */

export const fileType = async (filepath, sync = true) => {
    let fn = getExecFunction().name;
    let t = 0;
    try {
        if (sync) {
            let fstat = fs.lstatSync(filepath);
            if (fstat.isSymbolicLink()) {
                console.log(filepath + "是链接文件");
                return 3;
            } else if (fstat.isFile()) {
                console.log(filepath + "是文件");
                return 1;
            } else if (fstat.isDirectory()) {
                console.log(filepath + "是文件夹");
                return 2;
            } else {
                console.log(filepath + "处于未知状态");
                return -1;
            }
        } else {
            t = await fsPromise.lstat(filepath).then((fstat) => {
                if (fstat.isSymbolicLink()) {
                    console.log(filepath + "是链接文件");
                    return 3;
                } else if (fstat.isFile()) {
                    console.log(filepath + "是文件");
                    return 1;
                } else if (fstat.isDirectory()) {
                    console.log(filepath + "是文件夹");
                    return 2;
                } else {
                    console.log(filepath + "处于未知状态");
                    return -1;
                }
            });
        }
        return t;
    } catch (error) {
        console.log("fileType(): " + error);
    }
}

var a = fileType("3", true)
console.log(typeof (a));
console.log(a);

/**
 * 新建文件夹
 * @param {string} dir 路径
 * @param {boolean} sync 是否同步
 * @param {boolean} overwrite 是否覆盖
 * @param {boolean} recursive (mkdir -p ?)是否递归
 * @param {function list} callback 回调函数 列表传入函数，成功与失败,允许传入一个参数列表
 * @param {string} mode 文件权限默认"0700"
 * @returns null
 */
export const mkdir = (dir, sync = true, overwrite = false, recursive = false, callback = [onSuccess, onFailed], mode = "0700") => {
    try {
        let fn = getExecFunction().name;
        if (fs.existsSync(dir)) {
            console.log("mkdir:存在同名文件或者文件夹");
            if (overwrite) {
                rmDF(dir);
            } else {
                return;
            }
        }
        if (sync) {
            fs.mkdirSync(dir, { recursive: recursive, mode: mode });
        } else {
            // fs.mkdir(dir, { recursive: recursive, mode: mode }, (err, data) => defaultCallback(err, data, name));
            fsPromise.mkdir(dir, { recursive: recursive, mode: mode }).then(() => {
                callback[0]([fn]);
            }).catch(() => {
                callback[1]([fn]);
            });
        }
    } catch (error) {
        console.log("mkdir:" + error);
    }
}


// 删除文件
export const rmDF = async (filepath, sync = true) => {
    fn = getExecFunction().name;
    try {
        let isFile = fs.stat.isFile(filepath);
        if (sync) {
            fs.rmSync(filepath, { recursive: recursive });
        } else {
            // fs.rm()
        }
    } catch (error) {
        console.log(error);
    }
}



/**
 * 读取文件 TODO
 * @param {*} filepath 文件路径
 * @param {*} readBinary 是否返回二进制内容（否则返回文本） 
 * @param {*} sync 是否同步
 * @returns 内容
 */
export const readFileContent = (filepath, readBinary = false, sync = true, callback = defaultCallback) => {
    let content = "";
    try {
        if (sync) {
            // 同步读取
            var data = fs.readFileSync(filepath);
            if (readBinary) {
                content = data;
            } else {
                content = data.toString();
            }
            console.log("readFileContent:同步读取: " + content);
        } else {
            // 异步读取
            fs.readFile(filepath, function (err, data) {
                if (err) {
                    return console.error(err);
                }
                if (readBinary) {
                    content = data;
                } else {
                    content = data.toString();
                }
                console.log("readFileContent:异步读取: " + content);
            });
        }
    } catch (error) {
        console.log("readFileContent:" + error);
    }
    return content;
}


export const mv = (src, dst) => {
    // TODO
    try {
        fs.rename(oldname, newname);
    } catch (error) {
        console.log("readFileContent:" + error);
    }
}


