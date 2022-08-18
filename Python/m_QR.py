#!/usr/bin/python3
"""
二维码处理
"""
import qrcode
import zxing

import os.path as op


def readQR(qr_path):
    """
    识别二维码
    Args:
        qr_path: 二维码图片路径

    Returns:
        返回识别结果
    """
    reader = zxing.BarCodeReader()
    barcode = reader.decode(qr_path)
    return bytes.fromhex(barcode.raw)


def generateQR(data, save_file="qr.png", view_only=False):
    """
    生成二维码
    Args:
        data: 内容
        save_file: 保存的文件路径（需要扩展名）
        view_only: False不保存二维码图像，仅预览
    """
    img = qrcode.make(data)
    # type(img)  # qrcode.image.pil.PilImage
    if view_only:
        img.show()
    else:
        img.save(save_file)
        print("Saving to {} (Contain: {}) ....".format(save_file, data))


if __name__ == '__main__':
    generateQR("妳好", view_only=True)










