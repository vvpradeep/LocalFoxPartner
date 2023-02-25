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
import com.localfox.partner.databinding.ActivityPasswordEntryBinding
import com.localfox.partner.databinding.ActivitySignUpEmailBinding
import com.localfox.partner.databinding.ActivitySignUpMobileNumberBinding
import com.localfox.partner.entity.RegistrartionEntity
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpEmailActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpEmailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpEmailBinding.inflate(layoutInflater)
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
        var registrartionEntity =
            intent.getSerializableExtra("registrartionEntity") as RegistrartionEntity
        binding.backButtonLl.setOnClickListener {
            finish();
        }
        binding.nextButton.setOnClickListener {
            sendEmail(
                registrartionEntity.firstName!!,
                binding.emailEt.text.toString(),
                binding,
                registrartionEntity
            )
        }
    }

    fun sendEmail(
        firstname: String,
        email: String,
        binding: ActivitySignUpEmailBinding, registrartionEntity: RegistrartionEntity
    ) {
        try {
            val json = JSONObject()
            json.put("firstName", firstname)
            json.put("emailAddress", email)
            val requestBody: RequestBody =
                RequestBody.create(MediaType.parse("application/json"), json.toString())
            val call: Call<ResponseBody> = ApiUtils.apiService.sendEmailCode(requestBody)
            call.enqueue(
                object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>?,
                        response: Response<ResponseBody>?
                    ) {
                        binding.progressCircular.setVisibility(View.GONE)
                        if (response!!.isSuccessful && response!!.body() != null) {
                            val intent = Intent(
                                this@SignUpEmailActivity,
                                EmailVerificationActivity::class.java
                            )
                            registrartionEntity.emailAddress = email
                            intent.putExtra("registrartionEntity", registrartionEntity)
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