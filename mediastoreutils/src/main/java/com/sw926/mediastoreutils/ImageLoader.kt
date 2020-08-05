package com.sw926.mediastoreutils

import android.net.Uri
import android.provider.MediaStore

/**
 * Created by sunwei on 2020/8/5.
 */

class ImageLoader internal constructor() : BaseMediaLoader() {

    override val contentUri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
}