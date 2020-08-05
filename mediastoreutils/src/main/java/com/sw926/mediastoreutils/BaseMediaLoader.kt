package com.sw926.mediastoreutils

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import java.io.File

/**
 * Created by sunwei on 2020/8/5.
 */

abstract class BaseMediaLoader : IMediaLoader {
    companion object {

        private val PROJECTION = arrayOf(
            MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.WIDTH,
            MediaStore.MediaColumns.HEIGHT,
            MediaStore.MediaColumns.DATE_ADDED,
            MediaStore.MediaColumns.DATE_MODIFIED,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.SIZE,
            MediaStore.MediaColumns.DURATION
        )
    }


    abstract val contentUri: Uri

    private var orderBy: String? = null
    private var excludeTypes = mutableSetOf<String>()

    fun orderBy(columnName: String, isDesc: Boolean = false): String {
        return if (isDesc) "$columnName DESC" else columnName
    }

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
        return result
    }

    private fun doQuery(context: Context): List<MediaItem> {
        val uri = contentUri
        var selection = "${MediaStore.MediaColumns.SIZE}>0"
        excludeTypes.forEach {
            selection = "$selection AND ${MediaStore.MediaColumns.MIME_TYPE}!='image/${it}'"
        }

        return context.contentResolver.query(uri, PROJECTION, selection, null, orderBy)
            .use { cursor ->
                if (cursor != null) {
                    generateSequence { if (cursor.moveToNext()) cursor else null }
                        .map {
                            val mediaId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                            MediaItem(
                                mediaId = mediaId,
                                path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)),
                                contentUri = ContentUris.withAppendedId(uri, mediaId),
                                width = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.WIDTH)),
                                height = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.HEIGHT)),
                                dateAdded = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_ADDED)),
                                dateModified = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED)),
                                mineType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE)),
                                size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE)),
                                duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DURATION))
                            )
                        }.toList()
                } else {
                    listOf()
                }
            }
    }


}