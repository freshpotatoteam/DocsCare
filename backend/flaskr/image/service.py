import io
import os
import urllib

import boto3
import cv2
import nltk
import numpy as np
import settings as settings
from PIL import Image
from app import model
from konlpy.tag import Okt
from nltk.corpus import stopwords
from nltk.tokenize import word_tokenize
from pdf2image import convert_from_path
from pytesseract import image_to_string

s3 = boto3.resource(
    's3',
    aws_access_key_id=settings.ACCESS_KEY_ID,
    aws_secret_access_key=settings.SECRET_ACCESS_KEY
)

ALLOWED_EXTENSIONS = set(['txt', 'pdf', 'png', 'jpg', 'jpeg', 'gif'])
TEMP_IMAGE_PATH = './upload/tmp.jpg'

category_map = {
    '커리어': 'C1',
    '학업': 'C2',
    '금융': 'C3',
    '공공문서': 'C4',
    '여행': 'C5',
    '부동산': 'C6'
}

nltk.download('punkt')
nltk.download('stopwords')
ko_stop_words = set(stopwords.words('english'))
en_stop_words = set(stopwords.words('english'))
okt = Okt()

script_dir = os.path.dirname(__file__)  # <-- absolute dir the script is in
stopwordsFile = open(os.path.join(script_dir, '../stopword/ko_stopwords.txt'), 'r')
kr_stop_words = [data for data in stopwordsFile.read().split()]


def tokenizer(str, lang_type):
    result = []

    if lang_type == 'ko':
        word_tokens = okt.nouns(str)

        for w in word_tokens:
            if w not in kr_stop_words and len(w) >= 2:
                result.append(w)

        return result
    else:
        word_tokens = word_tokenize(str)

        for w in word_tokens:
            if w not in en_stop_words and len(w) >= 4:
                result.append(w.lower())

        return result


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
        rec_string += image_to_string(cao, lang='kor') + '\n'

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

    if image.mode in ('RGBA', 'LA'):
        background = Image.new(image.mode[:-1], image.size)
        background.paste(image, image.split()[-1])
        image = background

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
    inferred_vector = model.infer_vector(tokenizer(str, 'ko'))
    return category_map[model.docvecs.most_similar([inferred_vector], topn=len(model.docvecs))[0][0]]
