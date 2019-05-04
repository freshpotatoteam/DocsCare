from flask import request, abort
from flask_restplus import Resource

from backend.app import mongo, docscare_db
from backend.flaskr.user.swagger import api, user, insert_user


@api.route('/<user_id>')
@api.param('user_id', 'The user identifier')
class User(Resource):
    @api.doc('post_user')
    @api.expect(insert_user)
    @api.marshal_with(user, code=201, description='Success user insert')
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

        user_categories_document = {
            'user_id': user_id,
            'categories': []
        }

        for category in docscare_db.defaultCategories.find({}):
            user_document['categories'].append({
                'category_name': category['category_name'],
                'category_included_image': []
            })
            user_categories_document['categories'].append({
                'category_name': category['category_name'],
                'category_id': category['category_id'],
            })

        with mongo.start_session() as session:
            with session.start_transaction():
                user_insert_result = docscare_db.users.insert_one(user_document, session=session)
                user_categories_insert_result = docscare_db.userCategories.insert_one(user_categories_document,
                                                                                      session=session)

            if user_insert_result.inserted_id is None or user_categories_insert_result.inserted_id is None:
                session.abort_transaction()
                abort(500, 'Fail user insert')

        return user_document, 201

    @api.doc('delete_user')
    @api.response(204, 'Deleted User')
    def delete(self, user_id):
        '''delete user by user_id'''
        result = docscare_db.users.delete_one({'user_id': user_id})

        if result.deleted_count == 0:
            abort(400, 'User not found')

        return 'Deleted User', 204
