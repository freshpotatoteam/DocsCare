from bson.objectid import ObjectId
from flask import request, abort
from flask_restplus import Resource
from werkzeug.utils import secure_filename

import backend.settings as settings
from backend.app import docscare_db
from backend.flaskr.image.service import *
from backend.flaskr.image.swagger import api, user_image, insert_user_image
from backend.flaskr.util.token_utils import token_required


@api.route('')
class Image(Resource):
    default_parser = api.parser()
    default_parser.add_argument('user_id', help='The user identifier', required=True)

    with_content_parser = api.parser()
    with_content_parser.add_argument('user_id', help='The user identifier', required=True)
    with_content_parser.add_argument('text', help='image content text', required=True)

    @api.doc('get_user_image_list_by_image_content_text')
    @api.expect(with_content_parser, validate=True)
    @api.marshal_list_with(user_image)
    @token_required
    def get(self):
        '''get user image list by user_id and image content text'''
        result = docscare_db.userImages.find({'user_id': request.args.get('user_id'), 'image_text': {
            '$regex': request.args.get('text')
        }})

        if result is None:
            abort(400, 'Image Not Found')

        return result

    @api.doc('post_image', body=insert_user_image, parser=default_parser)
    @api.marshal_with(user_image, code=201, description='Success User Image Insert')
    @token_required
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

        # docscare_db.userImages.insert()
        return 'asd'


@api.route('/<image_id>')
@api.param('image_id', 'The image identifier')
class ImageWithImageId(Resource):
    @api.doc('get_user_image')
    @api.marshal_with(user_image)
    @token_required
    def get(self, image_id):
        '''get single image by image_id'''
        result = docscare_db.userImages.find_one({'_id': ObjectId(image_id)})

        if result is None:
            abort(400, 'Image Not Found')

        return 'asd', 200

    @api.doc('delete_user_image')
    @api.response(200, 'Deleted User')
    @token_required
    def delete(self, image_id):
        '''delete image by image_id'''
        result = docscare_db.userImages.delete_one({'_id': ObjectId(image_id)})

        if result.deleted_count == 0:
            abort(400, 'Image Not Found')

        return 'Deleted User', 200


@api.route('/<category_id>')
@api.param('category_id', 'The category identifier')
class ImageWithCategoryId(Resource):
    default_parser = api.parser()
    default_parser.add_argument('user_id', help='The user identifier', required=True)

    @api.doc('get_user_image_list_by_category_id')
    @api.expect(default_parser, validate=True)
    @api.marshal_list_with(user_image)
    @token_required
    def get(self, category_id):
        '''get user image list by by user_id and category_id'''
        result = None

        if category_id == 'all':
            result = docscare_db.userImages.find({'user_id': request.args.get('user_id')})
        else:
            result = docscare_db.userImages.find({'user_id': request.args.get('user_id'), 'category_id': category_id})

        if result is None:
            abort(400, 'Image Not Found')

        return result


@api.route('/<image_id>/<category_id>')
@api.param('image_id', 'The image identifier')
@api.param('category_id', 'Category id to modify')
class ImageWithImageIdAndCategoryId(Resource):
    default_parser = api.parser()
    default_parser.add_argument('user_id', help='The user identifier', required=True)

    @api.doc('update_user_image_category')
    @api.marshal_with(user_image)
    @token_required
    def put(self, image_id, category_id):
        '''update user image category by image_id and category_id'''
        result = docscare_db.userImages.update_one({
            '_id': ObjectId(image_id)
        }, {
            '$set': {'category_id': category_id}
        })

        if result.matched_count == 0:
            abort(400, 'Image Not Found')

        return 'Updated User Image Category', 200
