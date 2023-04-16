package com.localfox.partner.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.localfox.partner.databinding.FragmentSearchBinding
import com.localfox.partner.entity.Jobs
import com.localfox.partner.ui.HomeActivity
import com.localfox.partner.ui.JobDetailsActivity
import com.localfox.partner.ui.adapter.JobsAdapter


class SearchFragment : Fragment(), JobsAdapter.OnItemClickListener  {
    private lateinit var _binding: FragmentSearchBinding
    private val binding get() = _binding
    private var adapter : JobsAdapter? = null

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



        _binding.searchEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (adapter != null) adapter!!.filter(s.toString()!!)
            }
        })

        return _binding.root
    }

    override fun onResume() {
        val activity = requireActivity() as HomeActivity
        if (activity.jobsList != null && activity.jobsList!!.size  > 0) {
            adapter = JobsAdapter(activity.jobsList, requireContext()!!, this)
            _binding.jobsRecyclerview.adapter = adapter
        }
        super.onResume()
    }

    override fun onItemClick(job: Jobs) {
        val intent = Intent(
            activity,
            JobDetailsActivity::class.java
        )
        intent.putExtra("Jobs", job)
        startActivity(intent)
    }

    override fun getNewPageData() {

    }
}
