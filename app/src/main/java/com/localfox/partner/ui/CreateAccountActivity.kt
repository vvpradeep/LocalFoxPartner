package com.localfox.partner.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.localfox.partner.databinding.ActivityCreateAccountBinding
import com.localfox.partner.entity.RegistrartionEntity

class CreateAccountActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateAccountBinding
    var registrartionEntity: RegistrartionEntity = RegistrartionEntity()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateAccountBinding.inflate(layoutInflater)
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

        binding.nextButton.setOnClickListener {
            if (binding.firstNameEt!!.text!!.toString().trim().isNullOrBlank()) {
                binding.firstNameEt!!.setError("enter valid first name")
            }
            if (binding.lastNameEt!!.text!!.toString().trim().isNullOrBlank()) {
                binding.lastNameEt!!.setError("enter valid last name")
            }
            if (!binding.firstNameEt!!.text!!.toString().trim().isNullOrBlank() && !binding.lastNameEt!!.text!!.toString().trim().isNullOrBlank()) {
                val intent = Intent(this, SignUpMobileNumberActivity::class.java)
                registrartionEntity.firstName = binding.firstNameEt!!.text!!.toString();
                registrartionEntity.lastName = binding.lastNameEt!!.text!!.toString()
                intent.putExtra("registrartionEntity",registrartionEntity)
                startActivity(intent)
            }
        }
    }


}