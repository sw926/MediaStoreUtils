package com.sw926.mediastoreutils

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.time.Duration

/**
 * Created by sunwei on 2020/8/5.
 */
@Parcelize
data class MediaItem(
    val mediaId: Long,
    val path: String,
    val contentUri: Uri,
    val width: Int,
    val height: Int,
    val dateAdded: Long,
    val dateModified: Long,
    val mineType: String,
    val size: Long,
    val duration: Long
) : Parcelable