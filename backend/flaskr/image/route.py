from flask import request, abort
from flask_restplus import Resource
from werkzeug.utils import secure_filename

import backend.settings as settings
from backend.app import docscare_db
from backend.flaskr.image.service import *
from backend.flaskr.image.swagger import api, image


@api.route('')
class ImagePost(Resource):
    default_parser = api.parser()
    default_parser.add_argument('user_id', help='The user identifier', required=True)

    with_content_parser = api.parser()
    with_content_parser.add_argument('user_id', help='The user identifier', required=True)
    with_content_parser.add_argument('content', help='image content text', required=True)

    @api.expect(with_content_parser, validate=True)
    @api.doc('get_image_byText')
    def get(self):
        '''get image by user_id and image content text'''
        return 'asd'

    @api.expect(default_parser, validate=True)
    @api.doc('post_image')
    def post(self):
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

        # docscare_db.image.insert()
        return 'asd'


@api.route('/<image_id>')
@api.param('image_id', 'The image identifier')
class Image(Resource):
    parser = api.parser()
    parser.add_argument('user_id', help='The user identifier', required=True)

    @api.expect(parser, validate=True)
    @api.doc('get_image_byImageId')
    def get(self, image_id):
        '''get single image by user_id and image_id'''
        result = docscare_db.image.find_one({'user_id': request.args.get('user_id'), 'image_id': image_id})

        if result is None:
            abort(400, 'image not found')

        return 'asd', 200

    @api.expect(parser, validate=True)
    @api.doc('delete_image_byImageId')
    def delete(self, image_id):
        '''delete image by user_id and image_id'''
        result = docscare_db.image.delete_one({'user_id': request.args.get('user_id'), 'image_id': image_id})

        if result.deleted_count == 0:
            abort(400, 'image not found')

        return 'Deleted User', 204


@api.route('/<category_id>')
@api.param('category_id', 'The category identifier')
class ImageWithCategory(Resource):
    default_parser = api.parser()
    default_parser.add_argument('user_id', help='The user identifier', required=True)

    @api.expect(default_parser, validate=True)
    @api.doc('get_image_list_byCategory')
    @api.marshal_with(image)
    def get(self, category_id):
        '''get image list by by user_id and category_id'''
        result = docscare_db.image.find({'user_id': request.args.get('user_id'), 'image_category': category_id})

        if result is None:
            abort(400, 'image not found')

        return 'asd', 200


@api.route('/<image_id>/<category_id>')
@api.param('image_id', 'The image identifier')
@api.param('category_id', 'Category id to modify')
class ImageCategoryUpdate(Resource):
    default_parser = api.parser()
    default_parser.add_argument('user_id', help='The user identifier', required=True)

    @api.doc('update_image_category_byImageId')
    @api.marshal_with(image)
    def put(self, image_id, category_id):
        '''get image list by by user_id and category_id'''
        result = docscare_db.image.find({'user_id': request.args.get('user_id'), 'image_category': category_id})

        if result.modified_count == 0:
            abort(400, 'image not found')

        return 'asd', 200
