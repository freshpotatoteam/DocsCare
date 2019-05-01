from flask_restplus import Namespace, fields

api = Namespace('images', description='이미지 관련 api')

image = api.model('Image', {
    'id': fields.String(required=True, description='Image id'),
    'name': fields.String(required=True, description='The image name'),
})

Images = [
    {'id': 'felix', 'name': 'Felix'},
]