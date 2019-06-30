# Import all the dependencies
import re

from gensim.models.doc2vec import Doc2Vec, TaggedDocument

import backend.db as db
import backend.settings as settings

mongo_url = 'mongodb://{}:{}@{}:{}/{}?authSource=admin&ssl=true&readPreference=secondary'.format(
    settings.MONGO_USER,
    settings.MONGO_PASS,
    settings.MONGO_HOST,
    settings.MONGO_PORT,
    settings.MONGO_DB)

mongo = db.init_app(mongo_url)
docscare_db = mongo['docscare']

category_map = {
    'C1': '커리어',
    'C2': '학업',
    'C3': '금융',
    'C4': '공공문서',
    'C6': '여행',
    'C7': '부동산',
}

cursor = docscare_db.categorySampleData.find({})

name = re.compile(r'.*[a-zA-Z].*')
# for document in cursor:

tagged_data = [TaggedDocument(words=_d['tokenizer_result'], tags=[category_map[_d['category_id']]]) for i, _d in
               enumerate(cursor) if (len(_d['tokenizer_result']) > 1 and not name.match(_d['tokenizer_result'][0])) ]


# max_epchs만큼 끊어서 학습(bacth 처리용)
max_epochs = 100
vec_size = 20
# The initial learning rate.
alpha = 0.025

# min_count = Ignores all words with total frequency lower than this.
# size = vector size
# min_alpha = Learning rate will linearly drop to min_alpha as training progresses.
# dm = dm ({1,0}, optional) – Defines the training algorithm. If dm=1, ‘distributed memory’ (PV-DM) is used. Otherwise, distributed bag of words (PV-DBOW) is employed.
model = Doc2Vec(vector_size=vec_size,
                alpha=alpha,
                min_alpha=0.00025,
                min_count=1,
                dm=1)

model.build_vocab(tagged_data)

for epoch in range(max_epochs):
    print('iteration {0}'.format(epoch))
    model.train(tagged_data,
                total_examples=model.corpus_count,
                epochs=model.iter)
    # decrease the learning rate
    model.alpha -= 0.0002
    # fix the learning rate, no decay
    model.min_alpha = model.alpha

model.save("./save/d2v.model")
print("Model Saved")
