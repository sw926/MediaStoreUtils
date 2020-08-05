package com.sw926.mediastoreutilsdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sw926.mediastoreutils.MediaItem
import kotlinx.android.synthetic.main.activity_image_list.*
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
                    path("${it.mineType}\n${it.path}\n${Date(it.dateAdded * 1000)}\n${it.width}x${it.height}\n${it.duration / 1000}s")
                    imageUri(it.contentUri)
                }
            }
        }
    }
}