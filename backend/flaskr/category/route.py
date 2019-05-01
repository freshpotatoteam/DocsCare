from flask import request, abort
from flask_restplus import Resource

from backend.app import mongo
from backend.flaskr.category.swagger import api, category


@api.route('/<user_id>')
@api.param('user_id', 'The user identifier')
class CategoryPost(Resource):
    @api.doc('post_image')
    # @api.expect(insert_user_data)
    def post(self, user_id):
        '''create category by user_id'''
        if 'docs_image' not in request.files:
            abort(400, 'No docs_image key in request.files')

        file = request.files['docs_image']

        if file.filename == "":
            return abort(400, 'Please select a file')
        # mongo.db.image.insert()
        return 'asd'

    @api.doc('get_category_list')
    # @api.expect(insert_user_data)
    def get(self, user_id):
        '''get category list by user_id'''
        return 'asd'