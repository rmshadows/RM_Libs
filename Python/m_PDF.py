#!/usr/bin/python3
"""
处理、合成PDF的模块
"""
from PyPDF2 import PdfFileMerger
import sys

import m_System


def mergePdfs(direc, output):
    """
    合并PDF（带目录）
    Args:
        direc: 文件夹
        output: 保存路径

    Returns:
        None
    """
    # 为了避免RecursionError: maximum recursion depth exceeded while calling a Python object > 1000
    sys.setrecursionlimit(1200)
    merger = PdfFileMerger()
    pdf_files = m_System.getFile(direc, "pdf")
    pdf_files.sort()
    for pdf in pdf_files:
        merger.append(pdf)
    merger.write(output)
    merger.close()
    

if __name__ == '__main__':
    print("Hello World")










