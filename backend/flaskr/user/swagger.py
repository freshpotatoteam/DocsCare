from flask_restplus import Namespace, fields

api = Namespace('users', description='회원 관련 api')

user_category = api.model("Category", {
    'category_name': fields.String(required=True, description='category_name'),
    'category_included_image': fields.List(required=True, description='image Object Id', cls_or_instance=fields.String)
})

user = api.model('User', {
    'user_id': fields.String(required=True, description='user_id'),
    'nickname': fields.String(required=True, description='nickname'),
    'profile_image_path': fields.String(required=True, description='profile_image_path'),
    'thumbnail_image_path': fields.String(required=True, description='thumbnail_image_path'),
    'categories': fields.List(required=True, description='user category list', cls_or_instance=fields.Nested(user_category))
})

insert_user_data = api.model('Insert_User_Data', {
    'nickname': fields.String(required=True, description='nickname'),
    'profile_image_path': fields.String(required=True, description='profile_image_path'),
    'thumbnail_image_path': fields.String(required=True, description='thumbnail_image_path'),
})