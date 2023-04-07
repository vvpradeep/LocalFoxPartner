package com.localfox.partner.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.localfox.partner.databinding.FragmentSearchBinding
import com.localfox.partner.ui.HomeActivity
import com.localfox.partner.ui.adapter.JobsAdapter


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

        _binding.searchEt.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // on below line we are checking
                // if query exist or not.
                if (programmingLanguagesList.contains(query)) {
                    // if query exist within list we
                    // are filtering our list adapter.
                    listAdapter.filter.filter(query)
                } else {
                    // if query is not present we are displaying
                    // a toast message as no  data found..
                    Toast.makeText(this@MainActivity, "No Language found..", Toast.LENGTH_LONG)
                        .show()
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // if query text is change in that case we
                // are filtering our adapter with
                // new text on below line.
                listAdapter.filter.filter(newText)
                return false
            }
        })
    }


        return _binding.root
    }

    override fun onResume() {
        val activity = requireActivity() as HomeActivity
        val data = activity.jobsData1
        if (data != null) {
           var adapter = JobsAdapter(data!!.data!!.jobs)
            _binding.jobsRecyclerview.adapter = adapter
        }
        super.onResume()
    }


}
