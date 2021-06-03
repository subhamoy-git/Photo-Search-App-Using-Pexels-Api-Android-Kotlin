package com.pexels.photo.search.ui

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.pexels.photo.search.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

/**
 * Show photo to a full screen mode
 * User can also back to previous screen using device back button or using custom back button
 */
class FullScreenPhotoActivity : AppCompatActivity() {

    var originalUrl = String()
    lateinit var photoView: ImageView
    lateinit var back : Button
    lateinit var progress : ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_image)

        val intent = intent
        originalUrl = intent.getStringExtra("originalUrl").toString()
        photoView = findViewById(R.id.photoView)
        back = findViewById(R.id.back_button)
        progress = findViewById(R.id.progress)

        Glide.with(this)
            .load(originalUrl)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(p0: GlideException?, p1: Any?, p2: Target<Drawable>?, p3: Boolean): Boolean {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
                override fun onResourceReady(p0: Drawable?, p1: Any?, p2: Target<Drawable>?, p3: DataSource?, p4: Boolean): Boolean {
                    //do something when picture already loaded
                    progress.visibility = View.GONE

                    return false
                }
            })
            .into(photoView)

        back.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                finish()
            }

        })
    }

}