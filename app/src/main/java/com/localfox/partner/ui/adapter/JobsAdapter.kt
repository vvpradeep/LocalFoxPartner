package com.localfox.partner.ui.adapter


import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.localfox.partner.R
import com.localfox.partner.app.AppUtils
import com.localfox.partner.app.LetterDrawable
import com.localfox.partner.app.MyApplication
import com.localfox.partner.entity.Jobs
import com.localfox.partner.ui.HomeActivity


class JobsAdapter(private var jobs : ArrayList<Jobs>, var context : Context, private val listener: OnItemClickListener, var isSearch: Boolean) : RecyclerView.Adapter<JobsAdapter.ViewHolder>() {
    private var filteredData: List<Jobs> = jobs
    private var lastposition : Int = 0

    fun filter(query: String) {
        filteredData = jobs.filter {
            it.customer!!.fullName!!.contains(query, ignoreCase = true) ||
            it.customer!!.mobileNumber!!.contains(query, ignoreCase = true) ||
            it.customer!!.emailAddress!!.contains(query, ignoreCase = true) ||
            it.location!!.formattedAddress!!.contains(query, ignoreCase = true)
        }
        notifyDataSetChanged()
    }

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_search, parent, false)


        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

//        val ItemsViewModel = mList[position]
//
//        // sets the image to the imageview from our itemHolder class
//        holder.imageView.setImageResource(ItemsViewModel.image)
//
//        // sets the text to the textview from our itemHolder class
        var address = filteredData!!.get(position).location
        if (filteredData!!.get(position).customer != null) {
            holder.nameTextView.text = filteredData!!.get(position).customer!!.fullName
            if (filteredData.get(position).customer!!.profilePhoto != null && !filteredData.get(position).customer!!.profilePhoto.toString().contains("no-photo.")) {
                Glide.with(context)
                    .load(filteredData.get(position).customer!!.profilePhoto)
                    .into(holder.imageView)
            } else if (filteredData.get(position).customer!!.fullName != null) {
                val parts = filteredData.get(position).customer!!.fullName!!.split(" ")
                var name: String? = null;
                if (parts.size > 1) {
                    name = parts[0]!!.get(0)+""+parts[1]!!.get(0)
                } else {
                    name = parts[0]!!.get(0)+""+parts[0]!!.get(1)
                }
                Glide.with(context)
                    .load(LetterDrawable(name, context.applicationContext))
                    .into(holder.imageView)
            }

        }
        var locationString =  address!!.streetNumber + " " +  address!!.streetName +" \n" +
                address!!.suburb + " " + address!!.state + " "+address!!.postCode
        holder.addressTextView.text = locationString.toString()
        holder.dateTextView.text =  MyApplication.applicationContext().formatDate(filteredData!!.get(position).createdDate)

        val activity = context as HomeActivity
        if (position == jobs.size - 1 && jobs.size % activity.pageSize  == 0) {
            lastposition = position
            listener.getNewPageData()
        }

        holder.itemView.setOnClickListener {
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(filteredData.get(position))
            }
        }

        holder.callFL.setOnClickListener {
            if (filteredData!!.get(position).customer!= null && filteredData!!.get(position).customer!!.mobileNumber != null)
                listener.onCallClick(filteredData!!.get(position).customer!!.mobileNumber!!)
        }
        holder.mailFL.setOnClickListener {
            if (filteredData!!.get(position).customer!= null && filteredData!!.get(position).customer!!.emailAddress != null)
                listener.onMailClick(filteredData!!.get(position).customer!!.emailAddress!!)
        }
        holder.locationFL.setOnClickListener {
            if (filteredData!!.get(position).location!= null && filteredData!!.get(position).location!!.coordinates != null) {
                val coordinates: ArrayList<Double> =
                    filteredData!!.get(position).location!!.coordinates;
                listener.onLocationClick("", coordinates.get(0), coordinates.get(1))
            }
        }

        if (isSearch) {
            holder.socialMediaLL.visibility = View.GONE
        }

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return filteredData!!.size
    }

    public fun setData( jobs1 : ArrayList<Jobs>) {
        if (jobs1 != null) {
            jobs.addAll(jobs1)
        } else {
            jobs = jobs1
        }
        filteredData = jobs;
    }

    interface OnItemClickListener {
        fun onItemClick(job: Jobs)
        fun getNewPageData()
        fun onCallClick(phoneNumber: String)
        fun onMailClick(mailID: String)
        fun onLocationClick(location: String, lat: Double, Long: Double)
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.profile_image)
        val nameTextView: TextView = itemView.findViewById(R.id.name_tv)
        val addressTextView: TextView = itemView.findViewById(R.id.address_tv)
        val dateTextView: TextView = itemView.findViewById(R.id.date_tv)
        val socialMediaLL: LinearLayout = itemView.findViewById(R.id.social_media_ll)
        val mailFL: FrameLayout = itemView.findViewById(R.id.mail_fl)
        val callFL: FrameLayout = itemView.findViewById(R.id.call_fl)
        val locationFL: FrameLayout = itemView.findViewById(R.id.location_fl)

    }

}