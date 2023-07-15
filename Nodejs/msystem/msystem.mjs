/**
 * 系统操作模块，默认同步直接返回值，异步返回Promise
 */
"use strict";
import { rejects } from 'assert';
import clipboard from 'clipboardy';
import { log } from 'console';
import { randomFill } from 'crypto';
import { resolve, setDefaultResultOrder } from 'dns';
import fs, { unwatchFile } from 'fs';
import { endianness } from 'os';
import * as L from 'list'
import { debugPort, exit } from 'process';
import path from 'path';
import chalk from 'chalk';
import { addAbortSignal } from 'stream';
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
            // console.log("setClipboard:设置粘贴板:" + content);
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
            // console.log("getClipboard:粘贴板读取:" + content);
        } catch (error) {
            console.log("getClipboard failed:" + error);
        }
        return content;
    } else {
        return clipboard.read();
    }
}


/**
 * Std out
 */

/**
 * 简单的终端颜色 模式 ：成功绿0，失败红1，信息蓝2，信息黄3，信息紫4，信息黑5，信息白6
 * fg, bg:
    前景            背景              颜色
    ---------------------------------------
    30                40              黑色
    31                41              紅色
    32                42              綠色
    33                43              黃色
    34                44              藍色
    35                45              紫紅色
    36                46              青藍色
    37                47              白色
 * 
 * display:
    0                终端默认设置
    1                高亮显示
    4                使用下划线
    5                italic 闪烁(暂不支持闪烁，用italic代替)
    7                反显
    8                不可见
 * @param {*} str 字符串
 * @param {*} fg 前景色
 * @param {*} bg 背景色
 * @param {*} display 显示模式
 */
export const print = (str, fg = 31, bg = 0, display = 1) => {
    var logd = chalk;
    if (display == 0) {
        logd = logd.reset;
    } else if (display == 1) {
        logd = logd.bold;
    } else if (display == 4) {
        logd = logd.underline;
    } else if (display == 5) {
        // 暂不支持闪烁
        logd = logd.italic;
    } else if (display == 7) {
        logd = logd.inverse;
    } else if (display == 8) {
        logd = logd.hidden;
    }
    if (fg == 30) {
        logd = logd.blackBright;
    } else if (fg == 31) {
        logd = logd.redBright;
    } else if (fg == 32) {
        logd = logd.greenBright;
    } else if (fg == 33) {
        logd = logd.yellowBright;
    } else if (fg == 34) {
        logd = logd.blueBright;
    } else if (fg == 35) {
        logd = logd.magentaBright;
    } else if (fg == 36) {
        logd = logd.cyanBright;
    } else if (fg == 37) {
        logd = logd.whiteBright;
    }

    if (bg == 40) {
        logd = logd.bgBlackBright;
    } else if (bg == 41) {
        logd = logd.bgRedBright;
    } else if (bg == 42) {
        logd = logd.bgGreenBright;
    } else if (bg == 43) {
        logd = logd.bgYellowBright;
    } else if (bg == 44) {
        logd = logd.bgBlueBright;
    } else if (bg == 45) {
        logd = logd.bgMagentaBright;
    } else if (bg == 46) {
        logd = logd.bgCyanBright;
    } else if (bg == 47) {
        logd = logd.bgWhiteBright;
    }
    console.log(logd(str));
}

/**
 * 常用显示
0:成功 绿 success s
1:失败 红 error e
2:信息 蓝 info i
3:警告 黄 warn w
4:白 message  m
 * @param {*} str 
 * @param {*} mode 
 */
export const prompt = (str, mode = 4) => {
    if (mode == 0 || mode == "s") {
        print(str, 32);
    } else if (mode == 1 || mode == "e") {
        print(str, 31);
    } else if (mode == 2 || mode == "i") {
        print(str, 34);
    } else if (mode == 3 || mode == "w") {
        print(str, 33);
    } else if (mode == 4 || mode == "m") {
        print(str, 37);
    }
}


export const prompts = (str) => {
    prompt(str, "s");
}
export const prompte = (str) => {
    prompt(str, "e");
}
export const prompti = (str) => {
    prompt(str, "i");
}
export const promptw = (str) => {
    prompt(str, "w");
}
export const promptm = (str) => {
    prompt(str, "m");
}

/**
 * 打印列表
 * @param {Array} list 
 * @param {*} promptMode 
 */
