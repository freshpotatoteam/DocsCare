from flask_restplus import Namespace, fields

api = Namespace('categories', description='유저 카테고리 관련 api')

user_category_item = api.model("User_Category", {
    'category_name': fields.String(required=True, description='category name'),
    'category_id': fields.String(required=True, description='category id'),
})
#
# user_category_list = api.model('User_Category_List', [])