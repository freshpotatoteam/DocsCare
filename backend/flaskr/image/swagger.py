from flask_restplus import Namespace, fields

api = Namespace('images', description='이미지 관련 api')

user_image = api.model("User_Image", {
    'category_name': fields.String(required=True, description='category name'),
    'category_included_image': fields.List(required=True, description='image Object Id', cls_or_instance=fields.String)
})

image = api.model('Image', {
    'id': fields.String(required=True, description='Image id'),
    'name': fields.String(required=True, description='The image name'),
})

Images = [
    {'id': 'felix', 'name': 'Felix'},
]