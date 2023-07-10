package com.localfox.partner.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.localfox.partner.app.ApiUtils
import com.localfox.partner.app.MyApplication
import com.google.firebase.iid.FirebaseInstanceId
import com.localfox.partner.databinding.ActivitySplashBinding
import com.localfox.partner.entity.FCMResponse
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }

        if (MyApplication.applicationContext().getStringPrefsData(MyApplication.applicationContext().EMAIL) != null
            && !MyApplication.applicationContext().getStringPrefsData(MyApplication.applicationContext().EMAIL).equals("null")
            && MyApplication.applicationContext().getStringPrefsData(MyApplication.applicationContext().EMAIL).toString().length>1) {
            MyApplication.applicationContext().splashSIgnIn();
        }

        Timer().schedule(object : TimerTask() {
            override fun run() {
                if (MyApplication.applicationContext()
                        .getStringPrefsData(MyApplication.applicationContext().FCM_TOKEN) == null ||
                    MyApplication.applicationContext()
                        .getStringPrefsData(MyApplication.applicationContext().FCM_TOKEN).equals("null")|| MyApplication.applicationContext()
                        .getStringPrefsData(MyApplication.applicationContext().FCM_TOKEN).length <= 1 ) {
                    registerFCM()
                }
                finish()
                if (MyApplication.applicationContext().getStringPrefsData(MyApplication.applicationContext().EMAIL) != null
                    && !MyApplication.applicationContext().getStringPrefsData(MyApplication.applicationContext().EMAIL).equals("null")
                    && MyApplication.applicationContext().getStringPrefsData(MyApplication.applicationContext().EMAIL).toString().length>1 &&
                    MyApplication.applicationContext().getStringPrefsData(MyApplication.applicationContext().ISVAIDLOGIN).equals("true"))  {
                    startActivity(Intent(applicationContext, HomeActivity::class.java))
                } else {
                    startActivity(Intent(applicationContext, LoginActivity::class.java))
                }
            }
        }, 3000)


    }
    fun registerFCM() {
        try {

            var headers = mutableMapOf<String, String>()
            headers["Content-Type"] = "application/json"

            var manufacturer: String = Build.MANUFACTURER;

            var model: String = Build.MODEL

            val json = JSONObject()
            val idToken = FirebaseInstanceId.getInstance().token
            json.put("fcmToken", idToken)
            json.put("deviceOS", "ANDRIOD")
            json.put("deviceOSVersion", ""+android.os.Build.VERSION.SDK_INT)
            json.put("deviceModel", manufacturer + " "+model)
            json.put("appVersion", "1.1")

            val requestBody: RequestBody =
                RequestBody.create(MediaType.parse("application/json"), json.toString())

            val call: Call<FCMResponse> = ApiUtils.apiService.registerFcmToken(requestBody ,headers)
            call.enqueue(
                object : Callback<FCMResponse> {
                    override fun onResponse(
                        call: Call<FCMResponse>?,
                        response: Response<FCMResponse>?
                    ) {

                        if (response!!.isSuccessful) {

                            val gson = Gson()
                            var json =  gson.toJson(response.body())
                            val data: FCMResponse = gson.fromJson(json, FCMResponse::class.java) //
                            MyApplication.applicationContext().saveStringPrefsData(data!!.fcmbody!!._id.toString(), MyApplication.applicationContext().FCM_TOKEN)

                        }
                    }

                    override fun onFailure(call: Call<FCMResponse>?, t: Throwable?) {

                        Log.d("response", "onFailure ")

                    }
                })
        } catch (e: Exception) {
            Log.d("response", "Exception " + e.printStackTrace())
        }
    }


}