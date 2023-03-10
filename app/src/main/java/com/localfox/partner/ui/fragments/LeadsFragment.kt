package com.localfox.partner.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.localfox.partner.databinding.FragmentLeadsBinding
import com.localfox.partner.ui.adapter.JobsAdapter


class LeadsFragment : Fragment() {


    private lateinit var _binding: FragmentLeadsBinding
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLeadsBinding.inflate(inflater, container, false)


        // this creates a vertical layout Manager
       _binding.jobsRecyclerview.layoutManager = LinearLayoutManager(activity)
        _binding.jobsRecyclerview.setNestedScrollingEnabled(false);

        // ArrayList of class ItemsViewModel
        val data = ArrayList<String>()

        // This loop will create 20 Views containing
        // the image with the count of view
        for (i in 1..20) {
            data.add("s")
        }

        var normalLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        _binding.jobsRecyclerview.layoutParams = normalLayoutParams;

        // This will pass the ArrayList to our Adapter
        val adapter = JobsAdapter(data)

        // Setting the Adapter with the recyclerview
        _binding.jobsRecyclerview.adapter = adapter
        return binding.root
    }

}