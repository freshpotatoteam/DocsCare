# -*- coding: utf-8 -*-

import tensorflow as tf
from backend.model.network import RNN
import pymysql
import json
import random
import backend.db as db
import backend.settings as settings


data_dim = 128

# 출력 사이즈
hidden_size = 1

# 총 입력길이
sequence_length = 5
learning_rate = 1e-4
batch_size = 10000

data = []


mongo_url = 'mongodb://{}:{}@{}:{}/{}?authSource=admin&ssl=true&readPreference=secondary'.format(
    settings.MONGO_USER,
    settings.MONGO_PASS,
    settings.MONGO_HOST,
    settings.MONGO_PORT,
    settings.MONGO_DB)

mongo = db.init_app(mongo_url)
docscare_db = mongo['docscare']
connection = pymysql.connect(host='10.105.185.157', user='root', password='', db='comment_hackday', charset='utf8mb4', )

input_X = []
input_Y = []
with mongo.start_session() as session:
    with session.start_transaction():
        print('a')
        # abuse_list = []
        # not_abuse_list = []
        #
        # cursor.execute(abuse_sql)
        # rows = cursor.fetchall()
        # for row in rows:
        #     if row[0]:
        #         abuse_list.append(row[0])
        #
        # print("abuse_id load")
        # print("========================")
        #
        # cursor.execute(not_abuse_sql)
        # rows = cursor.fetchall()
        # for row in rows:
        #     if row[0]:
        #         not_abuse_list.append(row[0])
        #
        # print("not_abuse_id load")
        # print("========================")
        #
        # idx = 0
        #
        # for abuse in abuse_list:
        #     tmp_vector_list = []
        #
        #     cursor.execute(comments_sql, (abuse))
        #     rows = cursor.fetchall()
        #     for row in rows:
        #         if row[0]:
        #             tmp_vector_list.append(json.loads(row[0]))
        #
        #     if len(tmp_vector_list) < sequence_length:
        #         continue
        #
        #     for tmp_idx in range(len(tmp_vector_list)-sequence_length+1):
        #         input_X.append(tmp_vector_list[tmp_idx:tmp_idx+sequence_length])
        #         input_Y.append([[0], [0], [0], [1], [1]])
        #
        #     print("{} / {}".format(len(abuse_list), idx))
        #     idx += 1
        #
        # print("abuse_article load")
        # print("========================")
        #
        # idx=0
        # for abuse in not_abuse_list:
        #     tmp_vector_list = []
        #
        #     cursor.execute(comments_sql, (abuse))
        #     rows = cursor.fetchall()
        #     for row in rows:
        #         if row[0]:
        #             tmp_vector_list.append(json.loads(row[0]))
        #
        #     if len(tmp_vector_list) < sequence_length:
        #         continue
        #
        #     for tmp_idx in range(len(tmp_vector_list)-sequence_length+1):
        #         input_X.append(tmp_vector_list[tmp_idx:tmp_idx+sequence_length])
        #         input_Y.append([[0], [0], [0], [0], [0]])
        #
        #     print("{} / {}".format(len(abuse_list), idx))
        #     idx += 1
        #
        # print("not_abuse_article load")
        # print("========================")



def main():
    with tf.Session() as sess:
        net = RNN(sess, data_dim, sequence_length, hidden_size, learning_rate)
        tf.global_variables_initializer().run()

        start = 0
        net.restore(400)

        epoch = 2000

        for i in range(epoch):
            for batch in range(0, len(input_X), batch_size):
                data_X = input_X[batch:batch + batch_size]
                data_Y = input_Y[batch:batch + batch_size]

                loss, train, acc = net.update(data_X, data_Y)
                print("loss : {} / acc : {}".format(loss, acc))

            print("index : {}".format(i))
            if i != start and i % 100 == 0:
                net.save(i)


if __name__ == "__main__":
    main()


