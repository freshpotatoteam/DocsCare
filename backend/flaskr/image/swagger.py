from flask_restplus import Namespace, fields

api = Namespace('images', description='이미지 관련 api')

user_image = api.model('User_Image', {
    'user_id': fields.String(required=True, description='user id'),
    'image_name': fields.String(required=True, description='image name(file name)'),
    'image_text': fields.String(required=True, description='image text'),
    'image_thumbnail_url': fields.String(required=True, description='image thumbnail url'),
    'image_url': fields.String(required=True, description='image url'),
    'category_id': fields.String(required=True, description='category_id'),
    'insert_datetime': fields.String(required=True, description='image inserted time'),
    'update_datetime': fields.String(required=True, description='image updated time')
})

insert_user_image = api.model('Insert_User_Image', {
    'image_name': fields.String(required=False, description='image name(file name)'),
    'category_id': fields.String(required=False, description='category id'),
})
