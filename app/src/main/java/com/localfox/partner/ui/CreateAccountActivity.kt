package com.localfox.partner.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.localfox.partner.R
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
        setupClickableTextView(binding.termsAndConditionsTv)
    }

    fun setupClickableTextView(textView: TextView) {
        val fullText = resources.getText(R.string.terms_and_conditions_privacy);

        val spannableString = SpannableString(fullText)

        val clickablePart1 = "Terms and Conditions of use"
        val clickablePart2 = "Privacy"

        val clickableSpan1 = object : ClickableSpan() {
            override fun onClick(view: View) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://localfox.com.au/partner/terms"))
                startActivity(intent)
            }
        }

        val clickableSpan2 = object : ClickableSpan() {
            override fun onClick(view: View) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://localfox.com.au/partner/privacy"))
                startActivity(intent)
            }
        }

        val startIndex1 = fullText.indexOf(clickablePart1)
        val endIndex1 = startIndex1 + clickablePart1.length

        val startIndex2 = fullText.indexOf(clickablePart2)
        val endIndex2 = startIndex2 + clickablePart2.length

        spannableString.setSpan(clickableSpan1, startIndex1, endIndex1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(ForegroundColorSpan(resources.getColor(R.color.red)), startIndex1, endIndex1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(clickableSpan2, startIndex2, endIndex2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(ForegroundColorSpan(resources.getColor(R.color.red)), startIndex2, endIndex2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        textView.text = spannableString
        textView.movementMethod = LinkMovementMethod.getInstance()
    }

}