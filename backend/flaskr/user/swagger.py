from flask_restplus import Namespace, fields

api = Namespace('users', description='회원 관련 api')

user = api.model('User', {
    'user_id': fields.String(required=True, description='user id'),
    'nickname': fields.String(required=True, description='nickname'),
    'profile_image_path': fields.String(required=True, description='profile image path'),
    'thumbnail_image_path': fields.String(required=True, description='thumbnail image path'),
})

insert_user = api.model('Insert_User', {
    'nickname': fields.String(required=True, description='nickname'),
    'profile_image_path': fields.String(required=True, description='profile image path'),
    'thumbnail_image_path': fields.String(required=True, description='thumbnail image path'),
})
