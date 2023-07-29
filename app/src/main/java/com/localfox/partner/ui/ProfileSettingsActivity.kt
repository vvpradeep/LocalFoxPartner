package com.localfox.partner.ui

import android.content.Context
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.provider.Telephony
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.localfox.partner.app.MyApplication
import com.localfox.partner.app.SMSReceiver
import com.localfox.partner.databinding.ActivityProfileSettingsBinding
import com.localfox.partner.entity.profile.ProfileEntity
import com.localfox.partner.ui.fragments.UpdateAddressFragment
import com.localfox.partner.ui.fragments.UpdateMobileFragment


class ProfileSettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileSettingsBinding

    var receiver =  SMSReceiver();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileSettingsBinding.inflate(layoutInflater)
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
            finish()
        }
        setData()
    }

    override fun onStart() {
        super.onStart()
        val intentFilter = IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)
        registerReceiver(receiver, intentFilter)
    }

    override fun onStop() {
       try {
           unregisterReceiver(receiver)
       } catch (e: IllegalArgumentException) {
           e.printStackTrace();
       }
        dismissKeyboard()
        super.onStop()

    }

    private fun dismissKeyboard() {
        val inputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocusView: View? = currentFocus
        currentFocusView?.let {
            inputMethodManager.hideSoftInputFromWindow(
                it.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    fun setData() {
        var data: String = MyApplication.applicationContext()
            .getStringPrefsData(MyApplication.applicationContext().PROFILE_DATA)
        val gson = Gson()
        var profileData: ProfileEntity = gson.fromJson(
            data,
            ProfileEntity::class.java
        )

        binding.mobileNumberTextview.setText(profileData.data?.mobileNumber)
        binding.emailTv.setText(profileData.data?.emailAddress)
        binding.addressTv.setText(profileData.data?.location!!.unit?: ""+ " " + (profileData.data?.location!!.streetNumber?: "") +" "+ (profileData.data?.location!!.streetName?: "") + " \n "
        + (profileData.data?.location!!.suburb?: "")+" " + (profileData.data?.location!!.state?: "")+" " + (profileData.data?.location!!.postCode?: ""))

        binding.mobileUpdateTv.setOnClickListener {
            val updateMobileFragment: UpdateMobileFragment =
                UpdateMobileFragment.newInstance(profileData.data?.mobileNumber!!, profileData.data?.lastName!!)
            updateMobileFragment.show(
                supportFragmentManager,
                UpdateMobileFragment.TAG
            )
        }
        binding.addressUpdateTv.setOnClickListener {
            if (profileData.data == null || profileData.data?.address == null || TextUtils.isEmpty(profileData.data?.address)) {
                val updateAddressFragment: UpdateAddressFragment =
                    UpdateAddressFragment.newInstance("")
                updateAddressFragment.show(
                    supportFragmentManager,
                    UpdateAddressFragment.TAG
                )
            } else {
                val updateAddressFragment: UpdateAddressFragment =
                    UpdateAddressFragment.newInstance(profileData.data?.address!!)
                updateAddressFragment.show(
                    supportFragmentManager,
                    UpdateAddressFragment.TAG
                )
            }
        }
    }
}