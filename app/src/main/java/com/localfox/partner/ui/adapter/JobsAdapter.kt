package com.localfox.partner.ui.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.localfox.partner.R
import com.localfox.partner.entity.Jobs
import com.localfox.partner.entity.JobsList

class JobsAdapter(private var jobs : ArrayList<Jobs>) : RecyclerView.Adapter<JobsAdapter.ViewHolder>() {
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
        var address = jobs!!.get(position).location
        holder.nameTextView.text =jobs!!.get(position).customer!!.fullName
        holder.addressTextView.text = address!!.suburb + " " + address!!.state + " "+address!!.postCode
        holder.dateTextView.text = jobs!!.get(position).createdDate

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return jobs!!.size
    }

    fun setData( jobs1 : ArrayList<Jobs>) {
        jobs = jobs1
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.profile_image)
        val nameTextView: TextView = itemView.findViewById(R.id.name_tv)
        val addressTextView: TextView = itemView.findViewById(R.id.address_tv)
        val dateTextView: TextView = itemView.findViewById(R.id.date_tv)
    }
}