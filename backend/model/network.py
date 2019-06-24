# -*- coding: utf-8 -*-

import tensorflow as tf
import numpy as np
from tensorflow.contrib import rnn


# hidden_size : 각 출력 데이터 하나의 크기 (1) // 출력 : 0 ~ 1
# data_dim : 각 입력 데이터 하나의 크기 (128) // 입력 : 128차원 문장 벡터
# 벡터 5개를 넣어 어뷰징 유무를 예측하는 모델
class RNN:
    def __init__(self, session, data_dim, seq_length, hidden_size, learning_late=0.001, name="main"):
        self.session = session
        self.data_dim = data_dim
        self.seq_length = seq_length
        self.learning_late = learning_late

        self.hidden_size = hidden_size
        self.net_name = name

        self._build_network(self.learning_late)
        self.saver = tf.train.Saver()
        self.save_path = "./save/save_model_" + self.net_name + ".ckpt"
        tf.logging.info(name + " - initialized")


    def lstm_cell(self, size, keep_prob):
        cell = rnn.BasicLSTMCell(size, state_is_tuple=True, activation=tf.tanh)
        drop = rnn.DropoutWrapper(cell=cell, output_keep_prob=keep_prob)
        return drop


    def _build_network(self, l_rate):
        with tf.variable_scope(self.net_name):
            self._X = tf.placeholder(tf.float64, [None, self.seq_length, self.data_dim], name="input_x")
            self._Y = tf.placeholder(shape=[None, self.seq_length, self.hidden_size], dtype=tf.float64, name="input_y")
            self._keep_prob = tf.placeholder(tf.float64, name="kp")

            multi_cells = rnn.MultiRNNCell([self.lstm_cell(self.hidden_size, self._keep_prob) for _ in range(3)],
                                           state_is_tuple=True)

            outputs, _states = tf.nn.dynamic_rnn(multi_cells, self._X, dtype=tf.float64)

            self._Qpred = tf.contrib.layers.fully_connected(outputs, self.hidden_size, activation_fn=tf.nn.sigmoid)
            self.prediction = tf.round(self._Qpred)

            self.cost = tf.reduce_mean(tf.nn.sigmoid_cross_entropy_with_logits(
                logits=self._Qpred, labels=self._Y))
            self._train = tf.train.AdamOptimizer(learning_rate=l_rate).minimize(self.cost)

            correct_prediction = tf.equal(self.prediction, self._Y)
            self.accuracy = tf.reduce_mean(tf.cast(correct_prediction, tf.float32))


    def save(self, episode=0):
        self.saver.save(self.session, self.save_path+ "-" + str(episode))

    def restore(self, episode=0):
        load_path = self.save_path + "-" + str(episode)
        self.saver.restore(self.session, load_path)

    def predict(self, input_x):
        predict = self.session.run(self.prediction, feed_dict={
            self._X: input_x,
            self._keep_prob: 1.0
        })
        return predict

    def update(self, input_x, input_y):
        return self.session.run([self.cost, self._train, self.accuracy], feed_dict={
            self._X: input_x,
            self._Y: input_y,
            self._keep_prob: 0.7
        })
