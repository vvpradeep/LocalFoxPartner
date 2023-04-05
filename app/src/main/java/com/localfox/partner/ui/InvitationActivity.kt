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
import com.localfox.partner.app.ApiUtils
import com.localfox.partner.app.MyApplication
import com.localfox.partner.databinding.ActivityInvitationBinding
import com.localfox.partner.databinding.ActivitySplashBinding
import com.localfox.partner.databinding.FragmentLeadsBinding
import com.localfox.partner.entity.JobInviations
import com.localfox.partner.entity.JobsList
import com.localfox.partner.ui.adapter.JobsAdapter
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
        binding.closeButton.setOnClickListener {
            finish()
        }

        var jobsData: JobInviations = intent.getSerializableExtra("invitations") as JobInviations

        var address = jobsData!!.job!!.location
        binding.addressTv.setText(address!!.suburb + " " + address!!.state + " " + address!!.postCode)

        binding.desTv.text = jobsData!!.job!!.description

    }


    fun acceptJob(binding: FragmentLeadsBinding, id: String) {
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
                    } else {
                        MyApplication.applicationContext().showInvalidErrorToast()
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


}