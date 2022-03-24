import easydict
import os
import pickle
import numpy as np
import tensorflow as tf
from sklearn.model_selection import train_test_split
from seq2seq_dm_translate_model import DmTranslateTrain, loss_function
from preprocess_data import DM_MAX_LENGTH_SOURCE, DM_MAX_LENGTH_CHINESE

# DATA_FOLDER = "drive/MyDrive/Colab Notebooks/dmt/data"
# MODEL_FOLDER = "drive/MyDrive/Colab Notebooks/dmt/model"
DATA_FOLDER = "data"
MODEL_FOLDER = "model"


def load_preprocess():
    with open(os.path.join(DATA_FOLDER, 'preprocess.p'), mode='rb') as in_file:
        return pickle.load(in_file)


def get_numpy_array_from_dataset(ds):
    source = np.concatenate([inp for (inp, targ) in ds.as_numpy_iterator()])
    target = np.concatenate([targ for (inp, targ) in ds.as_numpy_iterator()])
    return source, target


(dm_ids_source, dm_ids_chinese), (token_to_idx_source, token_to_idx_zh), (idx_to_token_source, idx_to_token_zh) = load_preprocess()

args = easydict.EasyDict({
    "batch_size": 64,
    "units": 1024,
    "dm_max_length_source": DM_MAX_LENGTH_SOURCE,
    "dm_max_length_chinese": DM_MAX_LENGTH_CHINESE,
    "token_size_source": len(token_to_idx_source),
    "token_size_chinese": len(token_to_idx_zh),
    "embed_dim": 256,
    "attention_type": "luong"
})

"""
处理数据
1、分成训练和验证数据集
2、训练集执行shuffle，切成批次大小的倍数
3、验证集切成批次大小的倍数
4、将dataset转换成numpy array。因为模型把encoder和decoder包在一起，所以call方法的input需要包含source和chinese数据
source_dm_train, source_dm_val, chinese_dm_train, chinese_dm_val = train_test_split(dm_ids_source, dm_ids_chinese, test_size=0.2)

train_dataset = tf.data.Dataset.from_tensor_slices((source_dm_train, chinese_dm_train))
train_dataset = train_dataset.shuffle(6000).batch(args.batch_size, drop_remainder=True)

val_dataset = tf.data.Dataset.from_tensor_slices((source_dm_val, chinese_dm_val))
val_dataset = val_dataset.batch(args.batch_size, drop_remainder=True)

train_npa_source, train_npa_chinese = get_numpy_array_from_dataset(train_dataset)
val_npa_source, val_npa_chinese = get_numpy_array_from_dataset(val_dataset)
"""
"""
处理数据
1、使用所有数据创建dataset，执行shuffle
2、将dataset再转换成numpy array。
3、分成训练和验证数据集
4、将dataset转换成numpy array。
"""
all_dataset = tf.data.Dataset.from_tensor_slices((dm_ids_source, dm_ids_chinese))
all_dataset = all_dataset.shuffle(6000).batch(args.batch_size, drop_remainder=False)
all_npa_source, all_npa_chinese = get_numpy_array_from_dataset(all_dataset)
source_dm_train, source_dm_val, chinese_dm_train, chinese_dm_val = train_test_split(all_npa_source, all_npa_chinese, test_size=0.2)

train_dataset = tf.data.Dataset.from_tensor_slices((source_dm_train, chinese_dm_train))
train_dataset = train_dataset.batch(args.batch_size, drop_remainder=True)
val_dataset = tf.data.Dataset.from_tensor_slices((source_dm_val, chinese_dm_val))
val_dataset = val_dataset.batch(args.batch_size, drop_remainder=True)
train_npa_source, train_npa_chinese = get_numpy_array_from_dataset(train_dataset)
val_npa_source, val_npa_chinese = get_numpy_array_from_dataset(val_dataset)

"""
开始训练
DmTranslateTrain需要参数如下：
  batch_size, units, dm_max_length_source, dm_max_length_chinese, token_size_source, token_size_chinese, embed_dim, attention_type="luong"  
"""
dm_translate_train = DmTranslateTrain(args)
dm_translate_train.compile(optimizer="adam", loss=loss_function)

checkpoint_prefix = os.path.join(MODEL_FOLDER, "ckpt")
checkpoint_callback = tf.keras.callbacks.ModelCheckpoint(filepath=checkpoint_prefix, save_weights_only=True)
# dm_translate_train.fit((train_npa_source, train_npa_chinese), train_npa_chinese[:, 1:], batch_size=args.batch_size, epochs=10, callbacks=[checkpoint_callback])
dm_translate_train.fit((train_npa_source, train_npa_chinese), train_npa_chinese[:, 1:], batch_size=args.batch_size, epochs=10, callbacks=[checkpoint_callback], validation_data=((val_npa_source, val_npa_chinese), val_npa_chinese[:, 1:]))
