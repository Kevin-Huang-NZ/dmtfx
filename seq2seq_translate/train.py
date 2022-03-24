import easydict
import os
import pickle
import tensorflow as tf
from seq2seq_dm_translate_model import DmTranslateTrain, loss_function
from preprocess_data import DM_MAX_LENGTH_SOURCE, DM_MAX_LENGTH_CHINESE, BATCH_SIZE

# DATA_FOLDER = "drive/MyDrive/Colab Notebooks/dmt/data"
# MODEL_FOLDER = "drive/MyDrive/Colab Notebooks/dmt/model"
DATA_FOLDER = "data"
MODEL_FOLDER = "model"


def load_preprocess():
    with open(os.path.join(DATA_FOLDER, 'preprocess.p'), mode='rb') as in_file:
        return pickle.load(in_file)


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
开始训练
DmTranslateTrain需要参数如下：
  batch_size, units, dm_max_length_source, dm_max_length_chinese, token_size_source, token_size_chinese, embed_dim, attention_type="luong"  
"""
dm_translate_train = DmTranslateTrain(args)
dm_translate_train.compile(optimizer="adam", loss=loss_function)

checkpoint_prefix = os.path.join(MODEL_FOLDER, "ckpt")
checkpoint_callback = tf.keras.callbacks.ModelCheckpoint(filepath=checkpoint_prefix, save_weights_only=True)
dm_translate_train.fit((train_npa_source, train_npa_chinese), train_npa_chinese[:, 1:], batch_size=args.batch_size, epochs=10, callbacks=[checkpoint_callback])
# dm_translate_train.fit((train_npa_source, train_npa_chinese), train_npa_chinese[:, 1:], batch_size=args.batch_size, epochs=10, callbacks=[checkpoint_callback], validation_data=((val_npa_source, val_npa_chinese), val_npa_chinese[:, 1:]))
