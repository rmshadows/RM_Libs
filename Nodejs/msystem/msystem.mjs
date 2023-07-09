/**
 * 系统操作模块，默认同步直接返回值，异步返回Promise
 */
"use strict";
import { rejects } from 'assert';
import clipboard from 'clipboardy';
import { log } from 'console';
import { randomFill } from 'crypto';
import { resolve, setDefaultResultOrder } from 'dns';
import fs from 'fs';
import { endianness } from 'os';
import {List, Item} from 'linked-list'
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
 * @returns 返回字符串内容/async: None
 */
export const setClipboard = (content, sync = true) => {
    if (sync) {
        let result = false;
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
        return result;
    } else {
        return clipboard.write(content);
    }
};


/**
 * clipboardy获取系统粘贴板信息
 * @param {boolean} sync 是否同步，If false, Doesn't work in browsers.
 * @returns 返回字符串内容/async:Promise
 */
export const getClipboard = (sync = true) => {
    if (sync) {
        let content = "";
        try {
            content = clipboard.readSync();
            console.log("getClipboard:粘贴板读取:" + content);
        } catch (error) {
            console.log("getClipboard failed:" + error);
        }
        return content;
    } else {
        return clipboard.read();
    }
}


/**
 * 文件IO操作
 */

/**
 * 判断文件类型 文件：1 文件夹：2 链接文件：3
 * @param {String} filepath 
 * @param {boolean} sync 
 * @returns Number / Promise
 */
export const fileType = (filepath, sync = true) => {
    let fn = getExecFunction().name;
    try {
        if (sync) {
            let t = 0;
            let fstat = fs.lstatSync(filepath);
            if (fstat.isSymbolicLink()) {
                console.log(filepath + "是链接文件");
                t = 3;
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
            return t;
        } else {
            return fsPromise.lstat(filepath).then((fstat) => {
                console.log(typeof fstat);
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
    } catch (error) {
        console.log("fileType(): " + error);
    }
}


export const ls = (filepath, sync = true) => {
    if (sync) {
        try {
            let files = new List();
            files = fs.readdirSync(filepath);
            files.forEach(el => {
                if (el[0] === "."){
                    files.detach(el);
                }
            });
            console.log(files);
        } catch (error) {
            console.log("ls:"+error);
        }
    } else {
        console.log();
    }
}

ls(".");

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


