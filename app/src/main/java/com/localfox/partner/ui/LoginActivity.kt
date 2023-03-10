package com.localfox.partner.ui

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager

import com.google.gson.Gson
import com.localfox.partner.app.ApiUtils.apiService
import com.localfox.partner.app.MyApplication
import com.localfox.partner.databinding.ActivityLoginBinding
import com.localfox.partner.entity.LoginEntity
import com.localfox.partner.entity.profile.ProfileEntity
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    var emailPattern: String = "[a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\\.+[a-z]+"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
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

        binding.forgotPasswordTv.setOnClickListener {
            val intent = Intent(this, SignUpEmailActivity::class.java)
            intent.putExtra("isSignUp", false)
            intent.putExtra("isforgot", true)
            startActivity(intent)
        }

        if (MyApplication.applicationContext()
                .getStringPrefsData(MyApplication.applicationContext().EMAIL) != null
            && !MyApplication.applicationContext()
                .getStringPrefsData(MyApplication.applicationContext().EMAIL).equals("null")
            && MyApplication.applicationContext()
                .getStringPrefsData(MyApplication.applicationContext().EMAIL).toString().length > 1
        ) {
            binding.progressCircular.setVisibility(View.VISIBLE)

            binding.emailEt.setText(
                MyApplication.applicationContext()
                    .getStringPrefsData(MyApplication.applicationContext().EMAIL)
            )
            binding.passwordEt.setText(
                MyApplication.applicationContext()
                    .getStringPrefsData(MyApplication.applicationContext().PASSWORD)
            )
            loginAuthendication(
                binding.emailEt.text.toString(),
                binding.passwordEt.text.toString(),
                binding
            )

        }

        binding.signupButton.setOnClickListener {
            val intent = Intent(this, CreateAccountActivity::class.java)
            startActivity(intent)
        }


        binding.loginButton.setOnClickListener {
            if (!binding.emailEt!!.text.toString().trim { it <= ' ' }.matches(emailPattern.toRegex())) {
                binding.emailEt!!.setError("enter valid email")
                return@setOnClickListener
            }
            if (binding.passwordEt!!.text!!.toString().trim().length < 5) {
                binding.passwordEt!!.setError("enter valid password")
                return@setOnClickListener
            }
            if (!binding.emailEt!!.text!!.toString().trim()
                    .isNullOrBlank() && !binding.passwordEt!!.text!!.toString().trim()
                    .isNullOrBlank()
            ) {
                loginAuthendication(
                    binding.emailEt.text.toString(),
                    binding.passwordEt.text.toString(),
                    binding
                )
            }
        }
    }
    //mr.rajeshMekala@yahoo.com

    fun loginAuthendication(eMail: String, ePass: String, binding: ActivityLoginBinding) {
        binding.progressCircular.setVisibility(View.VISIBLE)
        try {
            val json = JSONObject()
            json.put("emailAddress", eMail)
            json.put("password", ePass)
            val requestBody: RequestBody =
                RequestBody.create(MediaType.parse("application/json"), json.toString())
            val call: Call<LoginEntity> = apiService.loginPost(requestBody)
            call.enqueue(
                object : Callback<LoginEntity> {
                    override fun onResponse(
                        call: Call<LoginEntity>?,
                        response: Response<LoginEntity>?
                    ) {
                        if (response!!.isSuccessful && response!!.body() != null) {
                            Log.d("response", "response isSuccessful")
                            val gson = Gson()
                            val json = gson.toJson(response.body()) //
                            MyApplication.applicationContext().getAPP(this@LoginActivity)
                            MyApplication.applicationContext().saveStringPrefsData(
                                json,
                                MyApplication.applicationContext().LOGIN_DATA
                            )
                            MyApplication.applicationContext().saveStringPrefsData(
                                eMail,
                                MyApplication.applicationContext().EMAIL
                            )
                            MyApplication.applicationContext().saveStringPrefsData(
                                ePass,
                                MyApplication.applicationContext().PASSWORD
                            )
                            var login: LoginEntity? = response.body()
                            var isMobileVerified: String = login!!.isMobileVerified.toString()
                            if (isMobileVerified.equals("true")) {
                                MyApplication.applicationContext().saveStringPrefsData(
                                    "true",
                                    MyApplication.applicationContext().ISVAIDLOGIN
                                )
                               getProfile(binding)
                            } else {
                                binding.progressCircular.setVisibility(View.GONE)
                                val intent =
                                    Intent(
                                        this@LoginActivity,
                                        MobileNumberVerificationActivity::class.java
                                    )
                                intent.putExtra("isFromSignIn", true)
                                intent.putExtra("email", eMail)
                                startActivity(intent)
                            }
                            //  linkfcm()
                        } else {
                            binding.progressCircular.setVisibility(View.GONE)
                            MyApplication.applicationContext()
                                .saveStringPrefsData(
                                    "",
                                    MyApplication.applicationContext().EMAIL
                                )
                            MyApplication.applicationContext().saveStringPrefsData(
                                "",
                                MyApplication.applicationContext().PASSWORD
                            )
                            try {
                                val jObjError = JSONObject(response.errorBody()?.string())
                                MyApplication.applicationContext().showToast(
                                    false, jObjError.getString("error"), this@LoginActivity)
                            } catch (e: Exception) {

                            }
                        }
                    }

                    override fun onFailure(call: Call<LoginEntity>?, t: Throwable?) {
                        binding.progressCircular.setVisibility(View.GONE)
                        MyApplication.applicationContext()
                            .saveStringPrefsData("", MyApplication.applicationContext().EMAIL)
                        MyApplication.applicationContext()
                            .saveStringPrefsData(
                                "",
                                MyApplication.applicationContext().PASSWORD
                            )
                        MyApplication.applicationContext().showInvalidErrorToast()
                        Log.d("response", "onFailure ")

                    }
                })
        } catch (e: Exception) {
            binding.progressCircular.setVisibility(View.GONE)
            Log.d("response", "Exception " + e.printStackTrace())
        }
    }




    fun getProfile(binding: ActivityLoginBinding) {
        binding.progressCircular.setVisibility(View.VISIBLE)
        try {

            var headers = mutableMapOf<String, String>()
            headers["Content-Type"] = "application/json"
            headers["Authorization"] = "Bearer " + MyApplication.applicationContext().getUserToken()

            val call: Call<ProfileEntity> = apiService.getProfile(headers)
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
                            MyApplication.applicationContext().getAPP(this@LoginActivity)
                            MyApplication.applicationContext().saveStringPrefsData(json,MyApplication.applicationContext().PROFILE_DATA)
                            MyApplication.applicationContext().saveNotificationsData(response.body()!!.data!!.NotificationSettings!!)
                            val intent =
                                Intent(this@LoginActivity, HomeActivity::class.java)
                            startActivity(intent)
                        }
                        else{
                            MyApplication.applicationContext().showInvalidErrorToast()
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
}
