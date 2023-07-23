/**
 * 二维码模块
 * 
<!-- index.html -->
<html>
  <body>
    <canvas id="canvas"></canvas>
    <script src="bundle.js"></script>
  </body>
</html>

// index.js -> bundle.js
var QRCode = require('qrcode')
var canvas = document.getElementById('canvas')

QRCode.toCanvas(canvas, 'sample text', function (error) {
  if (error) console.error(error)
  console.log('success!');
})


 */
import QRCode from 'qrcode'
import QRCodeReader from 'qrcode-reader';
import fs, { read } from 'fs';
import Jimp from 'jimp';
import { log } from 'console';

var reader = new QRCodeReader();


// 二维码生成
/**
 * 内部函数，生成二维码 With async/await
 * @param {*} text 
 * @param {*} width 
 * @param {*} type 
 */
const asyncGenerateQR = async (text, width, type) => {
  try {
    let data = await QRCode.toDataURL(text, { type: type, width: width });
    console.log(data)
  } catch (err) {
    console.error(err)
  }
}

/**
 * 生成二维码图像并保存
 * createQR("123", "1.svg", 1000);
 * @param {*} text 
 * @param {*} savepath 
 * @param {*} width 
 * @param {*} type 文件类型 svg png txt
 */
export const createQRSync = (text, savepath, width = "500", type = "svg") => {
  QRCode.toFile(savepath, text, { type: type, width: width });
}

/**
 * With promises 返回Base64图片
 * @param {*} text 
 * @param {*} width 500
 * @param {*} type image/png
 */
export const generateQRBase64Promises = (text, width = 500, type = "image/png") => {
  QRCode.toDataURL(text, { type: type, width: width })
    .then(url => {
      console.log(url);
      return url;
    })
    .catch(err => {
      console.error(err)
    });
}

/**
 * With async/await 返回Base64图片
 * @param {*} text 
 * @param {*} width 
 * @param {*} type 
 */
export const generateQRBase64Async = async (text, width = 500, type = "image/png") => {
  return await asyncGenerateQR(text, width, type);
}

// 二维码读取

/**
 * 读取二维码
 * @param {*} filepath 
 */
export const readQRCodeFromFile = async (filepath) => {
  let buffer = fs.readFileSync(filepath);
  let r = "";
  Jimp.read(buffer, function (err, image) {
    if (err) {
      console.error(err);
      // TODO handle error
    }
    reader.callback = async function (err, value) {
      if (err) {
        console.error(err);
        // TODO handle error
      }
      // console.log(value.result);
      // console.log(value);
      r = value.result;
    };
    reader.decode(image.bitmap);
    console.log("Return: " + r);
  });
}

readQRCodeFromFile("1.png").then(x => console.log("aaa"), x => console.log("???"));
// let a = readQRCodeFromFile("1.png")
// console.log(a);
