package com.sw926.mediastoreutils

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import java.io.File
import java.util.*

/**
 * Created by sunwei on 2020/8/5.
 */

abstract class BaseMediaLoader : IMediaLoader {
    companion object {

        //        private val QUERY_URI = MediaStore.Files.getContentUri("external")
        private val QUERY_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

    }

    private var orderBy: String? = null
    private var excludeTypes = mutableSetOf<String>()

    @Suppress("unused")
    fun orderBy(columnName: String, isDesc: Boolean = false) {
        orderBy = if (isDesc) "$columnName DESC" else columnName
    }

    @Suppress("unused")
    fun excludeType(type: String) {
        excludeTypes.add(type)
    }

    override fun load(context: Context): List<MediaFolder> {
        val all = doQuery(context)
        val result = all.groupBy {
            File(it.path).parentFile?.absolutePath
        }.map {
            val file = File(it.key)
            MediaFolder(file.name, file.absolutePath, it.value.first().path, it.value.first().contentUri, it.value.count(), it.value)
        }.toMutableList()

        if (all.isNotEmpty()) {
            result.add(
                0, MediaFolder(
                    "all",
                    "",
                    all.first().path,
                    all.first().contentUri,
                    all.count(),
                    all
                )
            )
        }
        return result
    }

    abstract fun createQuery(context: Context): Cursor?


    private fun doQuery(context: Context): List<MediaItem> {
        return createQuery(context)
            .use { cursor ->
                if (cursor != null) {
                    generateSequence { if (cursor.moveToNext()) cursor else null }
                        .map {
                            val mediaId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                            MediaItem(
                                mediaId = mediaId,
                                path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)),
                                contentUri = ContentUris.withAppendedId(QUERY_URI, mediaId),
                                width = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.WIDTH)),
                                height = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.HEIGHT)),
                                dateAdded = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_ADDED)),
                                dateModified = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED)),
                                mineType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE)),
                                size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE)),
                                duration = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DURATION))
                                } else 0
                            )
                        }.toList()
                } else {
                    listOf()
                }
            }
    }


}