export const prlst = (list, promptMode = 0) => {
    list.forEach((x) => {
        prompt(x, promptMode);
    });
}

/**
 * 返回环境变量分隔符
 * @returns 
 */
export const getPathSeparator = () => {
    return path.delimiter;
}

/**
 * 获取环境变量
 * @returns 
 */
export const getPath = () => {
    let p = L.empty();
    process.env.PATH.split(path.delimiter).forEach(function (dir) {
        p = L.append(dir, p);
    });
    return L.toArray(p);
}


/**
 * 获取文件分隔符
 * @returns 
 */
export const getFileSeparator = () => {
    return path.sep;
}

/**
 * 是否是Windows系统
 * @returns 
 */
export const isWindows = () => {
    return getFileSeparator() == "\\";
}


/**
 * 去除重复数组元素
 * @param {*} arr 
 * @returns 
 */
export const arrayRemoveDuplicates = (arr) => {
    return arr.filter(function (elem, index, self) {
        return index === self.indexOf(elem);
    });
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
                // console.log(filepath + "是链接文件");
                t = 3;
            } else if (fstat.isFile()) {
                // console.log(filepath + "是文件");
                return 1;
            } else if (fstat.isDirectory()) {
                // console.log(filepath + "是文件夹");
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
                    // console.log(filepath + "是链接文件");
                    return 3;
                } else if (fstat.isFile()) {
                    // console.log(filepath + "是文件");
                    return 1;
                } else if (fstat.isDirectory()) {
                    // console.log(filepath + "是文件夹");
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

/**
 * ls
 * @param {string} filepath 路径
 * @param {boolean} sync 是否同步 默认true
 * @param {boolean} showHidden 是否显示隐藏文件 默认否
 * @param {boolean} followLinks 是否跟随链接文件，默认否
 * @param {boolean} absolutePath 是否返回绝对路径，默认是
 * @returns 列表
 */
export const ls = (filepath, sync = true, showHidden = false, followLinks = true, absolutePath = true) => {
    filepath = path.resolve(filepath);
    if (sync) {
        try {
            // 如果是文件
            if (fileType(filepath) == 1) {
                return [filepath];
            }
            // 如果是链接文件 且不跟随 followLinks == false
            if (fileType(filepath) == 3 && !followLinks) {
                // console.log("所给的路径是链接。");
                return [filepath];
            } else if (fileType(filepath) == 3 && followLinks) {
                let tr = filepath = fileLinkedto(filepath)
                // 如果链接可用
                if (tr[1]) {
                    filepath = tr[0]
                } else {
                    return [filepath]
                }
            }
            // 接下来是目录处理
            let files = L.from(fs.readdirSync(filepath));
            // 如果不显示隐藏文件
            if (!showHidden) {
                files = L.filter((el) => {
                    if (el.substring(0, 1) == ".") {
                        return false;
                    } else {
                        return true;
                    }
                }, files);
            };
            // 返回绝对路径
            if (absolutePath) {
                let abFiles = L.empty();
                L.forEach((item) => {
                    item = path.join(filepath, item);
                    // 如果是链接, 且跟随链接
                    if (fileType(item) == 3 && followLinks) {
                        let lf;
                        try {
                            lf = fs.realpathSync(item);
                        } catch (error) {
                            console.log("ls: " + item + " 可能是损坏的链接文件");
                            lf = item;
                        }
                        abFiles = L.append(lf, abFiles);
                    } else {
                        abFiles = L.append(item, abFiles);
                    }
                }, files);
                return L.toArray(abFiles);
            } else {
                return L.toArray(files);
            }
        } catch (error) {
            console.log("ls:" + error);
        }
    } else {
        // 如果是文件
        if (fileType(filepath) == 1) {
            return [filepath];
        }
        // 如果是链接文件 且不跟随 followLinks == false
        if (fileType(filepath) == 3 && !followLinks) {
            // console.log("所给的路径是链接。");
            return [filepath];
        } else if (fileType(filepath) == 3 && followLinks) {
            try {
                filepath = fs.realpathSync(filepath);
            } catch (error) {
                console.log("ls: " + filepath + " 可能是损坏的链接文件");
                return [filepath];
            }
        }
        return fsPromise.readdir(filepath).then(x => {
            let files = L.from(x);
            if (!showHidden) {
                files = L.filter((el) => {
                    if (el.substring(0, 1) == ".") {
                        return false;
                    } else {
                        return true;
                    }
                }, files);
            };
            // 返回绝对路径
            if (absolutePath) {
                let abFiles = L.empty();
                L.forEach((item) => {
                    item = path.join(filepath, item);
                    // 如果是链接, 且跟随链接
                    if (fileType(item) == 3 && followLinks) {
                        let lf;
                        try {
                            lf = fs.realpathSync(item);
                        } catch (error) {
                            console.log("ls: " + item + " 可能是损坏的链接文件");
                            lf = item;
                        }
                        abFiles = L.append(lf, abFiles);
                    } else {
                        abFiles = L.append(item, abFiles);
                    }
                }, files);
                return L.toArray(abFiles);
            } else {
                return L.toArray(files);
            }
        });
    }
}


/**
 * 模拟la
 * @param {string} filepath  
 * @param {boolean} sync 
 * @param {boolean} followLinks 
 * @param {boolean} absolutePath 
 * @returns 
 */
export const la = (filepath, sync = true, followLinks = false, absolutePath = true) => {
    return ls(filepath, sync, true, followLinks, absolutePath);
}


/**
 * 返回链接文件指向
 * @param {*} filepath 链接文件
 * @returns [链接地址， 链接好坏]
 */
export const fileLinkedto = (filepath) => {
    let flt = "";
    let flc = false;
    if (fileType(filepath) == 3) {
        try {
            fs.realpathSync(filepath);
            flc = true;
        } catch (error) {
            console.log("fileLinkedto: " + filepath + " 可能是损坏的链接文件");
            flc = false;
        }
        try {
            flt = fs.readlinkSync(filepath);
        } catch (error) {
            console.log("fileLinkedto: " + filepath + error);
        }
    }
    return [flt, flc];
}

/**
 * tree 返回目录下所有文件，包括空文件夹(包含当前文件夹) 仅有同步方法
 * @param {*} filepath 
 * @param {*} showHidden 
 * @param {*} followLinks  如果给定root是链接，则follow links
 * @returns 
 */
export const tree = (filepath, showHidden = false, followLinks = false) => {
    filepath = path.resolve(filepath);
    let tfs = L.empty();
    // 添加当前路径
    tfs = L.list(filepath);
    // 如果root是链接文件 直接跟随
    if (fileType(filepath) == 3) {
        // 获取链接地址
        let tr = fileLinkedto(filepath)
        if (tr[1]) {
            // 如果链接可用 则跟随到实际指向
            filepath = tr[0]
        } else {
            // 链接不可用，直接返回
            return [filepath]
        }
    }
    // 如果root是文件
    if (fileType(filepath) == 1) {
        return [filepath];
    }
    // root是空目录当作文件处理
    if (la(filepath).length == 0) {
        return [filepath];
    }
    // 其他情况(目录) ! 除非跟随链接, 目录中的链接不可以再使用tree
    try {
        // 获取目录下的子文件
        let root_dir = ls(filepath, true, showHidden, followLinks);
        root_dir.forEach(item => {
            let dup = false;
            // 检查有无重复
            L.toArray(tfs).forEach(el => {
                el = String(el);
                // 先检查源文件是否重复
                if (String(item) == el) {
                    prompt("tree: 重复项目(源文件): " + item, "e")
                    dup = true;
                }
                // 如果跟随链接，就检查链接实际指向有无重复
                // 如果是链接文件(前面检查没有重复de)
                if (fileType(item) == 3 && followLinks) {
                    if (String(fileLinkedto(item)[0]) == el) {
                        prompt("tree: 重复项目(链接文件 " + item + " 指向): " + fileLinkedto(item)[0], "e")
                        dup = true;
                    }
                }
            });
            // 文件没重复
            if (!dup) {
                // 如果是链接，且不跟随(因为直接使用tree读取链接默认第一级跟随),直接返回
                if (fileType(item) == 3 && !followLinks) {
                    tfs = L.append(item, tfs);
                } else {
                    // 不是链接
                    let rtfs = L.from(tree(item, showHidden, followLinks));
                    tfs = L.concat(rtfs, tfs);
                }
            }
        });
        // 过滤重复 
        return arrayRemoveDuplicates(L.toArray(tfs));
    } catch (error) {
        console.log(error);
    }
}


tree("/home/ryan")


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


