package com.localfox.partner.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.localfox.partner.databinding.FragmentLeadsBinding
import com.localfox.partner.entity.Jobs
import com.localfox.partner.entity.JobsList
import com.localfox.partner.ui.HomeActivity
import com.localfox.partner.ui.InvitationActivity
import com.localfox.partner.ui.JobDetailsActivity
import com.localfox.partner.ui.adapter.JobsAdapter


class LeadsFragment : Fragment(), JobsAdapter.OnItemClickListener {


    private lateinit var _binding: FragmentLeadsBinding
    private val binding get() = _binding
    private var jobsData: JobsList? = null
    private var adapter : JobsAdapter? = null

        override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLeadsBinding.inflate(inflater, container, false)

       // getjobs(_binding)

        // this creates a vertical layout Manager
        _binding.jobsRecyclerview.layoutManager = LinearLayoutManager(activity)
        _binding.jobsRecyclerview.setNestedScrollingEnabled(false);


        var normalLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        _binding.jobsRecyclerview.layoutParams = normalLayoutParams;

        // This will pass the ArrayList to our Adapter
        binding.invitationLl.setOnClickListener {
            if (jobsData != null) {
                for (invitations in jobsData!!.data!!.jobInviations) {
                    val intent = Intent(
                        activity,
                        InvitationActivity::class.java
                    )
                    intent.putExtra("invitations", invitations)
                    startActivity(intent)
                }
            }
        }

        return binding.root
    }


    fun updateData(jobsData1: JobsList?) {
        jobsData = jobsData1
        val activity = requireActivity() as HomeActivity
        if (adapter == null)if (activity.jobsList != null && activity.jobsList!!.size  > 0) {
            adapter = JobsAdapter(activity.jobsList, requireContext()!!, this)
        }
        else {
            if (activity.jobsList != null && activity.jobsList!!.size  > 0) {
                adapter!!.setData(activity.jobsList)
            }
        }
        _binding.jobsRecyclerview.adapter = adapter
        _binding.countTv.text = "" + jobsData!!.invitationsCount;
    }

    override fun onResume() {
        val activity = requireActivity() as HomeActivity
        val data = activity.jobsData1
        if (activity.jobsList != null && activity.jobsList!!.size  > 0) {
            adapter = JobsAdapter(activity.jobsList, requireContext()!!, this)
            _binding.jobsRecyclerview.adapter = adapter
            _binding.countTv.text = "" + data!!.invitationsCount;
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
        (activity as HomeActivity).getPaginationData()
    }
}