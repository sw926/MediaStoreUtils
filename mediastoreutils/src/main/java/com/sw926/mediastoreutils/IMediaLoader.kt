package com.sw926.mediastoreutils

import android.content.Context

/**
 * Created by sunwei on 2020/8/5.
 */

interface IMediaLoader {

    fun load(context: Context): List<MediaFolder>

}