package com.localfox.partner.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.localfox.partner.R
import com.localfox.partner.ui.ProfilePhotoActivity

class ImagesGridAdapter(context: Context, items: List<String>) : BaseAdapter() {
    private val context: Context
    private val items: List<String>

    init {
        this.context = context
        this.items = items
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): Any {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        if (convertView == null) {
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.grid_item, parent, false)
        } else {
            view = convertView
        }
        val imageView: ImageView = view.findViewById(R.id.profile_image)
        Glide.with(context)
            .load(items.get(position))
            .into(imageView)

        view.setOnClickListener {
            var intent = Intent(context, ProfilePhotoActivity::class.java)
            intent.putExtra("id", items.get(position))
            intent.putExtra("islocal", false)
            context.startActivity(intent)
        }

        return view
    }
}