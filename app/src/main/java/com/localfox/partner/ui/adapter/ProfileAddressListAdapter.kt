package com.localfox.partner.ui.adapter

import android.content.Context
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.localfox.partner.R
import com.localfox.partner.ui.fragments.UpdateAddressFragment
import kotlinx.android.synthetic.main.address_list_item.view.*


class ProfileAddressListAdapter(
    private val context: Context,
    private val chaptersList: ArrayList<String>,
    private val editText: EditText, private val recyclerView: RecyclerView, private val textWatcher: TextWatcher,var listner : AdapterListener
) :
    RecyclerView.Adapter<ProfileAddressListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.address_list_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return chaptersList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvaddressType?.text = chaptersList.get(position)
        holder.itemView.setOnClickListener {
            var address: String = chaptersList.get(position)
            editText.removeTextChangedListener(textWatcher)
            editText.setText(address)
            recyclerView.visibility = View.GONE
            editText.addTextChangedListener(textWatcher)
           // val fragment = context as UpdateAddressFragment
           listner.onItemClicked(true)
                // You can now use the `fragment` object as a Fragment
                // Perform Fragment-related operations here


//            val intent =
//                Intent(context, ProfileAddressConformationActivity::class.java)
//            intent.putExtra("address", chaptersList.get(position))
            //context.startActivity(intent)

        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvaddressType = view.address_textView
    }

    interface AdapterListener {
        fun onItemClicked(iss: Boolean)
    }

}