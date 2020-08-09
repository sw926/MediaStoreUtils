package com.sw926.mediastoreutils

import android.content.Context
import android.database.Cursor
import android.os.Build
import android.provider.MediaStore

/**
 * Created by sunwei on 2020/8/5.
 */

class ImageLoader internal constructor() : BaseMediaLoader() {

    override fun createQuery(context: Context): Cursor? {
        val selection = "${MediaStore.MediaColumns.SIZE}>0"
        return context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            ProjectionHelper.projection,
            selection,
            null,
            null
        )
    }

}