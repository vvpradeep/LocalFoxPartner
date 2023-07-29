package com.localfox.partner.ui

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.localfox.partner.R
import com.localfox.partner.app.ApiUtils
import com.localfox.partner.app.MyApplication
import com.localfox.partner.databinding.ActivityNotificationBinding
import com.localfox.partner.entity.profile.ProfileEntity
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
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

        setData()
        onClickViews()


    }

    fun setData() {
        binding.emailSwitch.isChecked =MyApplication.applicationContext().getBolleanPrefsData("EmailNotifications")
        binding.smsSwitch.isChecked = MyApplication.applicationContext().getBolleanPrefsData("SmsNotifications")
        binding.pushSwitch.isChecked = MyApplication.applicationContext().getBolleanPrefsData("PushNotifications")
        binding.announcementsSwitch.isChecked = MyApplication.applicationContext().getBolleanPrefsData("Announcements")
        binding.eventsSwitch.isChecked = MyApplication.applicationContext().getBolleanPrefsData("Events")
    }

    fun onClickViews() {

        binding.backButtonLl.setOnClickListener() {
            finish()
        }
        binding.submitButton.setOnClickListener() {
            updateSettings()
        }
    }


    fun updateSettings() {
        try {
            binding.progressCircular.setVisibility(View.VISIBLE)
            val json = JSONObject()
            json.put("pushNotifications", binding.pushSwitch.isChecked)
            json.put("smsNotifications", binding.smsSwitch.isChecked)
            json.put("emailNotifications", binding.emailSwitch.isChecked)
            json.put("announcements", binding.announcementsSwitch.isChecked)
            json.put("events", binding.eventsSwitch.isChecked)
            val requestBody: RequestBody =
                RequestBody.create(MediaType.parse("application/json"), json.toString())

            var headers = mutableMapOf<String, String>()
            headers["Content-Type"] = "application/json"
            headers["Authorization"] = "Bearer " + MyApplication.applicationContext().getUserToken()

            val call: Call<ResponseBody> = ApiUtils.apiService.updateNotifications(requestBody, headers)
            call.enqueue(
                object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>?,
                        response: Response<ResponseBody>?
                    ) {

                        if (response!!.isSuccessful && response!!.body() != null) {
                            binding.progressCircular.setVisibility(View.GONE)
                            MyApplication.applicationContext().saveBolleanPrefsData( binding.pushSwitch.isChecked, "PushNotifications")
                            MyApplication.applicationContext().saveBolleanPrefsData( binding.smsSwitch.isChecked, "SmsNotifications")
                            MyApplication.applicationContext().saveBolleanPrefsData( binding.emailSwitch.isChecked, "EmailNotifications")
                            MyApplication.applicationContext().saveBolleanPrefsData( binding.announcementsSwitch.isChecked, "Announcements")
                            MyApplication.applicationContext().saveBolleanPrefsData( binding.eventsSwitch.isChecked, "Events")
                            MyApplication.applicationContext().showSuccessToast("Your settings has been updated successfully")

                            val handler = Handler()
                            val runnable = Runnable {
                                this@NotificationActivity.finish()
                            }
                            handler.postDelayed(runnable, 2000)

                        } else {
                            if (response!!.code() == MyApplication.applicationContext().SESSION) {
                                MyApplication.applicationContext().sessionSignIn{ result ->
                                    if (result) {
                                        updateSettings()
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

                    override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
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