import pymongo


def init_app(url):
    return pymongo.MongoClient(url)
