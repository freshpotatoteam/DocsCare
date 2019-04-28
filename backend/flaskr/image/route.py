import boto3
from flask import request, abort
from flask_restplus import Resource
from werkzeug.utils import secure_filename

import backend.settings as settings
from backend.flaskr.image.swagger import api, image

s3 = boto3.resource(
    's3',
    aws_access_key_id='AKIARTVYCVAX3ZTZHCPV',
    aws_secret_access_key='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'
)

ALLOWED_EXTENSIONS = set(['txt', 'pdf', 'png', 'jpg', 'jpeg', 'gif'])


@api.route('/<user_id>')
@api.param('user_id', 'The user identifier')
class Image(Resource):
    @api.doc('post_image')
    # @api.expect(insert_user_data)
    def post(self, user_id):
        '''create image by user_id and image_id '''
        if 'docs_image' not in request.files:
            abort(400, 'No docs_image key in request.files')

        file = request.files['docs_image']

        if file.filename == "":
            return abort(400, 'Please select a file')

        if file and allowed_file(file.filename):
            file.filename = secure_filename(file.filename)
            output = upload_file_to_s3(file, settings.S3_BUCKET)
            print(output)

        # mongo.db.image.insert()

        return 'asd'


@api.route('/<id>')
@api.param('id', 'The image identifier')
@api.response(404, 'Cat not found')
class Cat(Resource):
    @api.doc('get_cat')
    @api.marshal_with(image)
    def get(self, id):
        '''Fetch a image given its identifier'''
        api.abort(404)


def upload_file_to_s3(file, bucket_name, acl='public-read'):
    try:
        s3.Bucket(bucket_name).put_object(Key=file.filename, Body=file)
    except Exception as e:
        print('Something Happened: ', e)
        return e

    return '{}{}'.format(settings.S3_LOCATION, file.filename)


def allowed_file(filename):
    return '.' in filename and \
           filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS
