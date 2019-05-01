from flask_restplus import Api

from .image.route import api as image
from .user.route import api as user
from .category.route import api as category

api = Api(
    version='1.0',
    title='Docs Care API',
    description='Docs Care API입니다.',
    doc="/swagger-ui"
)

api.add_namespace(image)
api.add_namespace(user)
api.add_namespace(category)
