package com.localfox.partner.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import com.google.gson.Gson
import com.localfox.partner.R
import com.localfox.partner.app.ApiUtils
import com.localfox.partner.app.MyApplication
import com.localfox.partner.databinding.ActivityInvitationBinding
import com.localfox.partner.databinding.ActivitySplashBinding
import com.localfox.partner.databinding.FragmentLeadsBinding
import com.localfox.partner.entity.JobInviations
import com.localfox.partner.entity.JobsList
import com.localfox.partner.ui.adapter.ImagesGridAdapter
import com.localfox.partner.ui.adapter.JobsAdapter
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InvitationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInvitationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInvitationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        var invitations: JobInviations  = intent.getSerializableExtra("invitations") as JobInviations

        binding.closeButton.setOnClickListener {
            finish()

        }

        binding.acceptTv.setOnClickListener {
           acceptJob(binding, invitations!!.job!!.Id!!)
        }

        binding.declineTv.setOnClickListener {
            declineJob(binding, invitations!!.job!!.Id!!)
        }

        var jobsData: JobInviations = intent.getSerializableExtra("invitations") as JobInviations

        var address = jobsData!!.job!!.location
        binding.addressTv.setText(address!!.suburb + " " + address!!.state + " " + address!!.postCode)

        binding.desTv.text = jobsData!!.job!!.description



        if (jobsData.job!!.images != null && jobsData.job!!.images.size > 0) {
            binding.photoGrid.adapter = ImagesGridAdapter(this,jobsData.job!!.images);
            binding.photoGrid.isExpanded= true;
        } else {
            binding.photoGrid.visibility = View.GONE
            binding.noPhotosAddTv.visibility = View.VISIBLE
        }
    }


    fun acceptJob(binding: ActivityInvitationBinding, id: String) {
        binding.progressCircular.setVisibility(View.VISIBLE)
        try {
            var headers = mutableMapOf<String, String>()
            headers["Content-Type"] = "application/json"
            headers["Authorization"] = "Bearer " + MyApplication.applicationContext().getUserToken()

            val call: Call<JobsList> = ApiUtils.apiService.acceptJob(headers, id)
            call.enqueue(object : Callback<JobsList> {
                override fun onResponse(
                    call: Call<JobsList>?, response: Response<JobsList>?
                ) {
                    binding.progressCircular.setVisibility(View.GONE)
                    if (response!!.isSuccessful && response!!.body() != null) {
                        val gson = Gson()
                        val json = gson.toJson(response.body()) //
                        finish();
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_in_right)
                    } else {
                        if (response.code() == MyApplication.applicationContext().SESSION) {
                            MyApplication.applicationContext().sessionSignIn()
                        } else {
                            val jsonObject = JSONObject(response.errorBody()?.string())
                            val error: String = jsonObject.getString("error")
                            MyApplication.applicationContext().showErrorToast(""+ error)
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

    fun declineJob(binding: ActivityInvitationBinding, id: String) {
        binding.progressCircular.setVisibility(View.VISIBLE)
        try {
            var headers = mutableMapOf<String, String>()
            headers["Content-Type"] = "application/json"
            headers["Authorization"] = "Bearer " + MyApplication.applicationContext().getUserToken()

            val call: Call<JobsList> = ApiUtils.apiService.declineJob(headers, id)
            call.enqueue(object : Callback<JobsList> {
                override fun onResponse(
                    call: Call<JobsList>?, response: Response<JobsList>?
                ) {
                    binding.progressCircular.setVisibility(View.GONE)
                    if (response!!.isSuccessful && response!!.body() != null) {
                        val gson = Gson()
                        val json = gson.toJson(response.body()) //
                        finish();
                        overridePendingTransition(0, R.anim.slide_in_right_left_open)
                    } else {
                        if (response.code() == MyApplication.applicationContext().SESSION) {
                            MyApplication.applicationContext().sessionSignIn()
                        } else {
                            val jsonObject = JSONObject(response.errorBody()?.string())
                            val error: String = jsonObject.getString("error")
                            MyApplication.applicationContext().showErrorToast(""+ error)
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

//    override fun finish() {
//        super.finish()
//       // overridePendingTransition(R.anim.slide_in_left, R.anim.slide_in_right)
//        overridePendingTransition(0, R.anim.slide_in_right_left_open)
//
//    }

}