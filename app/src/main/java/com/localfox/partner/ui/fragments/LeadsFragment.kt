package com.localfox.partner.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.localfox.partner.app.ApiUtils
import com.localfox.partner.app.MyApplication
import com.localfox.partner.databinding.FragmentLeadsBinding
import com.localfox.partner.entity.EmailVerificationEntity
import com.localfox.partner.entity.Jobs
import com.localfox.partner.entity.JobsList
import com.localfox.partner.ui.HomeActivity
import com.localfox.partner.ui.InvitationActivity
import com.localfox.partner.ui.PasswordResetActivity
import com.localfox.partner.ui.adapter.JobsAdapter
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type


class LeadsFragment : Fragment() {


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
        adapter = JobsAdapter(jobsData!!.data!!.jobs)
        _binding.jobsRecyclerview.adapter = adapter
        _binding.countTv.text = "" + jobsData!!.invitationsCount;
    }

    override fun onResume() {
        val activity = requireActivity() as HomeActivity
        val data = activity.jobsData1
        if (data != null) {
            if (adapter != null &&  adapter is JobsAdapter) {
                adapter!!.setData(data!!.data!!.jobs)
                adapter!!.notifyDataSetChanged()
                _binding.countTv.text = "" + data!!.invitationsCount;
            } else {
                adapter = JobsAdapter(data!!.data!!.jobs)
                _binding.jobsRecyclerview.adapter = adapter
                _binding.countTv.text = "" + data!!.invitationsCount;
            }

        }
        super.onResume()
    }


}