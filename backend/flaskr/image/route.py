from datetime import datetime

from bson.objectid import ObjectId
from flask import request, abort
from flask_restplus import Resource
from werkzeug.utils import secure_filename

import settings as settings
from app import docscare_db
from flaskr.image.service import *
from flaskr.image.swagger import api, user_image
from flaskr.util.token_utils import token_required

file_name_prefix = '새 파일'


@api.route('')
class Image(Resource):
    default_parser = api.parser()
    default_parser.add_argument('user_id', help='The user identifier', required=True)

    with_content_parser = api.parser()
    with_content_parser.add_argument('user_id', help='The user identifier', required=True)
    with_content_parser.add_argument('text', help='image content text', required=True)

    post_parser = api.parser()
    post_parser.add_argument('user_id', help='The user identifier', required=True)
    post_parser.add_argument('docs_image', location='files', required=True)
    post_parser.add_argument('image_name', location='form')

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

    @api.doc('post_image')
    @api.expect(post_parser, validate=True)
    @api.marshal_with(user_image, code=201, description='Success User Image Insert')
    @token_required
    def post(self):
        '''create image by user_id'''
        if 'docs_image' not in request.files:
            abort(400, 'No docs_image key in request.files')

        file = request.files['docs_image']

        if file.filename == '':
            return abort(400, 'Please select a file')

        try:
            if file and allowed_file(file.filename):
                now = datetime.now().strftime('%Y-%m-%d %H:%M:%S')

                file.filename = secure_filename(file.filename)

                try:
                    image_name = request.form['image_name'] or '{} {}'.format(file_name_prefix, now)
                except Exception as e:
                    image_name = '{} {}'.format(file_name_prefix, now)

                file.save(os.path.join(settings.UPLOAD_FOLDER, file.filename))
                path = "./upload/{}".format(file.filename)
                print("file was uploaded in {} ".format(path))
                rec_string = process_image(path=path)
                file.seek(0, 0)
                source_image_output = upload_file_to_s3(file, settings.S3_SOURCE_FOLDER,
                                                        settings.S3_SOURCE_IMAGE_LOCATION, settings.S3_BUCKET)

                thumbnail_image_file = make_thumbnail_image(file)
                thumbnail_image_file.seek(0, 0)
                thumbnail_image_output = upload_file_to_s3(thumbnail_image_file, settings.S3_THUMBNAIL_FOLDER,
                                                           settings.S3_THUMBNAIL_IMAGE_LOCATION, settings.S3_BUCKET)

                now = datetime.now().strftime("%Y-%m-%d %H:%M:%S")

                # TODO 분류기를 이용한 카테고리 분류
                category_id = classifi_category_by_image_string(rec_string)

                user_image_document = {
                    'user_id': request.args.get('user_id'),
                    'image_text': chomp(rec_string),
                    'image_url': source_image_output,
                    'image_thumbnail_url': thumbnail_image_output,
                    'image_name': image_name,
                    'category_id': 'C1',
                    'insert_datetime': now,
                    'update_datetime': now,
                }
                docscare_db.userImages.insert_one(user_image_document)
                os.remove(path)
        except Exception as e:
            abort(500, 'Failed user image insert, {}'.format(e))

        return user_image_document, 200


@api.route('/<image_id>')
@api.param('image_id', 'The image identifier')
class ImageWithImageId(Resource):
    with_image_name_parser = api.parser()
    with_image_name_parser.add_argument('image_name', help='Image Name To Update', required=True)

    @api.doc('get_user_image')
    @api.marshal_with(user_image)
    @token_required
    def get(self, image_id):
        '''get single image by image_id'''
        result = docscare_db.userImages.find_one({'_id': ObjectId(image_id)})

        if result is None:
            abort(400, 'Image Not Found')

        return result, 200

    @api.doc('update_user_image_name')
    @api.expect(with_image_name_parser, validate=True)
    @token_required
    def put(self, image_id):
        '''update user image name by image_id'''
        result = docscare_db.userImages.update_one({
            '_id': ObjectId(image_id)
        }, {
            '$set': {'image_name': request.args.get('image_name')}
        })

        if result.matched_count == 0:
            abort(400, 'Image Not Found')

        return 'Updated User Image Name', 200

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
        '''get user image list by by user_id and category_id(Do category_id transfer 'all' for all category)'''
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
