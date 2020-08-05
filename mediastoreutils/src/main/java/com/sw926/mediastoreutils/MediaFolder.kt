package com.sw926.mediastoreutils

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by sunwei on 2020/8/5.
 */
@Parcelize
data class MediaFolder(
    val name: String,
    val path: String,
    val coverPath: String,
    val coverContentUri: Uri,
    val mediaNumber: Int,
    val mediaItems: List<MediaItem>
) : Parcelable