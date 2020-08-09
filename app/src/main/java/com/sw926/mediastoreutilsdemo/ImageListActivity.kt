package com.sw926.mediastoreutilsdemo

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sw926.mediastoreutils.MediaItem
import com.sw926.mediastoreutils.MediaStoreUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_image_list.*
import org.jetbrains.anko.selector
import java.io.File
import java.util.*

class ImageListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_list)

        val list = intent.getParcelableArrayListExtra<MediaItem>("image_list")

        rvImageList.withModels {
            list?.forEach {
                itemImageView {
                    id(it.mediaId)
                    mediaItem(it)
                    path("${it.mineType}\n${it.path}\n${Date(it.dateAdded * 1000)}\n${it.width}x${it.height}\n${it.duration / 1000}s\nsize=${it.size}")
                    imageUri(it.contentUri)
//                    imageFilePath(it.path)

                    onItemClickListener { model, _, _, _ ->
                        selector(items = listOf("delete", "move to trash", "share"), onClick = { _, i ->
                            if (i == 0) {
//                                model.imageUri()?.let { it1 -> MediaStoreUtils.deleteImage(this@ImageListActivity, it1) }

                                model.mediaItem()?.path?.let {
                                    val file = File(it)
                                    file.delete()
                                }


                            } else if (i == 2) {
                                shareToMore(this@ImageListActivity, "title", "content", imageUri = model.imageUri())
                            }
                        })
                    }
                }
            }
        }
    }

    @SuppressLint("CheckResult")
    fun shareToMore(
        context: Context,
        title: String?,
        content: String?,
        imageUri: Uri?
    ) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "image/*"
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
        content?.let {
            shareIntent.putExtra(Intent.EXTRA_TEXT, it)
        }
        title?.let {
            shareIntent.putExtra(Intent.EXTRA_TITLE, it)
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, it)
        }
        try {
            context.startActivity(shareIntent)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }
}