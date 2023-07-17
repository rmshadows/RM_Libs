/**
 * 系统操作模块，默认同步直接返回值，异步返回Promise
 */
"use strict";
import clipboard from 'clipboardy';
import fs from 'fs';
import * as L from 'list'
import path from 'path';
import chalk from 'chalk';
import fse from 'fs-extra';
import child_process from 'child_process';
import { time } from 'console';
import { kill } from 'process';

var fsPromise = fs.promises;

/**
 * 模块内部函数
export const cp = (src, dst, sync = true, recursive = false, overwrite = true) => {
    let fn = getExecFunction().name;
    if(sync){
        try {
        
        } catch (error) {
            console.log("%s: " + error, fn);
        }
    }
}
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


// 切勿将未经处理的用户输入传递给此函数。 任何包含 shell 的输入 元字符可用于触发任意命令执行
export const execCommand = (cmd, easymode = true, sync = true, pwd = ".", callback = (error, stdout, stderr) => {
    if (error) {
        console.error(`exec error: ${error}`);
        return;
    }
    console.log(`stdout: \n${stdout}`);
    console.log(`stderr: \n${stderr}`);
},
    shell = "/bin/bash", timeout = undefined,
    killSignal = 'SIGTERM', maxBuffer = 1024 * 1024,
    encoding = 'utf-8', windowsHide = false) => {
    // encoding = 'buffer'
    let fn = getExecFunction().name;
    if (sync) {
        try {
            if (easymode) {
                return child_process.execSync(cmd, {
                    cwd: pwd, shell: shell,
                    timeout: timeout, killSignal: killSignal,
                    maxBuffer: maxBuffer, encoding: encoding, windowsHide: windowsHide
                });
            } else {
                // https://juejin.cn/s/node%E6%89%A7%E8%A1%8Cshell%E8%8E%B7%E5%8F%96%E5%AE%9E%E6%97%B6%E8%BE%93%E5%87%BA
                // TODO
                const { spawn } = require('child_process');
                const ls = spawn('ls', ['-lh', '/usr']);

                ls.stdout.on('data', (data) => {
                    console.log(`stdout: ${data}`);
                });

                ls.stderr.on('data', (data) => {
                    console.error(`stderr: ${data}`);
                });

                ls.on('close', (code) => {
                    console.log(`child process exited with code ${code}`);
                });
            }
        } catch (error) {
            console.log("%s: " + error, fn);
        }
    } else {
        if (easymode) {
            child_process.exec(cmd, callback);
        } else {
            // https://juejin.cn/s/node%E6%89%A7%E8%A1%8Cshell%E8%8E%B7%E5%8F%96%E5%AE%9E%E6%97%B6%E8%BE%93%E5%87%BA
            // TODO
            const { spawn } = require('child_process');
            const ls = spawn('ls', ['-lh', '/usr']);

            ls.stdout.on('data', (data) => {
                console.log(`stdout: ${data}`);
            });

            ls.stderr.on('data', (data) => {
                console.error(`stderr: ${data}`);
            });

            ls.on('close', (code) => {
                console.log(`child process exited with code ${code}`);
            });

            // https://segmentfault.com/q/1010000023332928
            // https://www.qiniu.com/qfans/qnso-23516740#comments
            //https://devboke.com/back/55
            const { spawn } = require('child_process');
            const lss = spawn('ls', ['-lh', '/usr']);

            ls.stdout.on('data', (data) => {
                console.log(`stdout: ${data}`);
            });

            ls.stderr.on('data', (data) => {
                console.error(`stderr: ${data}`);
            });

            ls.on('close', (code) => {
                console.log(`child process exited with code ${code}`);
            });
        }
    }
}

console.log(execCommand("echo $SHELL", true, false));


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
 * 返回给定扩展名的文件
 * @param {string} filepath 某个目录
 * @param {string} ext 扩展名
 * @returns 
 */
