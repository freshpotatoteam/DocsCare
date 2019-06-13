package com.ddd.docscare.common

import android.os.Environment

const val BASE_URL = "http://15.164.143.16"
const val SELECTED_BITMAP = "SELECTED_BITMAP"
const val PROVIDER = "com.ddd.docscare.provider"

const val START_CAMERA_REQUEST_CODE = 888
var IMAGE_FOLDER_PATH = "${Environment.getExternalStorageDirectory().path}/scanSample"