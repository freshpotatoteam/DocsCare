from flask import request, abort
from flask_restplus import Resource

from app import mongo, docscare_db
from flaskr.user.swagger import api, user, insert_user
from flaskr.util.token_utils import token_required


@api.route('/<user_id>')
@api.param('user_id', 'The user identifier')
class User(Resource):
    @api.doc('post_user')
    @api.expect(insert_user)
    @api.marshal_with(user, code=201, description='Success User Insert')
    @token_required
    def post(self, user_id):
        '''create user by user_id'''
        req = request.get_json(force=True)
        user_document = {
            'user_id': user_id,
            'nickname': req['nickname'],
            'profile_image_path': req['profile_image_path'],
            'thumbnail_image_path': req['thumbnail_image_path'],
        }

        user_categories_document = {
            'user_id': user_id,
            'categories': []
        }

        for category in docscare_db.defaultCategories.find({}):
            user_categories_document['categories'].append({
                'category_name': category['category_name'],
                'category_id': category['category_id'],
            })

        with mongo.start_session() as session:
            with session.start_transaction():
                try:
                    docscare_db.users.insert_one(user_document, session=session)
                    docscare_db.userCategories.insert_one(user_categories_document, session=session)
                except Exception as e:
                    session.abort_transaction()
                    abort(500, 'Failed user insert, {}'.format(e))

        return user_document, 201

    @api.doc('delete_user')
    @api.response(200, 'Deleted User')
    @token_required
    def delete(self, user_id):
        '''delete user by user_id'''
        result = None

        with mongo.start_session() as session:
            with session.start_transaction():
                try:
                    result = docscare_db.users.delete_one({'user_id': user_id})
                    docscare_db.userCategories.delete_one({'user_id': user_id})
                    docscare_db.userImages.delete({'user_id': user_id})
                except Exception as e:
                    session.abort_transaction()
                    abort(500, 'Failed user delete, {}'.format(e))

                if result.deleted_count == 0:
                    session.abort_transaction()
                    abort(400, 'User not found')

        return 'Deleted User', 200
