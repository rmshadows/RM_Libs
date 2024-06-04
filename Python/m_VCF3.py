import base64
import quopri

import vobject
import csv

from vobject.vcard import Address

# pip install vobject
# 获取字段名称
def getFieldnames(vcards):
    fieldnames = set()
    for vcard in vcards:
        for key in vcard.contents.keys():
            fieldnames.add(key)
    print("所有字段名:", fieldnames)
    return fieldnames

# 获取字段名+属性名称
def getFieldnamesAndParams(vcards):
    fieldnames = set()
    for vcard in vcards:
        for key, items in vcard.contents.items():
            for item in items:
                fieldnames.add(key)
                for param_key, param_values in item.params.items():
                    fieldnames.add(f"{key}.{param_key}")
    sorted_fieldnames = sorted(fieldnames)
    print("所有字段名和属性:", sorted_fieldnames)
    return sorted_fieldnames


def readVCF(vcf_file, validate=False, ignoreUnreadable=False, verbose=False):
    """
    读取 VCF 文件并收集所有唯一的字段名
    Args:
        vcf_file: VCF 文件路径
        validate: 控制是否对 vCard 内容进行验证。
        ignoreUnreadable: 控制是否忽略无法读取的部分。

    Returns:
        vcards: 读取的 vCard 对象列表
        fieldnames: 所有唯一的字段名集合
    """
    vcards = []
    with open(vcf_file, 'r') as f:
        for vcard in vobject.base.readComponents(f, validate=validate, ignoreUnreadable=ignoreUnreadable):
            vcards.append(vcard)
            if verbose:
                vcard.prettyPrint()
    print("一共读取 {} 条信息".format(len(vcards)))
    return vcards

def process_address(adr):
    properties = [adr.street, adr.city, adr.region, adr.code, adr.country, adr.box, adr.extended]
    return "⭐".join([prop if prop else "None" for prop in properties])


def process_name(n):
    properties = [n.family, n.given, n.additional, n.prefix, n.suffix]
    return "☀".join([prop if prop else "None" for prop in properties])


def process_list(lst):
    return "☀".join([prop if prop else "None" for prop in lst])


def process_bytes(data):
    base64_encoded_data = base64.b64encode(data)
    return base64_encoded_data.decode('utf-8')


def vcards_to_csv(vcards, csv_file):
    """
    将 VCF 转换为 CSV 文件
    Args:
        vcards: vCard 对象列表
        csv_file: 目标 CSV 文件路径
    """
    fieldnames = getFieldnamesAndParams(vcards)

    with open(csv_file, 'w', newline='', encoding='utf-8') as csvfile:
        writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
        writer.writeheader()

        for vcard in vcards:
            row = {}
            for field in fieldnames:
                if "." in field:
                    p1, p2 = field.split(".")
                    values_with_params = []
                    if p1 in vcard.contents:
                        for item in vcard.contents[p1]:
                            ps = item.params
                            if p2 in ps:
                                tl = [psp if psp else "None" for psp in ps[p2]]
                                tr = "⭐".join(tl) if len(tl) > 1 else tl[0]
                                values_with_params.append(tr)
                            else:
                                values_with_params.append("None")
                        row[field] = "☀".join(values_with_params)
                    else:
                        row[field] = "None"
                else:
                    values_with_params = []
                    if field in vcard.contents:
                        for item in vcard.contents[field]:
                            if isinstance(item.value, vobject.vcard.Address):
                                values_with_params.append(process_address(item.value))
                                row[field] = "☀".join(values_with_params)
                            elif isinstance(item.value, vobject.vcard.Name):
                                row[field] = process_name(item.value)
                            elif isinstance(item.value, list):
                                row[field] = process_list(item.value)
                            elif isinstance(item.value, bytes):
                                # 这个是针对v3的二进制照片
                                row[field] = process_bytes(item.value)
                            else:
                                values_with_params.append(item.value)
                                row[field] = "☀".join(values_with_params)
                    else:
                        row[field] = "None"
            writer.writerow(row)