export const lsFileExtfilter = (filepath, ext) => {
    let fn = getExecFunction().name;
    if (fileType(filepath) != 2) {
        prompte("%s: 不是目录！", fn)
        return [];
    }
    try {
        let la_r = la(filepath);
        // prlst(la_r)
        let r_list = L.empty();
        la_r.forEach(el => {
            if (fileType(el) == 1 && path.extname(el) == "." + ext) {
                r_list = L.append(el, r_list);
            }
        });
        prlst(L.toArray(r_list))
        return L.toArray(r_list);
    } catch (error) {
        console.log("%s: " + error, fn);
    }
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
export const treeSync = (filepath, showHidden = false, followLinks = false) => {
    tree(filepath, showHidden, followLinks)
}


/**
 * 新建文件夹 注意Overwrite！会覆盖的！
 * @param {string} dir 路径 
 * @param {boolean} sync 是否同步
 * @param {boolean} overwrite 是否覆盖 会清空原有的
 * @param {boolean} recursive (mkdir -p ?)是否递归
 * @param {string} mode 文件权限默认"0700"
 * @returns null
 */
export const mkdir = (dir, sync = true, overwrite = false, recursive = false, mode = "0700") => {
    let fn = getExecFunction().name;
    if (fs.existsSync(dir)) {
        console.log("%s: 存在同名文件或者文件夹", fn);
        if (overwrite) {
            FD(dir);
        } else {
            return;
        }
    }
    if (sync) {
        try {
            fs.mkdirSync(dir, { recursive: recursive, mode: mode });
        } catch (error) {
            console.log("%s: " + error, fn);
        }
    } else {
        // 同意替换
        // fs.mkdir(dir, { recursive: recursive, mode: mode }, (err, data) => defaultCallback(err, data, name));
        // fsPromise.mkdir(dir, { recursive: recursive, mode: mode }).then(() => {
        //     callback[0]([fn]);
        // }).catch(() => {
        //     callback[1]([fn]);
        // });
        fsPromise.mkdir(dir, { recursive: recursive, mode: mode });
    }
}

/**
 * mkdir -p 注意Overwrite！会覆盖的！
 * @param {*} path 
 * @param {*} sync 
 * @param {*} overwrite 注意Overwrite！会覆盖的！
 * @param {*} mode 
 */
export const mkdirs = (path, sync = true, overwrite = false, mode = "0700") => {
    mkdir(path, sync, overwrite, true, mode);
}

/**
 * 删除文件
 * @param {string} filepath 
 * @param {boolean} sync 是否同步
 * @param {boolean} recursive 是否递归
 * @returns 
 */
export const rm = async (filepath, sync = true, recursive = false) => {
    let fn = getExecFunction().name;
    try {
        if (sync) {
            try {
                fs.rmSync(filepath, { recursive: recursive });
            } catch (error) {
                console.log(fn + ": " + error);
            }
        } else {
            return fsPromise.rm(filepath, { recursive: recursive });
        }
    } catch (error) {
        console.log(error);
    }
}
/**
 * 清空目录
 * @param {*} dir 目录
 * @param {*} sync 是否同步
 * @returns 
 */
export const rmClearDirectory = async (dir, sync = true) => {
    let fn = getExecFunction().name;
    // 判断是不是目录
    if (fileType(dir) != 2) {
        prompte(fn + ": " + dir + "不是目录！")
        return;
    }
    try {
        if (sync) {
            try {
                // 列出文件
                let dfiles = la(dir);
                dfiles.forEach(el => {
                    rm(el, sync, true);
                });
            } catch (error) {
                console.log(fn + ": " + error);
            }
        } else {
            la(dir, false).then((files) => {
                files.forEach(el => {
                    rm(el, sync, true);
                });
            });
        }
    } catch (error) {
        console.log(error);
    }
}
/**
 * 删除文件或者文件夹
 * @param {*} filepath 路径
 * @param {*} sync 是否同步
 * @returns 
 */
export const rmFD = async (filepath, sync = true) => {
    let fn = getExecFunction().name;
    if (sync) {
        try {
            return rm(filepath, sync, true);
        } catch (error) {
            console.log(fn + ": " + error);
            return undefined;
        }
    } else {
        rm(filepath, sync, true);
    }
}


/**
 * 复制文件
 * 如果recursive是false，除了src dst，其他Option选项无效
 * @param {string} src 
 * @param {string} dst 
 * @param {boolean} sync 是否同步
 * @param {boolean} recursive 是则可以复制目录
 * @param {boolean} overwrite 覆盖现有文件或目录，默认为 true。 请注意，如果将其设置为，复制操作将默默失败 false并且目的地存在。 使用 errorOnExist更改此行为的选项。 
 * @param {boolean} errorOnExist  什么时候 overwrite是 false并且目的地存在，抛出错误。 默认为 false. 
 * @param {boolean} dereference 取消引用符号链接，默认为 false. 
 * @param {boolean} preserveTimestamps 为 true 时，会将上次修改和访问时间设置为原始源文件的时间。 当为 false 时，时间戳行为取决于操作系统。 默认为 false. 
 * @param {Function} filter 过滤复制文件/目录的功能。 返回 true要复制该项目， false忽略它。 
 */
export const cp = (src, dst, sync = true, recursive = false,
    overwrite = true, errorOnExist = false,
    dereference = false, preserveTimestamps = false,
    filter = (src, dest) => {
        return true;
    }) => {
    let fn = getExecFunction().name;
    let result = false;
    if (sync) {
        try {
            if (recursive) {
                // 请注意，如果 src是一个目录，它将复制该目录内的所有内容，而不是整个目录本身
                //（请参阅 问题＃537 https://github.com/jprichardson/node-fs-extra/issues/537 ）。 
                fse.copySync(src, dst, {
                    overwrite: overwrite,
                    errorOnExist: errorOnExist,
                    dereference: dereference,
                    preserveTimestamps: preserveTimestamps,
                    filter: filter
                });
                result = true;
            } else {
                fs.copyFileSync(src, dst);
                result = true;
            }
        } catch (error) {
            console.log("%s: " + error, fn);
            result = false;
        }
        return result;
    } else {
        if (recursive) {
            // 请注意，如果 src是一个目录，它将复制该目录内的所有内容，而不是整个目录本身
            //（请参阅 问题＃537 https://github.com/jprichardson/node-fs-extra/issues/537 ）。 
            fse.copySync(src, dst, {
                overwrite: overwrite,
                errorOnExist: errorOnExist,
                dereference: dereference,
                preserveTimestamps: preserveTimestamps,
                filter: filter
            });
            fse.copy()
        } else {
            fs.copyFile(src, dst);
        }
    }
}


/**
 * 复制文件、文件夹
 * @param {string} src 
 * @param {string} dst 
 * @param {boolean} sync 是否同步
 * @param {boolean} overwrite 覆盖现有文件或目录，默认为 true。 请注意，如果将其设置为，复制操作将默默失败 false并且目的地存在。 使用 errorOnExist更改此行为的选项。 
 * @param {boolean} errorOnExist  什么时候 overwrite是 false并且目的地存在，抛出错误。 默认为 false. 
 * @param {boolean} dereference 取消引用符号链接，默认为 false. 
 * @param {boolean} preserveTimestamps 为 true 时，会将上次修改和访问时间设置为原始源文件的时间。 当为 false 时，时间戳行为取决于操作系统。 默认为 false. 
 * @param {Function} filter 过滤复制文件/目录的功能。 返回 true要复制该项目， false忽略它。 
 */
export const cpFD = (src, dst, sync = true,
    overwrite = true, errorOnExist = false,
    dereference = false, preserveTimestamps = false,
    filter = (s, d) => {
        return true;
    }) => {
    return cp(src, dst,
        sync, true,
        overwrite, errorOnExist,
        dereference, preserveTimestamps,
        filter)
}


/**
 * 移动文件（重命名）
 * @param {string} src 源
 * @param {string} dst 目的
 * @param {boolean} sync 是否同步
 * @returns 
 */
export const mv = (src, dst, sync = true) => {
    let fn = getExecFunction().name;
    let result = false;
    if (sync) {
        try {
            fs.renameSync(src, dst);
            result = true;
        } catch (error) {
            console.log("%s: " + error, fn);
            result = false;
        }
        return result;
    } else {
        fsPromise.rename(src, dst);
    }
}


/**
 * 先复制再删除
 * 如果recursive是false，除了src dst，其他Option选项无效
 * @param {string} src 
 * @param {string} dst 
 * @param {boolean} overwrite 覆盖现有文件或目录，默认为 true。 请注意，如果将其设置为，复制操作将默默失败 false并且目的地存在。 使用 errorOnExist更改此行为的选项。 
 * @param {boolean} errorOnExist  什么时候 overwrite是 false并且目的地存在，抛出错误。 默认为 false. 
 * @param {boolean} dereference 取消引用符号链接，默认为 false. 
 * @param {boolean} preserveTimestamps 为 true 时，会将上次修改和访问时间设置为原始源文件的时间。 当为 false 时，时间戳行为取决于操作系统。 默认为 false. 
 * @param {Function} filter 过滤复制文件/目录的功能。 返回 true要复制该项目， false忽略它。 
 */
export const mvCPRM = (src, dst,
    overwrite = true, errorOnExist = false,
    dereference = false, preserveTimestamps = false,
    filter = (s, d) => {
        return true;
    }) => {
    let fn = getExecFunction().name;
    try {
        if (cp(src, dst,
            true, true,
            overwrite, errorOnExist,
            dereference, preserveTimestamps,
            filter)) {
            rmFD(src);
        } else {
            prompte("%s: 复制文件似乎出错了", fn)
        }
    } catch (error) {
        console.log("%s: " + error, fn);
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



