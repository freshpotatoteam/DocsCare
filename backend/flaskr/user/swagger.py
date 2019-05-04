from flask_restplus import Namespace, fields

api = Namespace('users', description='회원 관련 api')

user_image_item = api.model("User_Image_Item", {
    'category_name': fields.String(required=True, description='category name'),
    'category_included_image': fields.List(required=True, description='image Object Id', cls_or_instance=fields.String)
})

user = api.model('User', {
    'user_id': fields.String(required=True, description='user id'),
    'nickname': fields.String(required=True, description='nickname'),
    'profile_image_path': fields.String(required=True, description='profile image path'),
    'thumbnail_image_path': fields.String(required=True, description='thumbnail image path'),
    'images': fields.List(required=True, description='user category list', cls_or_instance=fields.Nested(user_image_item))
})

insert_user = api.model('Insert_User', {
    'nickname': fields.String(required=True, description='nickname'),
    'profile_image_path': fields.String(required=True, description='profile image path'),
    'thumbnail_image_path': fields.String(required=True, description='thumbnail image path'),
})