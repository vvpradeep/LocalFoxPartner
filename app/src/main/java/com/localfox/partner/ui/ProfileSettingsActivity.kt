package com.localfox.partner.ui

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.localfox.partner.app.MyApplication
import com.localfox.partner.databinding.ActivityProfileSettingsBinding
import com.localfox.partner.entity.profile.ProfileEntity
import com.localfox.partner.ui.fragments.UpdateAddressFragment
import com.localfox.partner.ui.fragments.UpdateMobileFragment


class ProfileSettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileSettingsBinding

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
        binding.addressTv.setText(profileData.data?.address)

        binding.mobileUpdateTv.setOnClickListener {
            val updateMobileFragment: UpdateMobileFragment =
                UpdateMobileFragment.newInstance()
            updateMobileFragment.show(
                supportFragmentManager,
                UpdateMobileFragment.TAG
            )
        }
        binding.addressUpdateTv.setOnClickListener {
            val updateAddressFragment: UpdateAddressFragment =
                UpdateAddressFragment.newInstance()
            updateAddressFragment.show(
                supportFragmentManager,
                UpdateAddressFragment.TAG
            )
        }

    }
}