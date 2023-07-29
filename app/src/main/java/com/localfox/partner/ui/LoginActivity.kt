package com.localfox.partner.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

import com.google.gson.Gson
import com.localfox.partner.R
import com.localfox.partner.app.ApiUtils
import com.localfox.partner.app.ApiUtils.apiService
import com.localfox.partner.app.MyApplication
import com.localfox.partner.databinding.ActivityLoginBinding
import com.localfox.partner.entity.LoginEntity
import com.localfox.partner.entity.profile.ProfileEntity
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.TextView as TextView1

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

        askNotificationPermission()
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

        setupClickableTextView(binding.termsAndConditionsTv)

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

    fun setupClickableTextView(textView: TextView1) {
        val fullText = resources.getText(R.string.terms_and_conditions);

        val spannableString = SpannableString(fullText)

        val clickablePart1 = "Terms and Conditions of use"

        val clickableSpan1 = object : ClickableSpan() {
            override fun onClick(view: View) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://localfox.com.au/partner/terms"))
                startActivity(intent)
            }
        }

//        val clickableSpan2 = object : ClickableSpan() {
//            override fun onClick(view: View) {
//                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://localfox.com.au/partner/privacy"))
//                startActivity(intent)
//            }
//        }

        val startIndex1 = fullText.indexOf(clickablePart1)
        val endIndex1 = startIndex1 + clickablePart1.length

//        val startIndex2 = fullText.indexOf(clickablePart2)
//        val endIndex2 = startIndex2 + clickablePart2.length

        spannableString.setSpan(clickableSpan1, startIndex1, endIndex1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(ForegroundColorSpan(resources.getColor(R.color.red)), startIndex1, endIndex1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
       // spannableString.setSpan(clickableSpan2, startIndex2, endIndex2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
       // spannableString.setSpan(ForegroundColorSpan(resources.getColor(R.color.red)), startIndex2, endIndex2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        textView.text = spannableString
        textView.movementMethod = LinkMovementMethod.getInstance()
    }

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
                            linkfcm()
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
                        else{ if (response.code() == MyApplication.applicationContext().SESSION) {
                            MyApplication.applicationContext().sessionSignIn{ result ->
                                if (result) {
                                    getProfile(binding)
                                }
                            }
                        } else {
                            MyApplication.applicationContext().showInvalidErrorToast()
                        }
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


    fun linkfcm() {
        try {
            var headers = mutableMapOf<String, String>()
            headers["Authorization"] = "Bearer " + MyApplication.applicationContext().getUserToken()
            headers["Content-Type"] = "application/json"

            val call: Call<ResponseBody> =
                ApiUtils.apiService.linkPartner(headers, MyApplication.applicationContext().getStringPrefsData(MyApplication.applicationContext().FCM_TOKEN))
            call.enqueue(
                object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>?,
                        response: Response<ResponseBody>?
                    ) {
                        if (response!!.code() == MyApplication.applicationContext().SESSION) {
                            MyApplication.applicationContext().sessionSignIn { result ->
                                if (result) {
                                    linkfcm()
                                }
                            }
                            Log.d("res", "res")
                        } else {
                            Log.d("res", "res")
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                        Log.d("res", "res")
                    }
                })
        } catch (e: Exception) {
            Log.d("response", "Exception " + e.printStackTrace())
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Can post notifications.
        } else {
            askNotificationPermission()
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // Can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // Display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }


}
