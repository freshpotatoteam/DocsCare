import io
import os
import urllib

import boto3
import cv2
import numpy as np
from PIL import Image
from pdf2image import convert_from_path
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
        if file.filename.rsplit('.', 1)[1].lower() == 'pdf':
            s3.Bucket(bucket_name).put_object(Key=folder + '/' + file.filename, Body=file,
                                              ContentType='application/pdf')
        else:
            s3.Bucket(bucket_name).put_object(Key=folder + '/' + file.filename, Body=file,
                                              ContentType='image/' + file.filename.rsplit('.', 1)[1].lower())
    except Exception as e:
        print('Something Happened: ', e)
        return e

    return '{}{}'.format(location, file.filename)


def upload_thumbnail_file_to_s3(byte, filename, folder, location, bucket_name, acl='public-read'):
    try:
        s3.Bucket(bucket_name).put_object(Key=folder + '/' + filename, Body=byte,
                                          ContentType='image/jpeg')
    except Exception as e:
        print('Something Happened: ', e)
        return e

    return '{}{}'.format(location, filename)


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

    print('Recongizeing...')
    rec_string = image_to_string(cao, lang='eng+kor')
    return rec_string


def process_pdf(path=None):
    pages = convert_from_path(path, 200)
    rec_string = ''
    for index, page in enumerate(pages[:2]):
        in_mem_file = io.BytesIO()
        page.save(in_mem_file, 'JPEG')

        image = np.asarray(bytearray(in_mem_file.getvalue()), dtype="uint8")
        image = cv2.imdecode(image, cv2.IMREAD_COLOR)

        gray = cv2.cvtColor(image, cv2.COLOR_RGB2GRAY)
        ret2, th2 = cv2.threshold(gray, 0, 255, cv2.THRESH_BINARY + cv2.THRESH_OTSU)

        dst = cv2.fastNlMeansDenoising(th2, 10, 10, 7)

        cv2.imwrite(TEMP_IMAGE_PATH, dst)
        cao = Image.open(TEMP_IMAGE_PATH)

        os.remove(TEMP_IMAGE_PATH)
        print('{}st image Recongizeing...'.format(index + 1))
        rec_string += image_to_string(cao, lang='eng+kor') + '\n'

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
    filename, ext = os.path.splitext(file.filename)
    image = Image.open(file)

    thumbnail_size = 128, 128
    image.thumbnail(thumbnail_size)

    in_mem_file = io.BytesIO()
    image.save(in_mem_file, 'JPEG')
    in_mem_file.filename = filename + '.thumbnail'
    in_mem_file.seek(0)
    return in_mem_file.getvalue(), in_mem_file.filename


def make_thumbnail_pdf(file, file_path):
    filename, ext = os.path.splitext(file.filename)
    pages = convert_from_path(file_path, 200)
    image = pages[0]

    thumbnail_size = 128, 128
    image.thumbnail(thumbnail_size)
    in_mem_file = io.BytesIO()

    image.save(in_mem_file, 'JPEG')
    in_mem_file.filename = filename + '.thumbnail'
    in_mem_file.seek(0)
    return in_mem_file.getvalue(), in_mem_file.filename


def chomp(str):
    str = str.replace("\r", "")
    str = str.replace("\n", "")
    return str


def classifi_category_by_image_string(str):
    return str
