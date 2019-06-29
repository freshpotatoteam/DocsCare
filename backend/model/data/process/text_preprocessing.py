import os

import nltk
from konlpy.tag import Okt
from nltk.corpus import stopwords
from nltk.tokenize import word_tokenize

nltk.download('punkt')
nltk.download('stopwords')
ko_stop_words = set(stopwords.words('english'))
en_stop_words = set(stopwords.words('english'))
okt = Okt()

script_dir = os.path.dirname(__file__)  # <-- absolute dir the script is in
stopwordsFile = open(os.path.join(script_dir, '../stopword/ko_stopwords.txt'), 'r')
kr_stop_words = [data for data in stopwordsFile.read().split()]


def one_hot_encoding(word, word2index):
    one_hot_vector = [0] * (len(word2index))
    index = word2index[word]
    one_hot_vector[index] = 1
    return one_hot_vector


def tokenizer(str, lang_type):
    result = []

    if lang_type == 'ko':
        word_tokens = okt.nouns(str)

        for w in word_tokens:
            if w not in kr_stop_words and len(w) >= 2:
                result.append(w)

        return result
    else:
        word_tokens = word_tokenize(str)

        for w in word_tokens:
            if w not in en_stop_words and len(w) >= 4:
                result.append(w.lower())

        return result
