from flask import request, abort
from flask_restplus import Resource

from backend.app import docscare_db
from backend.flaskr.category.swagger import api, user_category_item

category_prefix = 'C'


@api.route('')
class Category(Resource):
    default_parser = api.parser()
    default_parser.add_argument('user_id', help='The user identifier', required=True)

    with_category_name_parser = api.parser()
    with_category_name_parser.add_argument('user_id', help='The user identifier', required=True)
    with_category_name_parser.add_argument('category_name', help='Category Name To add',
                                           required=True)

    @api.doc('get_user_category_list')
    @api.expect(default_parser, validate=True)
    @api.marshal_with([user_category_item])
    def get(self):
        '''get category list by user_id'''
        result = docscare_db.userCategories.find_one({'user_id': request.args.get('user_id')})

        if result is None:
            abort(400, 'User Categories Not Found')

        return result['categories']

    @api.doc('add_user_category_list')
    @api.expect(with_category_name_parser, validate=True)
    @api.response(200, 'Success Category Add')
    def put(self):
        '''add category by user_id'''

        user_category_by_user_id = docscare_db.userCategories.find_one({'user_id': request.args.get('user_id')})

        if user_category_by_user_id is None:
            abort(400, 'User categories not found')

        last_key_index = int(
            docscare_db.userCategories.find_one({'user_id': request.args.get('user_id')})['categories'][-1][
                'category_id'][len(category_prefix):]) + 1

        try:
            docscare_db.userCategories.update_one({
                'user_id': request.args.get('user_id')
            }, {
                '$push': {'categories': {
                    'category_name': request.args.get('category_name'),
                    'category_id': category_prefix + str(last_key_index)}
                }
            })
        except Exception as e:
            abort(500, 'Failed category add, {}'.format(e))

        return 'Success category add', 200

    @api.doc('delete_user_category')
    @api.expect(default_parser, validate=True)
    @api.response(204, 'Deleted User Category')
    def delete(self):
        '''delete user category by user_id'''
        result = None

        try:
            result = docscare_db.userCategories.delete_one({'user_id': request.args.get('user_id')})
        except Exception as e:
            abort(500, 'Failed deleted category, {}'.format(e))

        if result.deleted_count == 0:
            abort(400, 'User categories not found')

        return 'Deleted User Category', 204


@api.route('/<category_id>')
@api.param('category_id', 'The category identifier')
class CategoryNameUpdate(Resource):
    with_category_name_parser = api.parser()
    with_category_name_parser.add_argument('user_id', help='The user identifier', required=True)
    with_category_name_parser.add_argument('category_name', help='Category Name To Update ', required=True)

    @api.doc('update_user_category_name')
    @api.expect(with_category_name_parser, validate=True)
    @api.response(200, 'Success Category Name Update')
    def put(self, category_id):
        '''update category name by user_id and category name text'''
        result = None

        try:
            result = docscare_db.userCategories.update_one({
                'user_id': request.args.get('user_id'),
                'categories': {'$elemMatch': {'category_id': category_id}}
            }, {
                '$set': {'categories.$.category_name': request.args.get('category_name')}
            })
        except Exception as e:
            abort(500, 'Failed category name update, {}'.format(e))

        if result.matched_count == 0:
            abort(400, 'User categories not found')
        elif result.matched_count != 0 and result.modified_count == 0:
            abort(400, 'Already equal category name')

        return 'Success category name update', 200
