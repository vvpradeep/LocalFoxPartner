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
import com.localfox.partner.databinding.ActivityPasswordResetBinding
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PasswordResetActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPasswordResetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPasswordResetBinding.inflate(layoutInflater)
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
        var email = intent.getStringExtra("email")
        var rid = intent.getStringExtra("rid")

        binding.createAccountButton.setOnClickListener {
            if (binding.passwordResetEt.text.toString().length > 4 && binding.passwordResetEt.text.toString().equals(binding.passwordEt.text.toString())) {
            sendemailResetPasswordOTP(binding.passwordResetEt.text.toString(), rid!!, binding, email!!)
            } else{
                MyApplication.applicationContext().showErrorToast("Enter valid password")
            }
        }
    }


    fun sendemailResetPasswordOTP(
        password: String,rID: String,
        binding: ActivityPasswordResetBinding, email : String) {
        try {
            binding.progressCircular.setVisibility(View.VISIBLE)
            val json = JSONObject()
            json.put("referenceId", rID)
            json.put("newPassword", password)
            json.put("emailAddress", email)
            val requestBody: RequestBody =
                RequestBody.create(MediaType.parse("application/json"), json.toString())
            val call: Call<ResponseBody> = ApiUtils.apiService.setNewPassword(requestBody)
            call.enqueue(
                object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>?,
                        response: Response<ResponseBody>?
                    ) {
                        binding.progressCircular.setVisibility(View.GONE)
                        if (response!!.isSuccessful && response!!.body() != null) {
                            val intent = Intent(
                                this@PasswordResetActivity,
                                PasswordChangedActivity::class.java
                            )
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