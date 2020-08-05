package com.sw926.mediastoreutilsdemo

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sw926.mediastoreutils.IMediaLoader
import com.sw926.mediastoreutils.MediaFolder
import com.sw926.mediastoreutils.MediaStoreUtils
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var rxPermissions: RxPermissions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rxPermissions = RxPermissions(this)

        btnLoadImages.setOnClickListener {
            rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe({
                    if (it) {
                        loadImages(MediaStoreUtils.loadImages())
                    }
                }, { it.printStackTrace() })
        }

        btnLoadVideos.setOnClickListener {
            rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe({
                    if (it) {
                        loadImages(MediaStoreUtils.loadVideos())
                    }
                }, { it.printStackTrace() })
        }
    }


    private fun loadImages(loader: IMediaLoader) {
        val folders = loader.load(this)
        rvImages.withModels {
            folders.forEach {
                itemImageView {
                    id(it.path)
                    imageUri(it.coverContentUri)
                    path("${it.name}(${it.mediaNumber})")
                    images(it.mediaItems)
                    onItemClickListener { model, _, _, _ ->
                        val intent = Intent(this@MainActivity, ImageListActivity::class.java)
                        model.images()?.let {
                            intent.putParcelableArrayListExtra("image_list", ArrayList(it))
                        }
                        startActivity(intent)
                    }
                }
            }
        }
    }
}