package com.localfox.partner.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.localfox.partner.app.MyApplication
import com.localfox.partner.databinding.ActivityAccountCreatedBinding
import com.localfox.partner.databinding.ActivityPasswordChangedBinding

class PasswordChangedActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPasswordChangedBinding
    var isforgotPassword = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPasswordChangedBinding.inflate(layoutInflater)
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
        if (intent.hasExtra("isforgot")) {
            if (intent.getBooleanExtra("isforgot", false)) {
                isforgotPassword = true;
            }
        }

        binding.closeButtonLl.setOnClickListener {
            if (isforgotPassword) {
                var intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                finish()
                startActivity(intent)
            } else {
                finish();
            }
        }
    }

    override fun onBackPressed() {
        if (isforgotPassword) {
            var intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            finish()
            startActivity(intent)
        } else {
            finish();
        }
    }
}