from functools import wraps

from flask import request, abort

import settings as settings


def token_required(f):
    @wraps(f)
    def decorated(*args, **kwargs):

        token = None
        if 'X-API-KEY' in request.headers:
            token = request.headers['X-API-KEY']

        if not token:
            return abort(401, 'Token is missing.')

        if token != settings.API_KEY:
            return abort(401, 'Your token is wrong.')

        print('Token: {}'.format(token))
        return f(*args, **kwargs)

    return decorated
