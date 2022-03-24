import os
import re
import pickle
import numpy as np
import pandas as pd
import tensorflow as tf
from sklearn.model_selection import train_test_split

PADDING_TOKEN = "~"
START_TOKEN = "^"
END_TOKEN = "$"
# 外文地名最大长度（字母数量）
DM_MAX_LENGTH_SOURCE = 100
# 汉语地名最大长度（汉字数量）
DM_MAX_LENGTH_CHINESE = 50

# DATA_FOLDER = "drive/MyDrive/Colab Notebooks/dmt/data"
DATA_FOLDER = "data"
BATCH_SIZE = 64


def preprocess_dm_source(dm):
    """
    处理外文地名
    1、将多个空格替换成一个空格
    2、删除两端的空格、换行符等
    :param dm: 一个地名
    :return: 处理后的地名
    """
    dm = re.sub(r'[" "]+', " ", dm)
    dm = dm.strip()
    return dm


def preprocess_dm_chinese(dm):
    """
    处理中文地名
    1、删除所有空格
    2、删除两端的空格、换行符等
    :param dm: 一个地名
    :return: 处理后的地名
    """
    dm = re.sub(r' ', "", dm)
    dm = dm.strip()
    return dm


def load_data(file_path):
    """
    读取地名文件，解析出外文和中文的字符总数（去重后），做成字符和索引映射表。
    加工地名数据，首尾增加开始和结束标记。
    :param file_path: 文件路径
    :return: 字符和索引映射表, 地名列表
    """
    df = pd.read_table(file_path)
    df.columns = ['source', 'chinese']
    # 获取外文和中文字符数组
    characters_source = sorted(list(set(df.source.unique().sum())))
    characters_chinese = sorted(list(set(df.chinese.unique().sum())))

    # 添加的开头结尾符号
    special_characters = [PADDING_TOKEN, START_TOKEN, END_TOKEN]
    token_to_idx_source = dict([(char, i) for i, char in enumerate(special_characters + characters_source)])
    token_to_idx_zh = dict([(char, i) for i, char in enumerate(special_characters + characters_chinese)])
    idx_to_token_source = dict([(i, char) for i, char in enumerate(special_characters + characters_source)])
    idx_to_token_zh = dict([(i, char) for i, char in enumerate(special_characters + characters_chinese)])

    # 给地名添加开始和结束符
    df['source'] = df['source'].apply(lambda x: START_TOKEN + preprocess_dm_source(x) + END_TOKEN)
    df['chinese'] = df['chinese'].apply(lambda x: START_TOKEN + preprocess_dm_chinese(x) + END_TOKEN)
    # 获取地名数组，1维
    dm_text_source = df.source.values.tolist()
    dm_text_chinese = df.chinese.values.tolist()
    return (dm_text_source, dm_text_chinese), (token_to_idx_source, token_to_idx_zh), (idx_to_token_source, idx_to_token_zh)


def dm_to_ids(dm_text, token_to_idx):
    """
    将外文地名转换成单词索引数组，中文地名转换成汉字索引数组
    :param dm_text: 一个元组，包含：外文地名数组和中文地名数组，数组都是1维的
    :param token_idx: 一个元组，包含：外文和中文的字符到索引的映射表
    :return: 外文地名单词索引数组 / 中文地名汉字索引数组, 2D, shape(数据集的大小, 语种地名的最大长度-预定义)
    """
    dm_text_source, dm_text_chinese = dm_text
    token_to_idx_source, token_to_idx_zh = token_to_idx
    # 使用固定长度，替代从数据集中查找最大长度
    # max_length_source = max([len(dm) for dm in dm_text_source]) + 2
    # max_length_chinese = max([len(dm) for dm in dm_text_chinese]) + 2
    # print(f"外文地名最大长度：{max_length_source}")
    # print(f"中文地名最大长度：{max_length_chinese}")

    # 将地名数据转换成定长，长度为最长地名的长度。padding内容为：0(字符到索引的映射表中，把PADDING_TOKEN放在第一位，所以它的index是0)
    dm_ids_source = np.zeros((len(dm_text_source), DM_MAX_LENGTH_SOURCE), dtype=np.float32)
    dm_ids_chinese = np.zeros((len(dm_text_chinese), DM_MAX_LENGTH_CHINESE), dtype=np.float32)

    for i in range(len(dm_text_source)):
        one_dm_source = dm_text_source[i]
        one_dm_chinese = dm_text_chinese[i]

        tokens_source = list(one_dm_source)
        tokens_chinese = list(one_dm_chinese)

        end_index_source = 0
        end_index_chinese = 0
        for index, token in enumerate(tokens_source):
            if token != "":
                dm_ids_source[i][index + 1] = token_to_idx_source[token]
                end_index_source = end_index_source + 1

        for index, token in enumerate(tokens_chinese):
            if token != "":
                dm_ids_chinese[i][index + 1] = token_to_idx_zh[token]
                end_index_chinese = end_index_chinese + 1

    return dm_ids_source, dm_ids_chinese


def get_numpy_array_from_dataset(ds):
    source = np.concatenate([inp for (inp, targ) in ds.as_numpy_iterator()])
    target = np.concatenate([targ for (inp, targ) in ds.as_numpy_iterator()])
    return source, target


def preprocess_and_save():
    dm_text, token_to_idx, idx_to_token = load_data(os.path.join(DATA_FOLDER, "argentine-chinese.txt"))

    dm_ids_source, dm_ids_chinese = dm_to_ids(dm_text, token_to_idx)

    # print(dm_ids_source.shape)
    # print(dm_ids_chinese.shape)
    # print(token_idx)
    # print(idx_token)

    # shuffle
    all_dataset = tf.data.Dataset.from_tensor_slices((dm_ids_source, dm_ids_chinese))
    all_dataset = all_dataset.shuffle(6000).batch(BATCH_SIZE, drop_remainder=False)
    all_npa_source, all_npa_chinese = get_numpy_array_from_dataset(all_dataset)

    # split to train and validate
    source_dm_train, source_dm_val, chinese_dm_train, chinese_dm_val = train_test_split(all_npa_source, all_npa_chinese,
                                                                                        test_size=0.2)

    # drop remainder
    train_dataset = tf.data.Dataset.from_tensor_slices((source_dm_train, chinese_dm_train))
    train_dataset = train_dataset.batch(BATCH_SIZE, drop_remainder=True)
    val_dataset = tf.data.Dataset.from_tensor_slices((source_dm_val, chinese_dm_val))
    val_dataset = val_dataset.batch(BATCH_SIZE, drop_remainder=True)
    train_npa_source, train_npa_chinese = get_numpy_array_from_dataset(train_dataset)
    val_npa_source, val_npa_chinese = get_numpy_array_from_dataset(val_dataset)

    pickle.dump((
        (train_npa_source, train_npa_chinese),
        (val_npa_source, val_npa_chinese),
        token_to_idx,
        idx_to_token), open(os.path.join(DATA_FOLDER, 'preprocess.p'), 'wb'))


if __name__ == '__main__':
    preprocess_and_save()

