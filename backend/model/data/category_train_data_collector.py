import os
import shutil

import cv2
from PIL import Image
from google_images_download import google_images_download
from pytesseract import image_to_string

import backend.db as db
import backend.settings as settings

mongo_url = 'mongodb://{}:{}@{}:{}/{}?authSource=admin&ssl=true&readPreference=secondary'.format(
    settings.MONGO_USER,
    settings.MONGO_PASS,
    settings.MONGO_HOST,
    settings.MONGO_PORT,
    settings.MONGO_DB)

mongo = db.init_app(mongo_url)
docscare_db = mongo['docscare']


# C1: 커리어
# C2: 학업
# C3: 금융
# C4: 공공문서
# C5: 여행
# C6: 부동산
search_queries_map = [{'C1': ['커리어', 'career']}, {'C2': ['학업']}, {'C3': ['금융']}, {'C4': ['공공문서']}, {'C6': ['여행']}, {'C7': ['부동산']}]
TEMP_IMAGE_PATH = './downloads/tmp.jpg'

# creating object
response = google_images_download.googleimagesdownload()

# Bulk image download
def downloadimages(query):
    # keywords is the search query
    # format is the image file format
    # limit is the number of images to be downloaded
    # print urs is to print the image file url
    # size is the image size which can
    # be specified manually ('large, medium, icon')
    # aspect ratio denotes the height width ratio
    # of images to download. ('tall, square, wide, panoramic')
    arguments = {'keywords': query,
                 'format': 'jpg',
                 'limit': 10,
                 'print_urls': True,
                 'size': 'medium',
                 'aspect_ratio': 'panoramic',
                 'chromedriver': 'chromedriver'}
    try:
        return response.download(arguments)

        # Handling File NotFound Error
    except FileNotFoundError:
        arguments = {'keywords': query,
                     'format': 'jpg',
                     'limit': 10,
                     'print_urls': True,
                     'size': 'medium',
                     'chromedriver': 'chromedriver'
                     }

        # Providing arguments for the searched query
        try:
            # Downloading the photos based
            # on the given arguments
            return response.download(arguments)
        except:
            pass

# image OCR 처리
def process_image(path=None):
    image = cv2.imread(path)
    gray = cv2.cvtColor(image, cv2.COLOR_RGB2GRAY)
    ret2, th2 = cv2.threshold(gray, 0, 255, cv2.THRESH_BINARY + cv2.THRESH_OTSU)

    dst = cv2.fastNlMeansDenoising(th2, 10, 10, 7)

    cv2.imwrite(TEMP_IMAGE_PATH, dst)
    cao = Image.open(TEMP_IMAGE_PATH)

    os.remove(TEMP_IMAGE_PATH)

    print('Recongizeing...')
    ko_rec_string = image_to_string(cao, lang='kor')
    en_rec_string = image_to_string(cao, lang='eng')
    return ko_rec_string, en_rec_string


# Driver Code
for query_map in search_queries_map:
    category_id = list(query_map.keys())[0]

    for query in query_map[category_id]:
        image_paths = downloadimages(query)
        for index, image_path in enumerate(image_paths[0][query]):
            print('[{}] - {}st image process'.format(query, index + 1))
            ko_rec_string, en_rec_string = process_image(path=image_path)

            result = ''

            if len(ko_rec_string) > 10:
                # Todo 한국어 토크나이저 및 전처리
                result += ko_rec_string

            if len(en_rec_string) > 10:
                # Todo 영어 토크나이저 및 전처리
                result += en_rec_string


            category_sample_data_document = {
                'category_id': category_id,

            }
            print(result, category_id)

            # docscare_db.categorySampleData.insert_one(category_sample_data_document)

        shutil.rmtree('./downloads/{}'.format(query))

    # result = docscare_db.users.find({})
