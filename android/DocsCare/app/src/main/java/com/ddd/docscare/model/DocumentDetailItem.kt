package com.ddd.docscare.model

import android.graphics.Bitmap

data class DocumentDetailItem(var bitmap: Bitmap? = null,
                              var position: Int = 0,
                              val title: String = "")