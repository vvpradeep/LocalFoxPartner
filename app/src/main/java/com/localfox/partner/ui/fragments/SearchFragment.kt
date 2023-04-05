package com.localfox.partner.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.localfox.partner.R
import com.localfox.partner.databinding.FragmentLeadsBinding
import com.localfox.partner.databinding.FragmentSearchBinding
import com.localfox.partner.ui.HomeActivity


class SearchFragment : Fragment() {
    private lateinit var _binding: FragmentSearchBinding
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        // this creates a vertical layout Manager
        _binding.jobsRecyclerview.layoutManager = LinearLayoutManager(activity)
        _binding.jobsRecyclerview.setNestedScrollingEnabled(false);


        var normalLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        _binding.jobsRecyclerview.layoutParams = normalLayoutParams;




        return _binding.root
    }

    override fun onResume() {
        val activity = requireActivity() as HomeActivity
        val data = activity.jobsData1
        if (data != null) {
        }
        super.onResume()
    }

}