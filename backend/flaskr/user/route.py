from flask_restplus import Namespace, Resource, fields
from backend.app import mongo


api = Namespace('user', description='회원 관련 api')

userCategory = api.model("category", {
    'category_name': fields.String(required=True, description='category_name')
})

User = [
    {'id': 'felix', 'name': 'Felix'},
]

user = api.model('User', {
    '_id': fields.String(required=True, description='mongo objectId'),
    'use_id': fields.String(required=True, description='The user id'),
    'nickname': fields.String(required=True, description='nickname'),
    'profile_image_path': fields.String(required=True, description='profile_image_path'),
    'thumbnail_image_path': fields.String(required=True, description='thumbnail_image_path'),
    'category': fields.List(required=True, description='user category list', cls_or_instance=fields.String)
})

@api.route('/<userId>')
@api.param('userId', 'The user identifier')
class Cat(Resource):
    @api.doc('post_user')
    # @api.marshal_with(user, code=201, description='Object created')
    def post(self, userId):
        '''create user by userId'''
        for user in User:
            if user['id'] != id:
                print(mongo.db.defaultCategories.find({'category_name': '커리어'}))
                return mongo.db.defaultCategories.find({'category_name': '커리어'})[0]['category_name']

    @api.doc('delete_user')
    @api.marshal_with(user)
    @api.response(404, 'user not found')
    def delete(self, userId):
        '''delete user by userId'''
        for user in User:
            if user['id'] == id:
                return user
        api.abort(404)
