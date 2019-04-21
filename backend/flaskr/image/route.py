from flask_restplus import Namespace, Resource, fields

api = Namespace('image', description='이미지 관련 api')

image = api.model('Image', {
    'id': fields.String(required=True, description='Image id'),
    'name': fields.String(required=True, description='The image name'),
})

Images = [
    {'id': 'felix', 'name': 'Felix'},
]

@api.route('/')
class CatList(Resource):
    @api.doc(responses={403: 'Not Authorized'})
    @api.marshal_list_with(image)
    def get(self):
        '''List all cats'''
        return Images


@api.route('/<id>')
@api.param('id', 'The image identifier')
@api.response(404, 'Cat not found')
class Cat(Resource):
    @api.doc('get_cat')
    @api.marshal_with(image)
    def get(self, id):
        '''Fetch a image given its identifier'''
        for image in Images:
            if image['id'] == id:
                return image
        api.abort(404)
