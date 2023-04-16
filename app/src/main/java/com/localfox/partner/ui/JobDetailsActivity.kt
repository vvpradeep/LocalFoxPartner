package com.localfox.partner.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import com.bumptech.glide.Glide
import com.localfox.partner.databinding.ActivityInvitationBinding
import com.localfox.partner.databinding.ActivityJobDetailsBinding
import com.localfox.partner.databinding.ActivitySplashBinding
import com.localfox.partner.entity.JobInviations
import com.localfox.partner.entity.Jobs

class JobDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJobDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityJobDetailsBinding.inflate(layoutInflater)
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


        var job: Jobs = intent.getSerializableExtra("Jobs") as Jobs

        var address = job.location
        binding.includedLl.nameTv.text =job.customer!!.fullName
        binding.includedLl.dateTv.text = job.createdDate
        if (job.images != null && job.images.size > 1)
            Glide.with(this)
                .load(job.images.get(1))
                .into(binding.includedLl.profileImage)

        binding.jobLocationTv.text =  address!!.suburb + " " + address!!.state + " "+address!!.postCode
        binding.desTv.text = job.description
        binding.timeTv.text = job.urgency

    }
}