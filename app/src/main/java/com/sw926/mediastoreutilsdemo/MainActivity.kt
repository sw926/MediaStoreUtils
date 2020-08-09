package com.sw926.mediastoreutilsdemo

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import com.sw926.mediastoreutils.IMediaLoader
import com.sw926.mediastoreutils.MediaStoreUtils
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    private lateinit var rxPermissions: RxPermissions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rxPermissions = RxPermissions(this)

        btnLoadImages.setOnClickListener {
            loadMedia(MediaStoreUtils.loadImages())
//            rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE)
//                .subscribe({
//                    if (it) {
//                        loadMedia(MediaStoreUtils.loadImages())
//                    }
//                }, { it.printStackTrace() })
        }

        btnLoadVideos.setOnClickListener {
            rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe({
                    if (it) {
                        loadMedia(MediaStoreUtils.loadVideos())
                    }
                }, { it.printStackTrace() })
        }

        btnInsert.setOnClickListener {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe {
                    if (it) {
                        writeMediaFile()
                    }
                }
            } else {
                writeMediaFile()
            }
        }

        btnScan.setOnClickListener {
            MediaStoreUtils.scan(this)
        }
    }

    private fun writeMediaFile() {
        val file = File(cacheDir, "sample.jpg")
        if (file.exists()) {
            file.delete()
        }
        file.createNewFile()
        FileOutputStream(file).use { output ->
            assets.open("sample.jpg").use { input ->
                input.copyTo(output)
            }
        }

        MediaStoreUtils.insertImageFile(
            context = this, file = file.absolutePath, displayName = "sample_name",
            mimeType = "image/jpeg"
        )

//        MediaStoreUtils.insertImageBitmap(
//            context = this,
//            bitmap = BitmapFactory.decodeFile(file.absolutePath),
//            displayName = "sample_name",
//            mimeType = "image/jpeg",
//            quality = 100
//        )
    }

    private fun loadMedia(loader: IMediaLoader) {
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