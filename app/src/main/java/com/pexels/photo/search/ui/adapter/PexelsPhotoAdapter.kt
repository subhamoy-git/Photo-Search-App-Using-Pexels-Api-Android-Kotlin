package com.pexels.photo.search.ui.adapter

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.pexels.photo.search.R
import com.pexels.photo.search.model.PexelsImageWrapper
import com.pexels.photo.search.ui.FullScreenPhotoActivity
import java.util.*

/**
 * Show photos from pexels api with photographer name
 * Move to next screen for showing full screen image
 */
class PexelsPhotoAdapter(private val context: Context, private val dataSet: ArrayList<PexelsImageWrapper>) :
    RecyclerView.Adapter<PexelsPhotoAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView
        var imageView: ImageView
        var progress: ProgressBar

        init {
            textView = view.findViewById(R.id.textView)
            imageView = itemView.findViewById(R.id.imageViewItem)
            progress = itemView.findViewById(R.id.progress)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.inflate, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.textView.text = dataSet[position].photographer.toString()
        Glide.with(context)
            .load(dataSet[position].mediumUrl)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(p0: GlideException?, p1: Any?, p2: Target<Drawable>?, p3: Boolean): Boolean {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
                override fun onResourceReady(p0: Drawable?, p1: Any?, p2: Target<Drawable>?, p3: DataSource?, p4: Boolean): Boolean {
                    //do something when picture already loaded
                    viewHolder.progress.visibility = View.GONE

                    return false
                }
            })
            .into(viewHolder.imageView)

        viewHolder.imageView.setOnClickListener(View.OnClickListener {
            val value: String = dataSet[position].originalUrl.toString()
            val intent = Intent(context, FullScreenPhotoActivity::class.java)
            intent.putExtra("originalUrl", value)
            context.startActivity(intent)
        })
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}
