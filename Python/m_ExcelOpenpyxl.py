#!/usr/bin/python3
from openpyxl import Workbook
from openpyxl import load_workbook

"""
文档：https://openpyxl.readthedocs.io/en/stable/tutorial.html#accessing-one-cell

#### 访问单元格
访问单元格：ws['A4'] 或者 ws.cell(row=4, column=2, value=10)
多个单元格：cell_range = ws['A1':'C2']
>>> colC = ws['C']
>>> col_range = ws['C:D']
>>> row10 = ws[10]
>>> row_range = ws[5:10]

### 所有行
Worksheet.rows

"""


def createNewWorkbook(firstSheetName=None):
    """
    新建workbook但不是新建一个实际的文件
    获取第一张自动生成的表wb.active
    Args:
        firstSheetName: 第一Sheet表的名称
    Returns:

    """
    if firstSheetName is None:
        return Workbook()
    else:
        wb = Workbook()
        wb.active.title = firstSheetName
        return wb


def createSheet(wb, sheetName, position=None):
    """
    新增表格
    position = None # insert at the end (default)
     0) # insert at first position
     -1) # insert at the penultimate position
    默认名称：Sheet, Sheet1, Sheet2
    访问：wb["New Title"]
    Args:
        wb:
        sheetName: sheet名
        position: 位置

    Returns:
        返回新增的worksheet
    """
    if position is None:
        return wb.create_sheet(sheetName)
    else:
        return wb.create_sheet(sheetName, position)


def getSheetNames(wb):
    """
    返回Sheets名字
    Args:
        wb:workbook

    Returns:

    """
    return wb.sheetnames


def copyWorksheet(wb, ws):
    """
    复制工作表
    Args:
        wb: 复制到某workbook
        ws: 要被复制的worksheet

    Returns:

    """
    return wb.copy_worksheet(ws)


def readRows(ws, min_row, max_row, min_col, max_col, valueOnly=True):
    """
    读行
    Args:
        ws: worksheet
        min_row: 开始行
        max_row: 结束行
        min_col: 开始列
        max_col: 结束列
        valueOnly: 是否仅返回值
    Returns:
        <Cell Sheet1.A1>
        <Cell Sheet1.A2>
    """
    cs = []
    for row in ws.iter_rows(min_row=min_row, max_row=max_row, min_col=min_col, max_col=max_col, values_only=valueOnly):
        for cell in row:
            print(cell)
            cs.append(cell)
    return cs


def readColumns(ws, min_col, max_col, min_row, max_row, valueOnly=True):
    """
        读列
        Args:
            ws: worksheet
            min_col: 开始列
            max_col: 结束列
            min_row: 开始行
            max_row: 结束行
            valueOnly: 是否仅返回值
        Returns:
            <Cell Sheet1.A1>
            <Cell Sheet1.A2>
        """
    cs = []
    for col in ws.iter_cols(min_row=min_row, max_row=max_row, min_col=min_col, max_col=max_col, values_only=valueOnly):
        for cell in col:
            print(cell)
            cs.append(cell)
    return cs


def getCellRange(ws, cell1, cell2):
    """
    返回一个范围内的单元格
    colC = ws['C']
    col_range = ws['C:D']
    row10 = ws[10]
    row_range = ws[5:10]
    Args:
        ws: worksheet
        ws['A1':'C2']
        cell1: 起始
        cell2: 末尾

    Returns:

    """
    return ws[cell1:cell2]


def getRowOrColumns(ws, name):
    """
    返回行或者列
    colC = ws['C']
    col_range = ws['C:D']
    row10 = ws[10]
    row_range = ws[5:10]
    Args:
        ws: worksheet
        ws['A1':'C2']
        cell1: 起始
        cell2: 末尾

    Returns:

    """
    return ws[name]

def justReadOneRow(ws, row, deep=60, valueOnly=True):
    """
    读1行,50列
    Args:
        ws: worksheet
        row: 行
        valueOnly: 是否仅返回值
    Returns:
        <Cell Sheet1.A1>
        <Cell Sheet1.A2>
    """
    cs = []
    for row in ws.iter_rows(min_row=row, max_row=row, min_col=0, max_col=deep, values_only=valueOnly):
        for cell in row:
            print(cell)
            cs.append(cell)
    return cs


def justReadOneColumn(ws, col, deep=50, valueOnly=True):
    """
        读列
        Args:
            ws: worksheet
            min_col: 开始列
            max_col: 结束列
            min_row: 开始行
            max_row: 结束行
            valueOnly: 是否仅返回值
        Returns:
            <Cell Sheet1.A1>
            <Cell Sheet1.A2>
        """
    cs = []
    if col.isnumeric():
        for col in ws.iter_cols(min_row=0, max_row=deep, min_col=col, max_col=col, values_only=valueOnly):
            for cell in col:
                print(cell)
                cs.append(cell)
    else:
        cols = ws[col]
        print(cols)
        for col in cols:
            for cell in col:
                print(cell)
                cs.append(cell)
    return cs


def loadExcel(filename):
    """
    加载现有的Excel表格
    Args:
        filename:Excel文件名

    Returns:
        wb
    """
    return load_workbook(filename=filename)


if __name__ == '__main__':
    wb = loadExcel("1.xlsx")
    ws = wb[getSheetNames(wb)[0]]
    justReadOneColumn(ws, "B")
    print("Hello World")
