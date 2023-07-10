package com.localfox.partner.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.util.regex.Pattern

class SMSReceiver : BroadcastReceiver() {
    private val OTP_REGEX = "\\d{6}" // Regex pattern for a 6-digit OTP

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            for (message in messages) {
                val otp = extractOTP(message.messageBody)
                if (otp != null) {
                    // Do something with the OTP value
                    Log.d("OTP", otp)
                    val otpIntent = Intent("com.example.OTP_RECEIVED")
                    otpIntent.putExtra("otp", otp)

                    // Send the local broadcast
                    LocalBroadcastManager.getInstance(context).sendBroadcast(otpIntent)

                    // For example, you can send the OTP to an EditText field
                    // editText.setText(otp)
                }
            }
        }
    }

    private fun extractOTP(messageBody: String): String? {
        val otpPattern = "\\b\\d{4}\\b".toRegex() // Regex pattern for a 4-digit OTP
        val matchResult = otpPattern.find(messageBody)
        return matchResult?.value
    }

//    private fun extractOTP(messageBody: String): String? {
//        val pattern = Pattern.compile(OTP_REGEX)
//        val matcher = pattern.matcher(messageBody)
//        return if (matcher.find()) {
//            matcher.group(0)
//        } else {
//            null
//        }
//    }
}
