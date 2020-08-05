package com.sw926.mediastoreutils

/**
 * Created by sunwei on 2020/8/5.
 */

object MediaStoreUtils {

    fun loadImages(): ImageLoader {
        return ImageLoader()
    }

    fun loadVideos(): VideoLoader {
        return VideoLoader()
    }

}