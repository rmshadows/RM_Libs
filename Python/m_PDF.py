#!/usr/bin/python3
"""
处理、合成PDF的模块
"""
import os
import os.path as op
import random

from PIL import Image
from PyPDF2 import PdfFileMerger
from PyPDF2 import PdfFileReader as pdf_reader, PdfFileWriter as pdf_writer

from pdf2image import convert_from_path

import sys

import m_System


def add_content(pdf_in_name:str,pdf_out_name:str,content_dict:dict):
    """
    添加PDF注释（目录）
    Args:
        pdf_in_name: pdf文件名
        pdf_out_name: 输出pdf文件名
        content_dict: 字典 title:page {"索引":1}
    """
    pdf_in = pdf_reader(pdf_in_name)
    pdf_out = pdf_writer()
    pdf_out.cloneDocumentFromReader(pdf_in)
    for key in content_dict.keys():
        pdf_out.addBookmark(key,int(content_dict[key])-1)
    with open(pdf_out_name, "wb" ) as fout:
        pdf_out.write(fout)
    # print("PDF Marked!")


def mergePdfs(directory, output_pdf_file):
    """
    合并PDF（带目录）
    Args:
        directory: 文件夹
        output_pdf_file: 保存路径

    Returns:
        None
    """
    # 为了避免RecursionError: maximum recursion depth exceeded while calling a Python object > 1000
    sys.setrecursionlimit(1200)
    merger = PdfFileMerger()
    pdf_files = m_System.getFile(directory, "pdf")
    pdf_files.sort()
    for pdf in pdf_files:
        merger.append(pdf)
    merger.write(output_pdf_file)
    merger.close()


def image2pdf(directory, output_pdf_name, content:bool=True):
    """
    将所给文件夹的jpg图片转为PDF文档（提供目录）
    Args:
        directory: 文件夹路径
        output_pdf_name: 导出PDF名称
        content: 是否需要注释
    """
    ## change all png into jpg & delete the .png files
    names = os.listdir(directory)
    content_dict = {}
    for name in names:
        img = Image.open(op.join(directory, name))
        name = name.split(".")
        if name[-1] == "png":
            name[-1] = "jpg"
            name_jpg = str.join(".", name)
            r, g, b, a = img.split()
            img = Image.merge("RGB", (r, g, b))
            to_save_path = op.join(directory, name_jpg)
            img.save(to_save_path)
            os.remove(op.join(directory, "{}.png".format(name[0])))
        else:
            continue
    ## add jpg and jpeg to
    file_list = os.listdir(directory)
    pic_name = []
    im_list = []
    for x in file_list:
        if "jpg" in x or 'jpeg' in x:
            pic_name.append(x)
    # 排序 https://www.likecs.com/show-307952382.html
    pic_name.sort()  # sorted
    # 如果上面这一句无法满足，请取消注释下行(自行修改参数)
    # e.g.: a1 a2: print(sorted(pic_name, key=lambda info: (info[0], int(info[1:]))))
    # sort(key=lambda x: int(x[1]))
    pic_name = sorted(pic_name, key=lambda info: int(info[:-4]))
    # print(pic_name)
    new_pic = []
    n = 1
    for x in pic_name:
        if "jpg" in x:
            new_pic.append(x)
            content_dict[x] = n
            n += 1
    im1 = Image.open(op.join(directory, new_pic[0]))
    new_pic.pop(0)
    for i in new_pic:
        img = Image.open(op.join(directory, i))
        # im_list.append(Image.open(i))
        if img.mode == "RGBA":
            r, g, b, a = img.split()
            img = Image.merge("RGB", (r, g, b))
            img = img.convert('RGB')
            im_list.append(img)
        else:
            im_list.append(img)
    im1.save(output_pdf_name, "PDF", resolution=100.0, save_all=True, append_images=im_list)
    if content:
        # 生成中间文件过渡防意外
        r = "{}.pdf".format(random.randint(9999,999999))
        os.rename(output_pdf_name, r)
        try:
            add_content(r, output_pdf_name, content_dict)
            os.remove(r)
        except Exception as e:
            print(e)


def pdf2images(pdfFile, dpi=200, format='png', toDir="2images"):
    """
    拆分PDF到图片
    Args:
        pdfFile: PDF路径
        dpi: 图像DPI
        format: 图像格式
        toDir: 会在PDF同路径下新建文件夹

    Returns:

    """
    # 父目录绝对路径
    pp = op.dirname(pdfFile)
    image_path = op.join(pp, toDir)
    # 没有就新建
    if not op.exists(image_path):
        os.mkdir(image_path)
    pages = convert_from_path(pdfFile, dpi=dpi, fmt=format)
    for i, page in enumerate(pages):
        # page.save(f'page_{i + 1}.jpeg', format.upper())
        # 文件名+页码
        page.save(op.join(image_path, "{0}-{1}.{2}".format(pdfFile, i, format)))


if __name__ == '__main__':
    # 合并PDF
    image2pdf("images", "output.pdf")
    # 拆分PDF
    # pdf2images("1.pdf", 400, "jpg")










