package com.sw926.mediastoreutilsdemo

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.airbnb.epoxy.CallbackProp
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.bumptech.glide.Glide
import com.sw926.mediastoreutils.MediaItem
import kotlinx.android.synthetic.main.item_media_image.view.*
import java.io.File

/**
 * Created by sunwei on 2020/8/5.
 */

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class ItemImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    @ModelProp
    @JvmField
    var images: List<MediaItem>? = null

    init {
        View.inflate(context, R.layout.item_media_image, this)
    }

    @CallbackProp
    fun setOnItemClickListener(onClickListener: OnClickListener?) {
        setOnClickListener(onClickListener)
    }

    @ModelProp
    fun setImageUri(uri: Uri?) {
        Glide.with(this)
            .load(uri)
            .into(ivImage)
    }

    @ModelProp
    fun setImageFilePath(imagePath: String?) {
        if (imagePath != null) {
            Glide.with(this)
                .load(Uri.fromFile(File(imagePath)))
                .into(ivImage)
        }
    }

    @ModelProp
    fun setPath(path: String?) {
        tvPath.text = path.toString()
    }

}