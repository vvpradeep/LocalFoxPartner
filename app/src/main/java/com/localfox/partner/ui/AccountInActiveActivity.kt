package com.localfox.partner.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.gson.Gson
import com.localfox.partner.app.ApiUtils
import com.localfox.partner.app.MyApplication
import com.localfox.partner.databinding.ActivityAccountInActiveBinding
import com.localfox.partner.databinding.ActivityLoginBinding
import com.localfox.partner.entity.profile.ProfileEntity
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AccountInActiveActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccountInActiveBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountInActiveBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.headingTv.setText("Welcome, " + intent.getStringExtra("name"))
        binding.closeButtonLl.setOnClickListener {
            var intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            MyApplication.applicationContext()
                .saveStringPrefsData("", MyApplication.applicationContext().LOGIN_DATA)
            MyApplication.applicationContext()
                .saveStringPrefsData("", MyApplication.applicationContext().EMAIL)
            MyApplication.applicationContext()
                .saveStringPrefsData("", MyApplication.applicationContext().PASSWORD)
            MyApplication.applicationContext()
                .saveStringPrefsData("false", MyApplication.applicationContext().ISVAIDLOGIN)
            MyApplication.applicationContext().client.cache()!!.evictAll()
            finish()
            startActivity(intent)
            logout()

        }

        binding.refreshButton.setOnClickListener {
            getProfile(binding)
        }
    }

    fun getProfile(binding: ActivityAccountInActiveBinding) {
        binding.progressCircular.setVisibility(View.VISIBLE)
        try {

            var headers = mutableMapOf<String, String>()
            headers["Content-Type"] = "application/json"
            headers["Authorization"] = "Bearer " + MyApplication.applicationContext().getUserToken()

            val call: Call<ProfileEntity> = ApiUtils.apiService.getProfile(headers)
            call.enqueue(
                object : Callback<ProfileEntity> {
                    override fun onResponse(
                        call: Call<ProfileEntity>?,
                        response: Response<ProfileEntity>?
                    ) {
                        binding.progressCircular.setVisibility(View.GONE)
                        if (response!!.isSuccessful && response!!.body() != null) {
                            val gson = Gson()
                            val json = gson.toJson(response.body()) //
                            MyApplication.applicationContext().getAPP(this@AccountInActiveActivity)
                            MyApplication.applicationContext().saveStringPrefsData(
                                json,
                                MyApplication.applicationContext().PROFILE_DATA
                            )
                            MyApplication.applicationContext()
                                .saveNotificationsData(response.body()!!.data!!.NotificationSettings!!)
                            val intent =
                                Intent(this@AccountInActiveActivity, HomeActivity::class.java)
                            startActivity(intent)
                        } else {
                            if (response.code() == MyApplication.applicationContext().SESSION) {
                                MyApplication.applicationContext().sessionSignIn { result ->
                                    if (result) {
                                        getProfile(binding)
                                    }
                                }
                            } else {
                                MyApplication.applicationContext().showInvalidErrorToast()
                            }
                        }
                    }

                    override fun onFailure(call: Call<ProfileEntity>?, t: Throwable?) {
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

    fun logout() {
        try {
            var headers = mutableMapOf<String, String>()
            headers["Content-Type"] = "application/json"
            headers["Authorization"] = "Bearer " + MyApplication.applicationContext().getUserToken()

            val call: Call<ResponseBody> = ApiUtils.apiService.logout(headers)
            call.enqueue(
                object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>?,
                        response: Response<ResponseBody>?
                    ) {
                        if (response!!.isSuccessful) {

                        } else {
                            if (response!!.code() == MyApplication.applicationContext().SESSION) {
                                MyApplication.applicationContext().sessionSignIn { result ->
                                    if (result) {
                                        logout()
                                    }
                                }
                            } else {

                            }
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {

                    }
                })
        } catch (e: Exception) {
            Log.d("response", "Exception " + e.printStackTrace())
        }
    }
}