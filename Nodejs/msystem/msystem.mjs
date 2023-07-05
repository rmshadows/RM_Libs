import clipboard from 'clipboardy';
import { log } from 'console';
import fs from 'fs';


/**
 * clipboardy粘贴板操作
 */
/**
 * clipboardy设置粘贴板
 * @param {*} content content 内容
 * @param {*} sync 是否同步，Doesn't work in browsers.
 * @returns sync:返回字符串内容 sync false:返回Promise
 */
export const setClipboard = (content, sync = true) => {
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
        try {
            clipboard.write(content);
            if (clipboard.read() === content) {
                result = true;
            } else {
                console.log("setClipboard: 似乎失败了");
            }
            console.log("setClipboard:设置粘贴板:" + content);
        } catch (error) {
            console.log("setClipboard failed:" + error);
        }
    }
    return result;
};

/**
 * clipboardy获取系统粘贴板信息
 * @param {*} sync 是否同步，Doesn't work in browsers.
 * @returns sync:返回字符串内容 sync false:返回Promise
 */
export const getClipboard = (sync = true) => {
    let content = "";
    if (sync) {
        try {
            content = clipboard.readSync();
            console.log("getClipboard:粘贴板读取:" + content);
        } catch (error) {
            console.log("getClipboard failed:" + error);
        }
    } else {
        try {
            content = clipboard.read();
            console.log("getClipboard:粘贴板读取:" + content);
        } catch (error) {
            console.log("getClipboard failed:" + error);
        }
    }
    return content;
}

/**
 * 文件IO操作
 */
// TODO


/**
 * 新建文件夹
 * @param {*} dir 路径
 * @param {*} sync 是否同步
 * @param {*} overwrite 是否覆盖
 * @param {*} recursive (mkdir -p ?)是否递归
 * @param {*} mode 文件权限默认"0700"
 * @returns null
 */
export const mkdir = (dir, sync=true, overwrite = false,recursive = false, mode="0700") => {
    try {
        if (fs.existsSync(dir)) {
            console.log("mkdir:存在同名文件或者文件夹");   
            if (overwrite){
                // TODO：删除文件或者文件夹
            }else{
                return;
            }
        }
        if(sync){
            fs.mkdirSync(dir, {recursive: recursive, mode: mode});
        }else{
            fs.mkdir(dir, {recursive: recursive, mode: mode}, (err, data)=>{
                if (err) {
                    console.log("mkdir:" + err);
                }
            });
        }
    } catch (error) {
        console.log("mkdir:" + error);
    }
}




/**
 * 读取文件
 * @param {*} filepath 文件路径
 * @param {*} readBinary 是否返回二进制内容（否则返回文本） 
 * @param {*} sync 是否同步
 * @returns 内容
 */
export const readFileContent = (filepath, readBinary = false, sync = true) => {
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

    try {
        fs.rename(oldname, newname);
    } catch (error) {
        console.log("readFileContent:" + error);
    }
}