def csv_to_vcards(csv_file, delimiter=','):
    """
    从 CSV 文件读取数据并转换为 vCard 对象列表。

    Args:
        delimiter: 分隔符
        csv_file: CSV 文件的路径。

    Returns:
        vCards: vCard 对象列表。
    """
    vcards = []
    # 打开 CSV 文件进行读取
    with open(csv_file, newline='', encoding='utf-8') as csvfile:
        reader = csv.DictReader(csvfile, delimiter=delimiter)
        # 遍历 CSV 文件的每一行
        for row in reader:
            vcard = vobject.vCard()  # 创建一个新的 vCard 对象
            # 遍历每个字段和值
            for key, value in row.items():
                # 如果值为 "None"，跳过
                if value == "" or value == "None":
                    continue
                # 检查字段是否包含参数（例如，adr.TYPE）
                if '.' in key:
                    main_field, param = key.split('.')
                    items = value.split('☀')
                    # 确保 vCard 对象中有足够的字段实例
                    # 使用 enumerate 来获取每个元素的索引 idx 和对应的值 item。
                    # 如果 items 是 ['HOME', 'WORK', 'OTHER']，那么在每次循环中，idx 会分别是 0, 1, 2，
                    # 而 item 会分别是 'HOME', 'WORK', 'OTHER'。
                    for idx, item in enumerate(items):
                        # 这行代码检查 vcard.contents 中名为 main_field 的字段的数量是否足够。
                        # vcard.contents.get(main_field, []) 尝试获取 vcard.contents 中名为 main_field 的字段列表。
                        # 如果不存在这样的字段，则返回一个空列表。
                        # len(vcard.contents.get(main_field, [])) 返回这个列表的长度，即当前已有的字段实例数量。
                        while len(vcard.contents.get(main_field, [])) <= idx:
                            vcard.add(main_field)
                        # 将参数值添加到字段中
                        if item == "None":
                            pass
                        else:
                            vcard.contents[main_field][idx].params[param] = item.split('⭐')
                else:
                    # 跳过空的
                    if value == "" or value == "None":
                        continue
                    # 处理主要字段
                    if key == 'adr':
                        # 分割地址字段的各个部分
                        values = value.split('☀')
                        for v in range(0, len(values)):
                            if values[v] == "None":
                                values[v] = ""
                        for adr in values:
                            adr_param = adr.split('⭐')
                            for v in range(0, len(adr_param)):
                                if adr_param[v] == "None":
                                    adr_param[v] = ""
                        adr = Address(
                            street=adr_param[0], city=adr_param[1], region=adr_param[2],
                            code=adr_param[3], country=adr_param[4], box=adr_param[5],
                            extended=adr_param[6]
                        )
                        vcard.add(key).value = adr
                    elif key == 'n':
                        # 分割姓名字段的各个部分
                        values = value.split('☀')
                        for v in range(0, len(values)):
                            if values[v] == "None":
                                values[v] = ""
                        n = vobject.vcard.Name(
                            family=values[0], given=values[1], additional=values[2],
                            prefix=values[3], suffix=values[4]
                        )
                        vcard.add(key).value = n
                    elif key == 'org':
                        # 分割组织字段的各个部分
                        vcard.add(key).value = value.split('☀')
                    elif key == 'photo':
                        # 处理照片字段，将 base64 字符串解码为字节
                        vcard.add(key).value = base64.b64decode(value.encode('utf-8'))
                    elif key == 'email':
                        # 处理email
                        items = value.split('☀')
                        for item in items:
                            vcard.add(key).value = item
                    else:
                        # 处理其他字段
                        items = value.split('☀')
                        for item in items:
                            vcard.add(key).value = item
            vcards.append(vcard)  # 将 vCard 对象添加到列表中
    return vcards


def vcards_to_vcf(vcards, vcf_file, Escaping=False):
    """
    将 vCard 对象列表导出为 VCF 文件。

    Args:
        Escaping: 转义字符(True就会转义)
        vcards: vCard 对象列表。
        vcf_file: 目标 VCF 文件路径。
    """
    with open(vcf_file, 'w', encoding='utf-8') as file:
        for vcard in vcards:
            # 替换被转义的分号
            serialized_vcard = vcard.serialize()
            print(serialized_vcard)
            if not Escaping:
                serialized_vcard = serialized_vcard.replace(r'\;', ';')
                serialized_vcard = serialized_vcard.replace('\r\n ', '')
                serialized_vcard = serialized_vcard.replace('\n ', '')
                serialized_vcard = serialized_vcard.replace('\r ', '')
            file.write(serialized_vcard)  # 将 vCard 对象序列化为字符串并写入文件
            file.write("\n")  # 每个 vCard 对象之间添加一个换行


def quoted_printable_to_utf8(quoted_printable_string):
    """
    将 QUOTED-PRINTABLE 编码的字符串转换为 UTF-8 编码的字符串
    Args:
        quoted_printable_string: QUOTED-PRINTABLE 编码的字符串
    Returns:
        UTF-8 编码的字符串
    """
    decoded_bytes = quopri.decodestring(quoted_printable_string)
    return decoded_bytes.decode('utf-8')


# untest
def decode_vcard_lines(vcard_lines):
    """
    将 vCard 文件中的 QUOTED-PRINTABLE 编码的行转换为 UTF-8
    Args:
        vcard_lines: vCard 文件的行
    Returns:
        处理后的 vCard 行
    """
    decoded_lines = []
    for line in vcard_lines:
        if "QUOTED-PRINTABLE" in line:
            key, value = line.split(":", 1)
            value = quoted_printable_to_utf8(value)
            decoded_lines.append(f"{key}:{value}")
        else:
            decoded_lines.append(line)
    return decoded_lines


if __name__ == "__main__":
    vcards = csv_to_vcards("Contacts.csv")
