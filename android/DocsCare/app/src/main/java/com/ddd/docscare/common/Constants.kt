package com.ddd.docscare.common

import android.os.Environment
import com.ddd.docscare.R

const val BASE_URL = "http://15.164.143.16"
const val SELECTED_BITMAP = "SELECTED_BITMAP"
const val PROVIDER = "com.ddd.docscare.provider"

const val START_CAMERA_REQUEST_CODE = 888
var IMAGE_FOLDER_PATH = "${Environment.getExternalStorageDirectory().path}/scanSample"
var DOCS_FOLDER_PATH = "${Environment.getExternalStorageDirectory().path}/DocsCare"


var DEFAULT_FOLDER_LIST = listOf(
    Triple("${Environment.getExternalStorageDirectory().path}/DocsCare/Career", "커리어", R.drawable.career),
    Triple("${Environment.getExternalStorageDirectory().path}/DocsCare/Learning", "학업", R.drawable.learning),
    Triple("${Environment.getExternalStorageDirectory().path}/DocsCare/OfficialDocs", "공문서", R.drawable.officialdocs),
    Triple("${Environment.getExternalStorageDirectory().path}/DocsCare/Economy", "금융", R.drawable.economy),
    Triple("${Environment.getExternalStorageDirectory().path}/DocsCare/Travel", "여행", R.drawable.travel),
    Triple("${Environment.getExternalStorageDirectory().path}/DocsCare/RealEstate", "부동산", R.drawable.realestate)
)