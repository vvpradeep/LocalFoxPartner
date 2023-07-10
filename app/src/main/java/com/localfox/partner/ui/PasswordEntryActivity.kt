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
import com.localfox.partner.databinding.ActivityPasswordEntryBinding
import com.localfox.partner.entity.RegistrartionEntity
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PasswordEntryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPasswordEntryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPasswordEntryBinding.inflate(layoutInflater)
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
        binding.createAccountButton.setOnClickListener {
//            val intent = Intent(this, AccountCreatedActivity::class.java)
//            registrartionEntity.password = binding.passwordEt.text.toString()
//            intent.putExtra("registrartionEntity", registrartionEntity)
//            startActivity(intent)
            register(binding.passwordEt.text.toString(), binding, registrartionEntity)
        }
    }


    fun register(
        password: String,
        binding: ActivityPasswordEntryBinding, registrartionEntity: RegistrartionEntity
    ) {
        try {
            binding.progressCircular.setVisibility(View.VISIBLE)
            val json = JSONObject()
            json.put("firstName", registrartionEntity.firstName)
            json.put("lastName", registrartionEntity.lastName)
            json.put("mobileNumber", registrartionEntity.mobileNumber)
            json.put("emailAddress", registrartionEntity.emailAddress)
            json.put("password", password)
            json.put("mobileVerificationReference", registrartionEntity.mobileVerificationReference)
            json.put("emailVerificationReference", registrartionEntity.emailVerificationReference)

            val requestBody: RequestBody =
                RequestBody.create(MediaType.parse("application/json"), json.toString())
            val call: Call<ResponseBody> = ApiUtils.apiService.registerPartner(requestBody)
            call.enqueue(
                object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>?,
                        response: Response<ResponseBody>?
                    ) {
                        binding.progressCircular.setVisibility(View.GONE)
                        if (response!!.isSuccessful) {
                            val intent = Intent(
                                this@PasswordEntryActivity,
                                AccountCreatedActivity::class.java
                            )
                            //intent.putExtra("registrartionEntity", registrartionEntity)
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