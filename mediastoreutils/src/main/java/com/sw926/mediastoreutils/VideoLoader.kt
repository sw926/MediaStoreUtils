package com.sw926.mediastoreutils

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore

/**
 * Created by sunwei on 2020/8/5.
 */
class VideoLoader internal constructor() : BaseMediaLoader() {
    override fun createQuery(context: Context): Cursor? {
        return null
    }

}