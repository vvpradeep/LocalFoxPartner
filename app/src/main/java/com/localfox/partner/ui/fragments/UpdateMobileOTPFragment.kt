package com.localfox.partner.ui.fragments

import android.app.Activity
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.localfox.partner.R
import com.localfox.partner.app.ApiUtils
import com.localfox.partner.app.MyApplication
import com.localfox.partner.databinding.ActivityMobileNumberVerificationBinding
import com.localfox.partner.databinding.FragmentUpdateMobileNumberBinding
import com.localfox.partner.databinding.FragmentUpdateMobileOtpNumberBinding
import com.localfox.partner.entity.MobileVerificationEntity
import com.localfox.partner.entity.RegistrartionEntity
import com.localfox.partner.ui.MobileNumberVerificationActivity
import com.localfox.partner.ui.SignUpEmailActivity
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.ref.PhantomReference


class UpdateMobileOTPFragment : BottomSheetDialogFragment() {

    private lateinit var _binding: FragmentUpdateMobileOtpNumberBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUpdateMobileOtpNumberBinding.inflate(inflater, container, false)
        _binding.closeButtonLl.setOnClickListener {
            dismiss()
        }
        _binding.updateButton.setOnClickListener {
            if (!_binding.otpEt.text.toString().isNullOrBlank() && (_binding.otpEt.text.toString().length == 4 )) {
                sendmobileNumberOTP(_binding.otpEt.text.toString(), _binding)
            } else {
                _binding.otpEt.setError("enter valid number")
            }
        }
        return _binding.root
    }

    private val otpReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Retrieve the OTP from the intent
            val otp = intent.getStringExtra("otp")

            // Use the OTP as needed
            if (otp != null) {
                // Display the OTP in a TextView

                _binding.otpEt.setText(otp)

                // Perform other operations with the OTP
                // ...
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Register the broadcast receiver with the appropriate intent filter
        val filter = IntentFilter("com.example.OTP_RECEIVED")
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(otpReceiver, filter)
    }

    override fun onPause() {
        super.onPause()
        // Unregister the broadcast receiver when the activity is paused
        try {
            LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(otpReceiver)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace();
        }


    }

    fun sendmobileNumberOTP(otp: String,
        binding: FragmentUpdateMobileOtpNumberBinding) {
        try {
            val json = JSONObject()
            json.put("verificationCode", otp)
            json.put("verificationType", "MOBILE")
            json.put("context", "VERIFY_MOBILE")
            json.put("mobileNumber", getArguments()?.getString("mobileNumber", "").toString())
            val requestBody: RequestBody =
                RequestBody.create(MediaType.parse("application/json"), json.toString())
            val call: Call<MobileVerificationEntity> =
                ApiUtils.apiService.validateMobileCode(requestBody)
            call.enqueue(
                object : Callback<MobileVerificationEntity> {
                    override fun onResponse(
                        call: Call<MobileVerificationEntity>?,
                        response: Response<MobileVerificationEntity>?
                    ) {
                        binding.progressCircular.setVisibility(View.GONE)
                        if (response!!.isSuccessful && response!!.body() != null) {
                            var mobileVerification = response.body() as MobileVerificationEntity
                            sendmobileNumber(getArguments()?.getString("mobileNumber", "").toString() , mobileVerification!!.mobileVerificationReference.toString(), binding )
                        } else {
                            val jsonObject = JSONObject(response.errorBody()?.string())
                            val error: String = jsonObject.getString("error")
                            MyApplication.applicationContext().showErrorToast(""+ error)
                        }
                    }

                    override fun onFailure(call: Call<MobileVerificationEntity>?, t: Throwable?) {
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

    fun sendmobileNumber(_mobileNumber: String, reference: String, binding: FragmentUpdateMobileOtpNumberBinding) {
        try {
            binding.progressCircular.setVisibility(View.VISIBLE)
            val json = JSONObject()
            json.put("mobileNumber", _mobileNumber)
            json.put("mobileVerificationReference", reference)
            val requestBody: RequestBody =
                RequestBody.create(MediaType.parse("application/json"), json.toString())
            var headers = mutableMapOf<String, String>()
            headers["Content-Type"] = "application/json"
            headers["Authorization"] = "Bearer " + MyApplication.applicationContext().getUserToken()
            val call: Call<ResponseBody> =
                ApiUtils.apiService.updateMobileNumber(requestBody, headers)
            call.enqueue(
                object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>?,
                        response: Response<ResponseBody>?
                    ) {

                        if (response!!.isSuccessful && response!!.body() != null) {
                            binding.progressCircular.setVisibility(View.GONE)
                            MyApplication.applicationContext().showSuccessToast("Mobile number updated successfully")
                            if (dialog != null)
                                dialog!!.dismiss()
                            requireActivity().setResult(Activity.RESULT_OK)
                            requireActivity().finish()
                        } else {
                            if (response!!.code() == MyApplication.applicationContext().SESSION) {
                                MyApplication.applicationContext().sessionSignIn{ result ->
                                    if (result) {
                                        sendmobileNumber(_mobileNumber, reference, binding)
                                    } else {
                                        binding.progressCircular.setVisibility(View.GONE)
                                    }
                                }
                            } else {
                                binding.progressCircular.setVisibility(View.GONE)
                                val jsonObject = JSONObject(response.errorBody()?.string())
                                val error: String = jsonObject.getString("error")
                                MyApplication.applicationContext().showErrorToast(""+ error)
                            }
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
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


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { it ->
                val behaviour = BottomSheetBehavior.from(it)
                setupFullHeight(it)
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        return dialog
    }

    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }

    companion object {
        const val TAG = "ActionBottomDialog"
        fun newInstance(mobileNumber: String): UpdateMobileOTPFragment {
           var fragment : UpdateMobileOTPFragment = UpdateMobileOTPFragment()
            val args = Bundle()
            args.putString("mobileNumber", mobileNumber)
            fragment.setArguments(args)
            return fragment
        }
    }
}