package com.localfox.partner.app;

import android.app.Application
import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import com.google.gson.Gson
import com.localfox.partner.R
import com.localfox.partner.app.ApiUtils.apiService
import com.localfox.partner.databinding.ToastLayoutBinding
import com.localfox.partner.entity.LoginEntity
import com.localfox.partner.entity.RegistrationResponseEntiity
import com.localfox.partner.entity.profile.NotificationSettings
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyApplication : Application() {


    val SESSION : Int = 401
    val LOGIN_DATA : String = "LOGIN_DATA"
    val REGISTRATION_DATA : String = "REGISTRATION_DATA"
    val EMAIL : String = "EMAIL"
    val PASSWORD : String = "PASSWORD"
    val PROFILE_DATA : String = "PROFILE_DATA"
    val CHOOSE_SERVICE_DATA : String = "CHOOSE_SERVICE_DATA"
    val ISVAIDLOGIN : String = "VALID_LOGIN"
    val FCM_TOKEN : String = "FCM_TOKEN"



    var client = OkHttpClient.Builder().build()

    init {
        instance = this
    }

    companion object {
        private var instance: MyApplication? = null

        fun applicationContext() : MyApplication {
            return instance as MyApplication
        }
    }

    override fun onCreate() {
        super.onCreate()
    }

    fun getAPP(context: Context): MyApplication? {
        return context.getApplicationContext() as MyApplication
    }

    fun getAppContext(): Context {
        return applicationContext()
    }
    fun saveStringPrefsData(value: String, key: String) {
        var sharedPreferences = getSharedPreferences("prefs", 0)
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.commit()
    }
     fun  getStringPrefsData(key: String): String {
        var sharedPreferences = getSharedPreferences("prefs", 0)
        return sharedPreferences.getString(key, null).toString()
    }

    fun saveBolleanPrefsData(value: Boolean, key: String) {
        var sharedPreferences = getSharedPreferences("prefs", 0)
        val editor = sharedPreferences.edit()
        editor.putBoolean(key, value)
        editor.commit()
    }
    fun  getBolleanPrefsData(key: String): Boolean {
        var sharedPreferences = getSharedPreferences("prefs", 0)
        return sharedPreferences.getBoolean(key, false)
    }

    fun saveNotificationsData(notiSettings : NotificationSettings) {

        saveBolleanPrefsData(notiSettings.smsNotifications!!, "SmsNotifications")
        saveBolleanPrefsData(notiSettings.pushNotifications!!, "PushNotifications")
        saveBolleanPrefsData(notiSettings.events!!, "Events")
        saveBolleanPrefsData(notiSettings.emailNotifications!!, "EmailNotifications")
        saveBolleanPrefsData(notiSettings.announcements!!, "Announcements")

    }

    fun getUserToken(): String  {

        var token: String
        var data: String = MyApplication.applicationContext().getStringPrefsData(MyApplication.applicationContext().LOGIN_DATA)
        if( data != null && data.length>1) {
            val gson = Gson()
            var loginEntity: LoginEntity = gson.fromJson(data,
                LoginEntity::class.java)

            token = loginEntity.token.toString()
        } else {
            var data: String = MyApplication.applicationContext().getStringPrefsData(MyApplication.applicationContext().REGISTRATION_DATA)
            val gson = Gson()
            var registrationResponseEntiity: RegistrationResponseEntiity = gson.fromJson(data,
                RegistrationResponseEntiity::class.java)

            token = registrationResponseEntiity.token.toString()
        }

        return token
    }

    fun showErrorToast(text: String){
        showToast(false, text, applicationContext)
    }

    fun showInvalidErrorToast(){
        showToast(false, "Your Device is not connected with internet", applicationContext)
    }

    fun showSuccessToast(text: String){
        showToast(true, text, applicationContext)
    }

    fun showToast(success: Boolean, text: String, context: Context){
        //Creating the LayoutInflater instance
        var  binding: ToastLayoutBinding;

        val inflater = context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = ToastLayoutBinding.inflate(inflater)
        binding.msgTextView.setText(text)
        //Creating the Toast object
        val toast = Toast(applicationContext)
        toast.duration = Toast.LENGTH_LONG
        toast.setGravity(Gravity.FILL_HORIZONTAL, 25, 25)
        toast.view = binding.root//setting the view of custom toast layout
        toast.show()
        if (success) {
            binding.parentLl.setBackgroundColor(context.resources.getColor(R.color.green_toast_bg))
            binding.msgTextView.setTextColor(context.resources.getColor(R.color.green_toast_color))
            binding.imageView.setBackgroundResource(R.drawable.successtick)
        } else {
            binding.parentLl.setBackgroundColor(context.resources.getColor(R.color.red_toast_bg))
            binding.msgTextView.setTextColor(context.resources.getColor(R.color.red_toast_color))
            binding.imageView.setBackgroundResource(android.R.drawable.stat_sys_warning)
        }
        binding
    }

//    fun sessionSignIn() {
//            try {
//                showToast(false, "Session expired!. Please try again later",applicationContext)
//                val json = JSONObject()
//                json.put("emailAddress", MyApplication.applicationContext().getStringPrefsData(MyApplication.applicationContext().EMAIL))
//                json.put("password", MyApplication.applicationContext().getStringPrefsData(MyApplication.applicationContext().PASSWORD))
//                val requestBody: RequestBody = RequestBody.create(MediaType.parse("application/json"), json.toString())
//                val call: Call<LoginEntity> = apiService.loginPost(requestBody)
//                call.enqueue(
//                    object : Callback<LoginEntity> {
//                        override fun onResponse(call: Call<LoginEntity>?, response: Response<LoginEntity>?) {
//
//                            if (response!!.isSuccessful && response!!.body() != null) {
//                                Log.d("response","response isSuccessful")
//                                val gson = Gson()
//                                val json = gson.toJson(response.body()) //
//                                saveStringPrefsData(json,MyApplication.applicationContext().LOGIN_DATA)
//                            }
//                        }
//                        override fun onFailure(call: Call<LoginEntity>?, t: Throwable?) {
////                            MyApplication.applicationContext().saveStringPrefsData("",MyApplication.applicationContext().EMAIL)
////                            MyApplication.applicationContext().saveStringPrefsData("",MyApplication.applicationContext().PASSWORD)
//                        }
//                    })
//            } catch (e: Exception) {
//                Log.d("response","Exception "+e.printStackTrace())
//            }
//
//    }
//
    fun splashSIgnIn() {
            try {
                val json = JSONObject()
                json.put("emailAddress", getStringPrefsData(EMAIL))
                json.put("password",getStringPrefsData(PASSWORD))
                val requestBody: RequestBody = RequestBody.create(MediaType.parse("application/json"), json.toString())
                val call: Call<LoginEntity> = apiService.loginPost(requestBody)
                call.enqueue(
                    object : Callback<LoginEntity> {
                        override fun onResponse(call: Call<LoginEntity>?, response: Response<LoginEntity>?) {

                            if (response!!.isSuccessful && response!!.body() != null) {
                                Log.d("response","response isSuccessful")
                                val gson = Gson()
                                val json = gson.toJson(response.body()) //
                                saveStringPrefsData(json,MyApplication.applicationContext().LOGIN_DATA)
                            }
                        }
                        override fun onFailure(call: Call<LoginEntity>?, t: Throwable?) {
//                            MyApplication.applicationContext().saveStringPrefsData("",MyApplication.applicationContext().EMAIL)
//                            MyApplication.applicationContext().saveStringPrefsData("",MyApplication.applicationContext().PASSWORD)
                        }
                    })
            } catch (e: Exception) {
                Log.d("response","Exception "+e.printStackTrace())
            }


    }

}