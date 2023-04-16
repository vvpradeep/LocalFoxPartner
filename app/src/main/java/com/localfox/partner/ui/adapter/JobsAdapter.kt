package com.localfox.partner.ui.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.localfox.partner.R
import com.localfox.partner.entity.Jobs
import com.localfox.partner.entity.JobsList

class JobsAdapter(private var jobs : ArrayList<Jobs>, var context : Context, private val listener: OnItemClickListener) : RecyclerView.Adapter<JobsAdapter.ViewHolder>() {
    private var filteredData: List<Jobs> = jobs
    private var lastposition : Int = 0

    fun filter(query: String) {
        filteredData = jobs.filter {
            it.customer!!.fullName!!.contains(query, ignoreCase = true) ||
            it.customer!!.mobileNumber!!.contains(query, ignoreCase = true) ||
            it.customer!!.emailAddress!!.contains(query, ignoreCase = true) ||
            it.location!!.suburb!!.contains(query, ignoreCase = true)
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
        holder.nameTextView.text =filteredData!!.get(position).customer!!.fullName
        holder.addressTextView.text = address!!.suburb + " " + address!!.state + " "+address!!.postCode
        holder.dateTextView.text = filteredData!!.get(position).createdDate
        if (filteredData.get(position).images != null && filteredData.get(position).images.size > 1) {
            Glide.with(context)
                .load(filteredData.get(position).images.get(1))
                .into(holder.imageView)
        }

        if (position == jobs.size - 1) {
            lastposition = position
            listener.getNewPageData()
        }

        holder.itemView.setOnClickListener {
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(filteredData.get(position))
            }
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
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.profile_image)
        val nameTextView: TextView = itemView.findViewById(R.id.name_tv)
        val addressTextView: TextView = itemView.findViewById(R.id.address_tv)
        val dateTextView: TextView = itemView.findViewById(R.id.date_tv)
    }
}