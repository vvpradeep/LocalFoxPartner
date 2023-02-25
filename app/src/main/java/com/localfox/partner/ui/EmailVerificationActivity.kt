package com.localfox.partner.ui

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import com.localfox.partner.R
import com.localfox.partner.app.ApiUtils
import com.localfox.partner.app.MyApplication
import com.localfox.partner.databinding.ActivityEmailVerificationBinding
import com.localfox.partner.databinding.ActivityMobileNumberVerificationBinding
import com.localfox.partner.entity.RegistrartionEntity
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EmailVerificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEmailVerificationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmailVerificationBinding.inflate(layoutInflater)
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

        binding.backButtonLl.setOnClickListener {
            finish();
        }
        var registrartionEntity =
            intent.getSerializableExtra("registrartionEntity") as RegistrartionEntity
        binding.nextButton.setOnClickListener {
            sendemailOTP(binding.otpEt.text.toString(), binding, registrartionEntity)
        }
    }

    fun sendemailOTP(
        otp: String,
        binding: ActivityEmailVerificationBinding, registrartionEntity: RegistrartionEntity
    ) {
        try {
            val json = JSONObject()
            json.put("verificationCode", otp)
            json.put("verificationType", "EMAIL")
            json.put("context", "VERIFY_EMAIL")
            json.put("mobileNumber", registrartionEntity.emailAddress)
            val requestBody: RequestBody =
                RequestBody.create(MediaType.parse("application/json"), json.toString())
            val call: Call<ResponseBody> = ApiUtils.apiService.validateMobileCode(requestBody)
            call.enqueue(
                object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>?,
                        response: Response<ResponseBody>?
                    ) {
                        binding.progressCircular.setVisibility(View.GONE)
                        if (response!!.isSuccessful && response!!.body() != null) {
                            val intent = Intent(
                                this@EmailVerificationActivity,
                                PasswordEntryActivity::class.java
                            )
                            intent.putExtra("registrartionEntity",registrartionEntity)
                            startActivity(intent)
                        } else {
                            MyApplication.applicationContext().showInvalidErrorToast()
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