package com.ddd.docscare.model

import android.graphics.Bitmap

data class DocumentDetailItem(var bitmap: Bitmap? = null,
                              var position: Int,
                              val type: Int,
                              val title: String = "")