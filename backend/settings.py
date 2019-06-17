# Swagger의 기본 형식을 List로하는
SWAGGER_UI_DOC_EXPANSION  = 'list'
# Validation 활성화
RESTPLUS_VALIDATE  =  True

# DB
MONGO_HOST = 'cluster0-shard-00-00-e26oa.mongodb.net'
MONGO_PORT = 27017
MONGO_DB = 'docscare'
MONGO_USER = 'docscare'
MONGO_PASS = 'docscare'

# S3
S3_BUCKET = 'docscare'
S3_SOURCE_FOLDER = 'source'
S3_THUMBNAIL_FOLDER = 'thumbnail'
CLOUD_FRONT_SOURCE_IMAGE_LOCATION = 'https://d2gtrs9h7ipjtf.cloudfront.net/{}/'.format(S3_SOURCE_FOLDER)
CLOUD_FRONT_THUMBNAIL_IMAGE_LOCATION = 'https://d2gtrs9h7ipjtf.cloudfront.net/{}/'.format(S3_THUMBNAIL_FOLDER)

API_KEY = 'c30d6e11-67cc-4643-8e20-b2cdff2799ef'

UPLOAD_FOLDER = './upload'