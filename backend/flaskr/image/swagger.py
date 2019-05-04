from flask_restplus import Namespace, fields

api = Namespace('images', description='이미지 관련 api')

user_image = api.model('User_Image', {
    'user_id': fields.String(required=True, description='user id'),
    'image_id': fields.String(required=True, description='image id'),
    'image_thumbnail_url': fields.String(required=True, description='image thumbnail url'),
    'image_url': fields.String(required=True, description='image url'),
    'image_text': fields.String(required=True, description='image text'),
    'image_name': fields.String(required=True, description='image name(file name)'),
    'category_id': fields.String(required=True, description='category_id'),
    'image_insert_datetime': fields.String(required=True, description='image inserted time'),
    'image_update_datetime': fields.String(required=True, description='image updated time')
})

insert_user_image = api.model('Insert_User_Image', {
    'image_name': fields.String(required=False, description='image name(file name)'),
    'category_id': fields.String(required=False, description='category id'),
})
