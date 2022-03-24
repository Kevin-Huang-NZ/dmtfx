import tensorflow as tf
import tensorflow_addons as tfa


class DmTranslateTrain(tf.keras.Model):
    def __init__(self, kwargs):
        super(DmTranslateTrain, self).__init__()
        self.batch_size = kwargs.batch_size
        self.dm_max_length_chinese = kwargs.dm_max_length_chinese

        # encoder
        """
        Embedding
            input_dim: Integer. Size of the vocabulary, i.e. maximum integer index + 1.
            output_dim: Integer. Dimension of the dense embedding.
            input_length: Length of input sequences, when it is constant. 
        """
        self.encoder_embedding = tf.keras.layers.Embedding(kwargs.token_size_source, kwargs.embed_dim,
                                                           input_length=kwargs.dm_max_length_source)
        self.encoder_lstm_layer = tf.keras.layers.LSTM(kwargs.units, return_sequences=True, return_state=True,
                                                       recurrent_initializer='glorot_uniform')

        # decoder
        # Embedding Layer
        self.decoder_embedding = tf.keras.layers.Embedding(kwargs.token_size_chinese, kwargs.embed_dim,
                                                           input_length=kwargs.dm_max_length_chinese)
        # Final Dense layer on which softmax will be applied
        self.decoder_final_layer = tf.keras.layers.Dense(kwargs.token_size_chinese)
        # Sampler
        self.decoder_sampler = tfa.seq2seq.sampler.TrainingSampler()
        # Wrap attention mechanism with the fundamental rnn cell of decoder
        self.decoder_rnn_cell = tf.keras.layers.LSTMCell(kwargs.units)
        self.decoder_attention_mechanism = self.build_attention_mechanism(kwargs.units, None,
                                                                          self.batch_size * [kwargs.dm_max_length_source],
                                                                          kwargs.attention_type)
        self.decoder_rnn = tfa.seq2seq.AttentionWrapper(self.decoder_rnn_cell, self.decoder_attention_mechanism,
                                                        attention_layer_size=kwargs.units)
        # Define the decoder with respect to fundamental rnn cell
        self.decoder = tfa.seq2seq.BasicDecoder(self.decoder_rnn, sampler=self.decoder_sampler,
                                                output_layer=self.decoder_final_layer)

    @staticmethod
    def build_attention_mechanism(units, memory, memory_sequence_length, attention_type='luong'):
        """
        :param units: final dimension of attention outputs
        :param memory: encoder hidden states of shape (batch_size, max_length_input, enc_units)
        :param memory_sequence_length: 1d array of shape (batch_size) with every element set to max_length_input (for masking purpose)
        :param attention_type: Which sort of attention (Bahdanau, Luong)
        :return:
        """
        if attention_type == 'bahdanau':
            return tfa.seq2seq.BahdanauAttention(units=units, memory=memory,
                                                 memory_sequence_length=memory_sequence_length)
        else:
            return tfa.seq2seq.LuongAttention(units=units, memory=memory,
                                              memory_sequence_length=memory_sequence_length)

    def _decoder_initial_state(self, encoder_state):
        decoder_initial_state = self.decoder_rnn.get_initial_state(batch_size=self.batch_size, dtype=tf.float32)
        decoder_initial_state = decoder_initial_state.clone(cell_state=encoder_state)
        return decoder_initial_state

    def call(self, inputs, training=False):
        # print(inputs)
        encoder_input, decoder_input = inputs
        # print(decoder_input)
        # encoding
        encoder_embedded_input = self.encoder_embedding(encoder_input)
        encoder_output, h, c = self.encoder_lstm_layer(encoder_embedded_input)

        # decoding
        decoder_embedded_input = self.decoder_embedding(decoder_input)
        self.decoder_attention_mechanism.setup_memory(encoder_output)
        outputs, _, _ = self.decoder(decoder_embedded_input, initial_state=self._decoder_initial_state([h, c]),
                                     sequence_length=self.batch_size * [self.dm_max_length_chinese - 1])
        return outputs.rnn_output


@tf.function
def loss_function(real, pred):
    print(real)
    print(pred)
    cross_entropy = tf.keras.losses.SparseCategoricalCrossentropy(from_logits=True, reduction='none')
    loss = cross_entropy(y_true=real, y_pred=pred)
    mask = tf.logical_not(tf.math.equal(real, 0))
    mask = tf.cast(mask, dtype=loss.dtype)
    loss = mask * loss
    loss = tf.reduce_mean(loss)
    return loss


class DmTranslatePredict(tf.keras.Model):
    def __init__(self, train_model, start_token_idx, end_token_idx):
        super(DmTranslatePredict, self).__init__()
        self.start_token_idx = start_token_idx
        self.end_token_idx = end_token_idx
        # encoder
        self.encoder_embedding = train_model.encoder_embedding
        self.encoder_lstm_layer = train_model.encoder_lstm_layer

        # decoder
        self.decoder_embedding_matrix = train_model.decoder_embedding.variables[0]
        self.decoder_attention_mechanism = train_model.decoder_attention_mechanism
        self.decoder_rnn = train_model.decoder_rnn
        # Sampler
        self.decoder_sampler = tfa.seq2seq.GreedyEmbeddingSampler()
        # Define the decoder with respect to fundamental rnn cell
        self.decoder = tfa.seq2seq.BasicDecoder(self.decoder_rnn, sampler=self.decoder_sampler,
                                                output_layer=train_model.decoder_final_layer)

    def _decoder_initial_state(self, size, encoder_state):
        decoder_initial_state = self.decoder_rnn.get_initial_state(batch_size=size, dtype=tf.float32)
        decoder_initial_state = decoder_initial_state.clone(cell_state=encoder_state)
        return decoder_initial_state

    def call(self, inputs, training=False):
        inference_batch_size = inputs.shape[0]
        # print(inputs)
        # encoding
        encoder_embedded_input = self.encoder_embedding(inputs)
        encoder_output, h, c = self.encoder_lstm_layer(encoder_embedded_input)

        start_tokens = tf.fill([inference_batch_size], self.start_token_idx)
        # decoding
        self.decoder_attention_mechanism.setup_memory(encoder_output)
        outputs, _, _ = self.decoder(self.decoder_embedding_matrix, start_tokens=start_tokens, end_token=self.end_token_idx,
                                     initial_state=self._decoder_initial_state(inference_batch_size, [h, c]))
        return outputs.sample_id

