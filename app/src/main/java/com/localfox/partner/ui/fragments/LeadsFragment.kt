package com.localfox.partner.ui.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.localfox.partner.R
import com.localfox.partner.app.ApiUtils
import com.localfox.partner.app.MyApplication
import com.localfox.partner.databinding.FragmentLeadsBinding
import com.localfox.partner.entity.Jobs
import com.localfox.partner.entity.JobsList
import com.localfox.partner.ui.HomeActivity
import com.localfox.partner.ui.InvitationActivity
import com.localfox.partner.ui.JobDetailsActivity
import com.localfox.partner.ui.adapter.JobsAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LeadsFragment : Fragment(), JobsAdapter.OnItemClickListener {


    private lateinit var _binding: FragmentLeadsBinding
    private val binding get() = _binding
    private var jobsData: JobsList? = null
    private var adapter: JobsAdapter? = null
    var phoneNumber: String = "";

    private val CALL_PERMISSION_REQUEST_CODE = 1010

    private lateinit var mLinearLayoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLeadsBinding.inflate(inflater, container, false)


        _binding.swipeRefreshLayout.setOnRefreshListener {
            val activity = requireActivity() as HomeActivity
            getjobs(_binding, 1, activity.pageSize)
            _binding.swipeRefreshLayout.isRefreshing = false
        }


        mLinearLayoutManager = LinearLayoutManager(activity)
        _binding.jobsRecyclerview.layoutManager = mLinearLayoutManager
        _binding.jobsRecyclerview.setNestedScrollingEnabled(false);

        _binding.jobsRecyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                _binding.swipeRefreshLayout.setEnabled(mLinearLayoutManager.findFirstCompletelyVisibleItemPosition() === 0) // 0 is for first item position
            }
        })

        _binding.searchIV.setOnClickListener {
            val activityView = activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)
            activityView!!.menu.getItem(1).isChecked = true
            val menu = activityView!!.menu
            val menuItemId = R.id.navigation_search
            menu?.performIdentifierAction(menuItemId, 1)
        }


        var normalLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        _binding.jobsRecyclerview.layoutParams = normalLayoutParams;

        // This will pass the ArrayList to our Adapter
        binding.invitationLl.setOnClickListener {
            val activity = requireActivity() as HomeActivity
            if ( activity.jobsData1 != null) {
                for ((index, item) in activity.jobsData1!!.data!!.jobInviations.withIndex()) {
                    val intent = Intent(
                        activity,
                        InvitationActivity::class.java
                    )
                    intent.putExtra("invitations", item)
                    if (index + 1 == activity.jobsData1!!.data!!.jobInviations.size) {
                        startActivityForResult(intent, 1009)
                    } else {
                        startActivity(intent)
                    }
                }

            }
        }

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode ==1009) {
            val activity = requireActivity() as HomeActivity
            getjobs(_binding, 1, activity.pageSize)
        }
    }

    fun getjobs(binding: FragmentLeadsBinding, pageNumber : Int, pageSize: Int) {
        binding.progressCircular.setVisibility(View.VISIBLE)
        try {

            var headers = mutableMapOf<String, String>()
            headers["Content-Type"] = "application/json"
            headers["Authorization"] = "Bearer " + MyApplication.applicationContext().getUserToken()

            val call: Call<JobsList> = ApiUtils.apiService.getJobs(headers, pageNumber, pageSize)
            call.enqueue(object : Callback<JobsList> {
                override fun onResponse(
                    call: Call<JobsList>?, response: Response<JobsList>?
                ) {
                    if (response!!.isSuccessful && response!!.body() != null) {
                        binding.progressCircular.setVisibility(View.GONE)
                        val gson = Gson()
                        val json = gson.toJson(response.body()) //
                        var jobsData1 = response.body()!!
                        val activity = requireActivity() as HomeActivity
                        activity.jobsData1 = response.body()!!
                        if (jobsData1 != null  && jobsData1!!.data != null && jobsData1!!.data!!.jobs != null &&  jobsData1!!.data!!.jobs!!.size > 0) {
                            activity.jobsList.clear()
                            activity.jobsList.addAll(jobsData1!!.data!!.jobs)
                            activity.lastDataFetched  = true
                        } else {
                            activity.lastDataFetched = false
                        }
                        setAdapter();
                        //sendDataToFragment(response.body()!!)
                    } else {
                        if (response.code() == MyApplication.applicationContext().SESSION) {
                            MyApplication.applicationContext().sessionSignIn { result ->
                                if (result) {
                                   getjobs(binding, pageNumber, pageSize)
                                } else {
                                    binding.progressCircular.setVisibility(View.GONE)
                                }
                            }
                        } else {
                            binding.progressCircular.setVisibility(View.GONE)
                            MyApplication.applicationContext().showInvalidErrorToast()
                        }
                    }
                }

                override fun onFailure(call: Call<JobsList>?, t: Throwable?) {
                    binding.progressCircular.setVisibility(View.GONE)
                    MyApplication.applicationContext().showInvalidErrorToast()
                    Log.d("response", "onFailure ")

                }
            })
        } catch (e: Exception) {
            binding.progressCircular.setVisibility(View.GONE)
            Log.d("response", "Exception " + e.printStackTrace())
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == CALL_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission granted
            // Retry the phone call after permission is granted
            makePhoneCall(phoneNumber)
        } else {
            // Permission denied
            Toast.makeText(requireContext(), "Call permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun makePhoneCall(phoneNumber: String) {
        val callIntent = Intent(Intent.ACTION_CALL)
        callIntent.data = Uri.parse("tel:$phoneNumber")
        startActivity(callIntent)
    }

    override fun onCallClick(phoneNumber: String) {
        this.phoneNumber = phoneNumber;
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CALL_PHONE),
                CALL_PERMISSION_REQUEST_CODE
            )
        } else {
            makePhoneCall(phoneNumber)
        }
    }

    override fun onMailClick(mailID: String) {

     val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_EMAIL, arrayOf(mailID))
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)

    }

    override fun onLocationClick(location: String, lat: Double, long: Double) {
        val geoUri = Uri.parse("geo:$lat,$long")
        val intent = Intent(Intent.ACTION_VIEW, geoUri)
        val shareIntent = Intent.createChooser(intent, null)
        startActivity(shareIntent)

    }

    fun updateData(jobsData1: JobsList?) {
        jobsData = jobsData1
        val activity = requireActivity() as HomeActivity
        if (adapter == null) if (activity.jobsList != null && activity.jobsList!!.size > 0) {
            adapter = JobsAdapter(activity.jobsList, requireContext()!!, this, false)
        } else {
            if (activity.jobsList != null && activity.jobsList!!.size > 0) {
                adapter!!.setData(activity.jobsList)
            }
        }
        _binding.jobsRecyclerview.adapter = adapter
        if (jobsData!!.invitationsCount != null && jobsData!!.invitationsCount!! > 0) {
            _binding.countTv.text = "" + jobsData!!.invitationsCount;
            _binding.invitationLl.visibility = View.VISIBLE
        } else {
            _binding.invitationLl.visibility = View.GONE
        }
    }

    override fun onResume() {
        setAdapter()
        super.onResume()
    }

    fun setAdapter() {
        val activity = requireActivity() as HomeActivity
        val data = activity.jobsData1
        jobsData = activity.jobsData1
        if (activity.jobsList != null && activity.jobsList!!.size > 0) {
            adapter = JobsAdapter(activity.jobsList, requireContext()!!, this, false)
            _binding.jobsRecyclerview.adapter = adapter
            if (data!!.invitationsCount != null && data!!.invitationsCount!! > 0) {
                _binding.countTv.text = "" + data!!.invitationsCount;
                _binding.invitationLl.visibility = View.VISIBLE
            } else {
                _binding.invitationLl.visibility = View.GONE
            }
        }
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