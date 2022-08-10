#!/usr/bin/python3
"""
用于处理Python脚本的配置文件的模块
"""
import json


def write_JSON(json_save_path, json_body):
    """
    保存JSON
    Args:
        json_save_path: 保存位置
        json_body: JSON类
    """
    with open(json_save_path, "w", encoding="utf-8") as f:
        json.dump(json_body, f, ensure_ascii=False, indent=4)
        
        
def load_JSON(json_path):
    """
    从文本文档加载JSON
    Args:
        json_path: JSON文本文件路径

    Returns:
        返回JSON类
    """
    with open(json_path, 'r', encoding="utf-8") as f:
        json_dict = json.load(f)
    return json_dict


if __name__ == '__main__':
    


