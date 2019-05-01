from flask import request, abort
from flask_restplus import Resource
from werkzeug.utils import secure_filename

import backend.settings as settings
from backend.app import mongo
from backend.flaskr.image.service import *
from backend.flaskr.image.swagger import api, image


@api.route('/<user_id>')
@api.param('user_id', 'The user identifier')
class ImagePost(Resource):
    @api.doc(id='get_image_byText', params={'content': 'image content text'})
    # @api.expect(insert_user_data)
    def get(self, user_id):
        '''get image by user_id and image content text'''
        return 'asd'

    @api.doc('post_image')
    # @api.expect(insert_user_data)
    def post(self, user_id):
        '''create image by user_id'''
        if 'docs_image' not in request.files:
            abort(400, 'No docs_image key in request.files')

        file = request.files['docs_image']

        if file.filename == "":
            return abort(400, 'Please select a file')

        if file and allowed_file(file.filename):
            file.filename = secure_filename(file.filename)
            output = upload_file_to_s3(file, settings.S3_LOCATION, settings.S3_BUCKET)
            print(output)

        # mongo.db.image.insert()
        return 'asd'


@api.route('/<user_id>/<image_id>')
@api.param('user_id', 'The user identifier')
@api.param('image_id', 'The image identifier')
class ImageWithImageId(Resource):
    @api.doc('get_image_byImageId')
    def get(self, user_id, image_id):
        '''get single image by user_id and image_id'''
        result = mongo.db.image.find_one({'user_id': user_id, 'image_id': image_id})

        if result is None:
            abort(400, 'image not found')

        return 'asd', 200

    @api.doc('delete_image_byImageId')
    def delete(self, user_id, image_id):
        '''delete image by user_id and image_id'''
        result = mongo.db.image.delete_one({'user_id': user_id, 'image_id': image_id})

        if result.deleted_count == 0:
            abort(400, 'image not found')

        return 'Deleted User', 204


@api.route('/<user_id>/<category_id>')
@api.param('user_id', 'The image identifier')
@api.param('category_id', 'The category identifier')
class ImageWithCategory(Resource):
    @api.doc('get_image_list_byCategory')
    @api.marshal_with(image)
    def get(self, user_id, category_id):
        '''get image list by by user_id and category_id'''
        result = mongo.db.image.find({'user_id': user_id, 'image_category': category_id})

        if result is None:
            abort(400, 'image not found')

        return 'asd', 200

@api.route('/<user_id>/<image_id>/<category_id>')
@api.param('user_id', 'The user identifier')
@api.param('image_id', 'The image identifier')
@api.param('category_id', 'Category id to modify')
class ImageCategoryUpdate(Resource):
    @api.doc('update_image_category_byImageId')
    @api.marshal_with(image)
    def put(self, user_id, image_id, category_id):
        '''get image list by by user_id and category_id'''
        result = mongo.db.image.find({'user_id': user_id, 'image_category': category_id})

        if result.modified_count == 0:
            abort(400, 'image not found')

        return 'asd', 200
