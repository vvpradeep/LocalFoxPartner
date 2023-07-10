package com.localfox.partner.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.localfox.partner.app.LetterDrawable
import com.localfox.partner.app.MyApplication
import com.localfox.partner.databinding.ActivityJobDetailsBinding
import com.localfox.partner.entity.Jobs
import com.localfox.partner.ui.adapter.ImagesGridAdapter


class JobDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJobDetailsBinding
    private val CALL_PERMISSION_REQUEST_CODE = 1010
    var phoneNumber: String = "";

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

        binding.closeButton.setOnClickListener() {
            finish()
        }

        var job: Jobs = intent.getSerializableExtra("Jobs") as Jobs

        var address = job.location
        binding.includedLl.nameTv.text =job.customer!!.fullName
        binding.includedLl.addressTv.text = address!!.suburb + " " + address!!.state + " "+address!!.postCode

        binding.includedLl.dateTv.text =  MyApplication.applicationContext().formatDate(job.createdDate)

        if (job.customer!!.profilePhoto != null && !job.customer!!.profilePhoto.toString().contains("no-photo.jpg")) {
            Glide.with(this)
                .load(job.customer!!.profilePhoto)
                .into(binding.includedLl.profileImage)
        } else {
            val parts = job.customer!!.fullName!!.split(" ")
            Glide.with(this)
                .load(LetterDrawable(parts[0]!!.get(0)+""+parts[1]!!.get(0), applicationContext))
                .into(binding.includedLl.profileImage)
        }

        binding.desTv.text = job.description
        binding.timeTv.text = job.urgency
        binding.addressDesTv.text =  address!!.suburb + " " + address!!.state + " "+address!!.postCode

        if (job.images != null && job.images.size > 0) {
            binding.photoGrid.adapter = ImagesGridAdapter(this, job.images);
            binding.photoGrid.isExpanded= true;
        } else {
            binding.photoGrid.visibility = View.GONE
            binding.noPhotosAddTv.visibility = View.VISIBLE
        }

        binding.includedLl.callFl.setOnClickListener {
            if (job!!.customer!= null && job!!.customer!!.mobileNumber != null)
                onCallClick(job!!.customer!!.mobileNumber!!)
        }
        binding.includedLl.mailFl.setOnClickListener {
            if (job!!.customer!= null && job!!.customer!!.emailAddress != null)
                onMailClick(job!!.customer!!.emailAddress!!)
        }
        binding.includedLl.locationFl.setOnClickListener {
            if (job!!.location!= null && job!!.location!!.coordinates != null) {
                val coordinates: ArrayList<Double> =
                    job!!.location!!.coordinates;
                onLocationClick("", coordinates.get(0), coordinates.get(1))
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CALL_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission granted
            // Retry the phone call after permission is granted
            makePhoneCall(phoneNumber)
        } else {
            // Permission denied
            Toast.makeText(this, "Call permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun makePhoneCall(phoneNumber: String) {
        val callIntent = Intent(Intent.ACTION_CALL)
        callIntent.data = Uri.parse("tel:$phoneNumber")
        startActivity(callIntent)
    }

    fun onCallClick(phoneNumber: String) {
        this.phoneNumber = phoneNumber;
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CALL_PHONE),
                CALL_PERMISSION_REQUEST_CODE
            )
        } else {
            makePhoneCall(phoneNumber)
        }
    }

    fun onMailClick(mailID: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_EMAIL, arrayOf(mailID))
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    fun onLocationClick(location: String, lat: Double, long: Double) {
        val geoUri = Uri.parse("geo:$lat,$long")
        val intent = Intent(Intent.ACTION_VIEW, geoUri)
        val shareIntent = Intent.createChooser(intent, null)
        startActivity(shareIntent)
    }


}