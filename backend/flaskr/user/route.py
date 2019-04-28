from flask import request, abort
from flask_restplus import Resource
from backend.app import mongo
from backend.flaskr.user.swagger_models import api, user, insert_user_data


@api.route('/<user_id>')
@api.param('user_id', 'The user identifier')
class User(Resource):
    @api.doc('post_user')
    @api.marshal_with(user, code=201, description='Object created')
    @api.expect(insert_user_data)
    def post(self, user_id):
        '''create user by user_id'''
        req = request.get_json(force=True)
        user_document = {
            'user_id': user_id,
            'nickname': req['nickname'],
            'profile_image_path': req['profile_image_path'],
            'thumbnail_image_path': req['thumbnail_image_path'],
            'categories': []
        }

        for category in mongo.db.defaultCategories.find({}):
            category_document = {
                "category_name": category['category_name'],
                "category_included_image": []
            }

            user_document['categories'].append(category_document)

        mongo.db.users.insert(user_document)

        return user_document, 201

    @api.doc('delete_user')
    def delete(self, user_id):
        '''delete user by user_id'''
        result = mongo.db.users.delete_one({'user_id': user_id})
        if result.deleted_count == 0:
            abort(400, 'user not found')
        return 'Deleted User', 204
