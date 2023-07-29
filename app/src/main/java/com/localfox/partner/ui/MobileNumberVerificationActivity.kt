package com.localfox.partner.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Telephony
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.localfox.partner.R
import com.localfox.partner.app.ApiUtils
import com.localfox.partner.app.MyApplication
import com.localfox.partner.app.SMSReceiver
import com.localfox.partner.databinding.ActivityCreateAccountBinding
import com.localfox.partner.databinding.ActivityMobileNumberVerificationBinding
import com.localfox.partner.databinding.ActivitySignUpMobileNumberBinding
import com.localfox.partner.entity.EmailVerificationEntity
import com.localfox.partner.entity.MobileVerificationEntity
import com.localfox.partner.entity.RegistrartionEntity
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MobileNumberVerificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMobileNumberVerificationBinding
    var registrartionEntity : RegistrartionEntity? = null;
    var receiver =  SMSReceiver();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMobileNumberVerificationBinding.inflate(layoutInflater)
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
        if (intent.hasExtra("registrartionEntity")) {
            registrartionEntity =
                intent.getSerializableExtra("registrartionEntity") as RegistrartionEntity
        }
        binding.backButtonLl.setOnClickListener {
            finish();
        }
        binding.nextButton.setOnClickListener {
            if (registrartionEntity != null && binding.otpEt.text.toString().length == 4) {
                sendmobileNumberOTP(binding.otpEt.text.toString(), binding, registrartionEntity!!)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION).also {
            registerReceiver(receiver, it)
        }
    }
    override fun onStop() {
        try {
            unregisterReceiver(receiver)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace();
        }
        super.onStop()
    }

    private val otpReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Retrieve the OTP from the intent
            val otp = intent.getStringExtra("otp")

            // Use the OTP as needed
            if (otp != null) {
                // Display the OTP in a TextView

                binding.otpEt.setText(otp)

                // Perform other operations with the OTP
                // ...
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Register the broadcast receiver with the appropriate intent filter
        val filter = IntentFilter("com.example.OTP_RECEIVED")
        LocalBroadcastManager.getInstance(this).registerReceiver(otpReceiver, filter)
    }

    override fun onPause() {
        super.onPause()
        // Unregister the broadcast receiver when the activity is paused
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(otpReceiver)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace();
        }

    }

    fun sendmobileNumberOTP(
        otp: String,
        binding: ActivityMobileNumberVerificationBinding, registrartionEntity: RegistrartionEntity
    ) {
        try {
            val json = JSONObject()
            json.put("verificationCode", otp)
            json.put("verificationType", "MOBILE")
            json.put("context", "VERIFY_MOBILE")
            json.put("mobileNumber", registrartionEntity.mobileNumber)
            val requestBody: RequestBody =
                RequestBody.create(MediaType.parse("application/json"), json.toString())
            val call: Call<MobileVerificationEntity> = ApiUtils.apiService.validateMobileCode(requestBody)
            call.enqueue(
                object : Callback<MobileVerificationEntity> {
                    override fun onResponse(
                        call: Call<MobileVerificationEntity>?,
                        response: Response<MobileVerificationEntity>?
                    ) {
                        binding.progressCircular.setVisibility(View.GONE)
                        if (response!!.isSuccessful && response!!.body() != null) {
                            val intent = Intent(
                                this@MobileNumberVerificationActivity,
                                SignUpEmailActivity::class.java
                            )
                            var mobileVerification = response.body() as MobileVerificationEntity
                            registrartionEntity.mobileVerificationReference = mobileVerification.mobileVerificationReference
                            intent.putExtra("registrartionEntity",registrartionEntity)
                            intent.putExtra("isSignUp", true)
                            startActivity(intent)
                        } else {
                            val jsonObject = JSONObject(response.errorBody()?.string())
                            val error: String = jsonObject.getString("error")
                            MyApplication.applicationContext().showErrorToast(""+ error)
                        }
                    }

                    override fun onFailure(call: Call<MobileVerificationEntity>?, t: Throwable?) {
                        binding.progressCircular.setVisibility(View.GONE)
                        MyApplication.applicationContext().showErrorToast(""+ t!!.message)
                        Log.d("response", "onFailure ")

                    }
                })
        } catch (e: Exception) {
            binding.progressCircular.setVisibility(View.GONE)
            Log.d("response", "Exception " + e.printStackTrace())
        }
    }
}