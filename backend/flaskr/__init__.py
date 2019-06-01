from flask_restplus import Api

from .image.route import api as image
from .user.route import api as user
from .category.route import api as category

authorizations = {
    'apikey': {
        'type': 'apiKey',
        'in': 'header',
        'name': 'X-API-KEY'
    }
}

api = Api(
    version='1.0',
    title='Docs Care API',
    description='Docs Care API입니다.',
    doc="/swagger-ui",
    authorizations=authorizations,
    security='apikey'
)

api.add_namespace(image)
api.add_namespace(user)
api.add_namespace(category)
# test