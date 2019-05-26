import os
import urllib

import boto3
import cv2
import numpy as np
from PIL import Image
from pytesseract import image_to_string

s3 = boto3.resource(
    's3',
    aws_access_key_id='AKIARTVYCVAX3ZTZHCPV',
    aws_secret_access_key='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'
)

ALLOWED_EXTENSIONS = set(['txt', 'pdf', 'png', 'jpg', 'jpeg', 'gif'])
TEMP_IMAGE_PATH = './upload/tmp.jpg'


def upload_file_to_s3(file, folder, location, bucket_name, acl='public-read'):
    try:
        # Key = folder name + file name
        s3.Bucket(bucket_name).put_object(Key=folder + '/' + file.filename, Body=file)
    except Exception as e:
        print('Something Happened: ', e)
        return e

    return '{}{}'.format(location, file.filename)


def process_image(url=None, path=None):
    image = None

    if url != None:
        image = url_to_image(url)
    elif path != None:
        image = cv2.imread(path)
    else:
        return "Wrong Wrong Wrong, What are you doing ??? "

    gray = cv2.cvtColor(image, cv2.COLOR_RGB2GRAY)
    ret2, th2 = cv2.threshold(gray, 0, 255, cv2.THRESH_BINARY + cv2.THRESH_OTSU)

    dst = cv2.fastNlMeansDenoising(th2, 10, 10, 7)

    cv2.imwrite(TEMP_IMAGE_PATH, dst)
    cao = Image.open(TEMP_IMAGE_PATH)

    os.remove(TEMP_IMAGE_PATH)

    print("Recongizeing...")
    rec_string = image_to_string(cao, lang='kor+eng')
    return rec_string


def url_to_image(url):
    resp = urllib.urlopen(url)
    print(resp)
    image = np.asarray(bytearray(resp.read()), dtype="uint8")
    image = cv2.imdecode(image, cv2.IMREAD_COLOR)
    return image


def allowed_file(filename):
    return '.' in filename and \
           filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS


def make_thumbnail_image(file):
    image = Image.open(file)
    thumbnail_image =  image.crop((0, 0, 200, 200))
    thumbnail_image.save(file.filename)
    return file


def chomp(str):
    str = str.replace("\r", "")
    str = str.replace("\n", "")
    return str
