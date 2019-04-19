from flask_restplus import Api

from .image.route import api as image
from .user.route import api as user

api = Api(
    version='1.0',
    title='Docs Care API',
    description='Docs Care API입니다.'
)

api.add_namespace(image)
api.add_namespace(user)
