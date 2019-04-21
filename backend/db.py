from flask import Flask
from flask_pymongo import PyMongo


def init_app(app: Flask):
    return PyMongo(app)
