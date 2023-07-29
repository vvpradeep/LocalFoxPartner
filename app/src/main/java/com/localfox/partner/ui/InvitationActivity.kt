package com.localfox.partner.ui

import android.app.Activity
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
import okhttp3.ResponseBody
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

            val call: Call<ResponseBody> = ApiUtils.apiService.acceptJob(headers, id)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>?, response: Response<ResponseBody>?
                ) {
                    if (response!!.isSuccessful && response!!.body() != null) {
                        binding.progressCircular.setVisibility(View.GONE)
                        MyApplication.applicationContext().showSuccessToast("Invitation Accepted successfully")
                        setResult(Activity.RESULT_OK)
                        finish()
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_in_right)
                    } else {
                        if (response.code() == MyApplication.applicationContext().SESSION) {
                            MyApplication.applicationContext().sessionSignIn { result ->
                                if (result) {
                                    acceptJob(binding, id)
                                } else {
                                    binding.progressCircular.setVisibility(View.GONE)
                                    val jsonObject = JSONObject(response.errorBody()?.string())
                                    val error: String = jsonObject.getString("error")
                                    MyApplication.applicationContext().showErrorToast(""+ error)
                                    setResult(Activity.RESULT_OK)
                                    finish()
                                }
                            }
                        } else {
                            binding.progressCircular.setVisibility(View.GONE)
                            val jsonObject = JSONObject(response.errorBody()?.string())
                            val error: String = jsonObject.getString("error")
                            MyApplication.applicationContext().showErrorToast(""+ error)
                            setResult(Activity.RESULT_OK)
                            finish()
                        }
                    }
                }
                override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                    binding.progressCircular.setVisibility(View.GONE)
                    MyApplication.applicationContext().showErrorToast(t!!.localizedMessage)
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

            val call: Call<ResponseBody> = ApiUtils.apiService.declineJob(headers, id)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>?, response: Response<ResponseBody>?
                ) {
                    if (response!!.isSuccessful && response!!.body() != null) {
                        binding.progressCircular.setVisibility(View.GONE)
                        MyApplication.applicationContext().showSuccessToast("Invitation Rejected successfully")
                        setResult(Activity.RESULT_OK)
                        finish();
                        overridePendingTransition(0, R.anim.slide_in_right_left_open)
                    } else {
                        if (response.code() == MyApplication.applicationContext().SESSION) {
                            MyApplication.applicationContext().sessionSignIn { result ->
                                if (result) {
                                    declineJob(binding, id)
                                } else {
                                    binding.progressCircular.setVisibility(View.GONE)
                                    val jsonObject = JSONObject(response.errorBody()?.string())
                                    val error: String = jsonObject.getString("error")
                                    MyApplication.applicationContext().showErrorToast(""+ error)
                                    setResult(Activity.RESULT_OK)
                                    finish()
                                }
                            }
                        } else {
                            binding.progressCircular.setVisibility(View.GONE)
                            val jsonObject = JSONObject(response.errorBody()?.string())
                            val error: String = jsonObject.getString("error")
                            MyApplication.applicationContext().showErrorToast(""+ error)
                            setResult(Activity.RESULT_OK)
                            finish()
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                    binding.progressCircular.setVisibility(View.GONE)
                    MyApplication.applicationContext().showErrorToast(t!!.localizedMessage)
                    Log.d("response", "onFailure ")
                }
            })
        } catch (e: Exception) {
            binding.progressCircular.setVisibility(View.GONE)
            Log.d("response", "Exception " + e.printStackTrace())
        }
    }

}