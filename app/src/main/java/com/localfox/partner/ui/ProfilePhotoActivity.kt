package com.localfox.partner.ui

import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.localfox.partner.R
import com.localfox.partner.databinding.ActivityProfilePhotoBinding
import com.localfox.partner.databinding.ActivitySecurityBinding

class ProfilePhotoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfilePhotoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfilePhotoBinding.inflate(layoutInflater)
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
        binding.closeButtonLl.setOnClickListener {
            finish()
        }

        val transformation = MultiTransformation(CenterCrop(), RoundedCorners(40))

        if (!TextUtils.isEmpty(intent.getStringExtra("id"))) {
            if (!intent.getBooleanExtra("islocal", false))
                Glide.with(this)
                    .load(intent.getStringExtra("id"))
                    .transform(transformation)
                    .into(binding.profileImage)
            else
                Glide.with(this)
                    .load(Uri.parse(intent.getStringExtra("id")))
                    .transform(transformation)
                    .into(binding.profileImage!!)
        }

    }
}