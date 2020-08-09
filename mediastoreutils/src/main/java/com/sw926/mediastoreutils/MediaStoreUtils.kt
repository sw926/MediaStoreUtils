package com.sw926.mediastoreutils

import android.app.Activity
import android.app.Fragment
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.OutputStream
import kotlin.concurrent.fixedRateTimer

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

    fun scan(context: Context) {
        MediaScannerConnection.scanFile(context, arrayOf(Environment.getExternalStorageDirectory().path), null, MediaScannerConnection.OnScanCompletedListener { s, uri ->

        })
    }

    fun deleteImage(context: Context, imageUri: Uri) {
        val intent = MediaStore.createDeleteRequest(context.contentResolver, listOf(imageUri))
        context.startIntentSender(intent.intentSender, null, 0, 0, 0)

    }


    fun deleteImage(context: Fragment, imageUri: Uri) {
//        val intent = MediaStore.createDeleteRequest(context.contentResolver, listOf(imageUri))
//        context.startIntentSender(intent.intentSender, null, 0, 0, 0)

    }

    fun insertImageBitmap(
        context: Context,
        bitmap: Bitmap,
        saveFormat: Bitmap.CompressFormat,
        displayName: String,
        mimeType: String,
        quality: Int,
        relativePath: String? = null,
        title: String? = null,
        description: String? = null
    ) {
        inertImageAndroidQ(
            context = context,
            displayName = displayName,
            mimeType = mimeType,
            width = bitmap.width,
            height = bitmap.height,
            title = title,
            description = description,
            relativePath = relativePath
        ) { output ->
            bitmap.compress(saveFormat, quality, output)
        }
    }

    fun insertImageFile(
        context: Context,
        file: String,
        relativePath: String? = null,
        displayName: String,
        mimeType: String,
        title: String? = null,
        description: String? = null
    ) {
        val option = BitmapFactory.Options()
        option.inJustDecodeBounds = true
        BitmapFactory.decodeFile(file, option)

        inertImageAndroidQ(
            context = context,
            displayName = displayName,
            mimeType = mimeType,
            width = option.outWidth,
            height = option.outHeight,
            title = title,
            description = description,
            relativePath = relativePath
        ) { output ->
            File(file).inputStream().use { input ->
                input.copyTo(output)
            }
        }
    }

    private fun inertImageAndroidQ(
        context: Context,
        displayName: String,
        mimeType: String,
        width: Int,
        height: Int,
        title: String?,
        description: String?,
        relativePath: String?,
        block: (OutputStream) -> Unit
    ) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            val sdcard: File = Environment.getExternalStorageDirectory()
            val mediaDir = File(sdcard, "DCIM/Camera")
            if (!mediaDir.exists()) {
                mediaDir.mkdirs()
            }
        }

        val values = createContentValues(displayName, mimeType, width, height, title, description, relativePath)
        val resolver = context.contentResolver
        val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val items = resolver.insert(collection, values)

        items?.let {
            resolver.openOutputStream(items)?.use { output ->
                block.invoke(output)
            }
            values.clear()

            resolver.openFileDescriptor(it, "r")?.use { pfd ->
                values.put(MediaStore.Images.Media.SIZE, pfd.statSize)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.put(MediaStore.Images.Media.IS_PENDING, 0)
            }

            if (values.size() > 0) {
                resolver.update(items, values, null, null)
            }
        }
    }

    private fun createContentValues(
        displayName: String,
        mimeType: String,
        width: Int,
        height: Int,
        title: String?,
        description: String?,
        relativePath: String?
    ): ContentValues {
        return ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, displayName)
            put(MediaStore.Images.Media.MIME_TYPE, mimeType)
            put(MediaStore.Images.Media.DESCRIPTION, description)
            put(MediaStore.Images.Media.TITLE, title)
            put(MediaStore.Images.Media.WIDTH, width)
            put(MediaStore.Images.Media.HEIGHT, height)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.IS_PENDING, 1)
                put(MediaStore.Images.Media.RELATIVE_PATH, relativePath)
            }
        }
    }
}