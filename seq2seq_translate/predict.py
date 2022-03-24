import easydict
import os
import pickle
import numpy as np
import tensorflow as tf
from matplotlib import pyplot as plt
from seq2seq_dm_translate_model import DmTranslateTrain, loss_function, DmTranslatePredict
from preprocess_data import DM_MAX_LENGTH_SOURCE, DM_MAX_LENGTH_CHINESE, preprocess_dm_source, START_TOKEN, END_TOKEN, BATCH_SIZE

# DATA_FOLDER = "drive/MyDrive/Colab Notebooks/dmt/data"
# MODEL_FOLDER = "drive/MyDrive/Colab Notebooks/dmt/model"
DATA_FOLDER = "data"
MODEL_FOLDER = "model"


def load_preprocess():
    with open(os.path.join(DATA_FOLDER, 'preprocess.p'), mode='rb') as in_file:
        return pickle.load(in_file)


def translate(dm):
    dm_text = preprocess_dm_source(dm)

    inputs = np.zeros((1, DM_MAX_LENGTH_SOURCE))
    inputs[0][0] = token_to_idx_source[START_TOKEN]
    end_index_source = 1
    for index, token in enumerate(list(dm_text)):
        if token != "":
            inputs[0][index + 1] = token_to_idx_source[token]
            end_index_source = end_index_source + 1
    inputs[0][end_index_source] = token_to_idx_source[END_TOKEN]
    result = dm_translate_predict.predict(inputs, batch_size=inputs.shape[0])
    print(result)
    result = [idx_to_token_zh[c] for c in result[0]]
    print('Input: %s' % dm)
    print('Predicted translation: {}'.format(result))


def test_translate():
    translate('Ea. Las Maravillas')
    print('real: {}'.format('拉斯马拉维亚斯庄园'))

    translate('Huaiqueria')
    print('real: {}'.format('瓦伊克里亚村'))

    translate('Cauce Seco del R. Salado')
    print('real: {}'.format('考塞塞科-德尔萨拉多河'))

    translate('Co. Las Hormigas')
    print('real: {}'.format('拉斯奥尔米加斯山'))

    translate('La Estancia')
    print('real: {}'.format('拉埃斯坦西亚村'))

    translate('Ea. La Salteña')
    print('real: {}'.format('拉萨尔特尼亚庄园'))

    translate('Villa Mascardi')
    print('real: {}'.format('马斯卡尔迪村'))


(train_npa_source, train_npa_chinese), (val_npa_source, val_npa_chinese), \
        (token_to_idx_source, token_to_idx_zh), (idx_to_token_source, idx_to_token_zh) = load_preprocess()

args = easydict.EasyDict({
    "batch_size": BATCH_SIZE,
    "units": 1024,
    "dm_max_length_source": DM_MAX_LENGTH_SOURCE,
    "dm_max_length_chinese": DM_MAX_LENGTH_CHINESE,
    "token_size_source": len(token_to_idx_source),
    "token_size_chinese": len(token_to_idx_zh),
    "embed_dim": 256,
    "attention_type": "luong"
})

"""
初始化一下训练模型，然后加载weights
DmTranslateTrain需要参数如下：
  batch_size, units, dm_max_length_source, dm_max_length_chinese, token_size_source, token_size_chinese, embed_dim, attention_type="luong"  
"""
dm_translate_train = DmTranslateTrain(args)
dm_translate_train.compile(optimizer="adam", loss=loss_function)
dm_translate_train.fit((train_npa_source[:args.batch_size, :], train_npa_chinese[:args.batch_size, :]), train_npa_chinese[:args.batch_size, 1:], batch_size=args.batch_size, epochs=1)

checkpoint_prefix = os.path.join(MODEL_FOLDER, "ckpt")
dm_translate_train.load_weights(checkpoint_prefix)
dm_translate_predict = DmTranslatePredict(dm_translate_train, token_to_idx_source[START_TOKEN], token_to_idx_source[END_TOKEN])

test_translate()

