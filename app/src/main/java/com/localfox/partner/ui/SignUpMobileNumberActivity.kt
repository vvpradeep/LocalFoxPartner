package com.localfox.partner.ui

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import com.google.gson.Gson
import com.localfox.partner.R
import com.localfox.partner.app.ApiUtils
import com.localfox.partner.app.MyApplication
import com.localfox.partner.databinding.ActivityLoginBinding
import com.localfox.partner.databinding.ActivityPasswordEntryBinding
import com.localfox.partner.databinding.ActivitySignUpMobileNumberBinding
import com.localfox.partner.entity.LoginEntity
import com.localfox.partner.entity.RegistrartionEntity
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpMobileNumberActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpMobileNumberBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpMobileNumberBinding.inflate(layoutInflater)
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

        binding.backButtonLl.setOnClickListener {
            finish()
        }

        binding.nextButton.setOnClickListener {
            if (!binding.mobileEt.text.toString()
                    .isNullOrBlank() && ((binding.mobileEt.text.toString().startsWith("4", true) && binding.mobileEt.text.toString().length == 9) || (binding.mobileEt.text.toString().startsWith("04", true) && binding.mobileEt.text.toString().length == 10))
            ) sendmobileNumber(
                    registrartionEntity.firstName!!,
                    binding.mobileEt.text.toString(),
                    binding,
                    registrartionEntity
                )
            else binding.mobileEt.setError("enter valid number")
        }
    }

    fun sendmobileNumber(
        firstname: String,
        _mobileNumber: String,
        binding: ActivitySignUpMobileNumberBinding, registrartionEntity: RegistrartionEntity
    ) {
        var mobileNumber : String = _mobileNumber
        if (_mobileNumber.length == 9) {
            mobileNumber = "0" + mobileNumber
        }
        try {
            binding.progressCircular.setVisibility(View.VISIBLE)
            val json = JSONObject()
            json.put("firstName", firstname)
            json.put("mobileNumber", mobileNumber)
            val requestBody: RequestBody =
                RequestBody.create(MediaType.parse("application/json"), json.toString())
            val call: Call<ResponseBody> = ApiUtils.apiService.sendMobileCode(requestBody)
            call.enqueue(
                object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>?,
                        response: Response<ResponseBody>?
                    ) {
                        binding.progressCircular.setVisibility(View.GONE)
                        if (response!!.isSuccessful) {
                            val intent = Intent(
                                this@SignUpMobileNumberActivity,
                                MobileNumberVerificationActivity::class.java
                            )
                            registrartionEntity.mobileNumber = mobileNumber
                            intent.putExtra("registrartionEntity", registrartionEntity)
                            startActivity(intent)
                        } else {
                            val jsonObject = JSONObject(response.errorBody()?.string())
                            val error: String = jsonObject.getString("error")
                            MyApplication.applicationContext().showErrorToast(""+ error)
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
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