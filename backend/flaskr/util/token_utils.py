from functools import wraps

from flask import request

import backend.settings as settings


def token_required(f):
    @wraps(f)
    def decorated(*args, **kwargs):

        token = None
        if 'X-API-KEY' in request.headers:
            token = request.headers['X-API-KEY']

        if not token:
            return 'Token is missing.', 401

        if token != settings.API_KEY:
            return 'Your token is wrong.', 401

        print('Token: {}'.format(token))
        return f(*args, **kwargs)

    return decorated
