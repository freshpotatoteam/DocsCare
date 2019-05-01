import boto3

s3 = boto3.resource(
    's3',
    aws_access_key_id='AKIARTVYCVAX3ZTZHCPV',
    aws_secret_access_key='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'
)

ALLOWED_EXTENSIONS = set(['txt', 'pdf', 'png', 'jpg', 'jpeg', 'gif'])


def upload_file_to_s3(file, location, bucket_name, acl='public-read'):
    try:
        s3.Bucket(bucket_name).put_object(Key=file.filename, Body=file)
    except Exception as e:
        print('Something Happened: ', e)
        return e

    return '{}{}'.format(location, file.filename)


def allowed_file(filename):
    return '.' in filename and \
           filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

def make_thumbnail_image(filename):
    return '.' in filename and \
           filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

