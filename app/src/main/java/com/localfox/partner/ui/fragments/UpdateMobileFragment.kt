package com.localfox.partner.ui.fragments

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.localfox.partner.R
import com.localfox.partner.app.ApiUtils
import com.localfox.partner.app.MyApplication
import com.localfox.partner.databinding.FragmentUpdateMobileNumberBinding
import com.localfox.partner.ui.MobileNumberVerificationActivity
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class UpdateMobileFragment : BottomSheetDialogFragment() {

    private lateinit var _binding: FragmentUpdateMobileNumberBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUpdateMobileNumberBinding.inflate(inflater, container, false)
        _binding.closeButtonLl.setOnClickListener {
            dismiss()
        }
        _binding.mobileEt.setText(getArguments()?.getString("mobileNumber", ""))
        _binding.updateButton.setOnClickListener {
            if (!_binding.mobileEt.text.toString()
                    .isNullOrBlank() && ((_binding.mobileEt.text.toString().startsWith("4", true) && _binding.mobileEt.text.toString().length == 9) ||
                        (_binding.mobileEt.text.toString().startsWith("04", true) &&_binding.mobileEt.text.toString().length == 10)))

                sendmobileNumber(
                    _binding.mobileEt.text.toString(),getArguments()?.getString("firstName", "").toString(),
                    _binding
                )
            else _binding.mobileEt.setError("enter valid number")
        }
        return _binding.root
    }
    fun sendmobileNumber(_mobileNumber: String, firstName: String, binding: FragmentUpdateMobileNumberBinding) {
        var mobileNumber: String = _mobileNumber
        if (_mobileNumber.length == 9) {
            mobileNumber = "0" + mobileNumber
        }
        try {
            binding.progressCircular.setVisibility(View.VISIBLE)
            val json = JSONObject()
            json.put("firstName", firstName)
            json.put("mobileNumber", mobileNumber)
            val requestBody: RequestBody =
                RequestBody.create(MediaType.parse("application/json"), json.toString())
            val call: Call<ResponseBody> = ApiUtils.apiService.sendMobileCode(requestBody)
            call.enqueue(
                object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>?,
                        response: Response<ResponseBody>?
                    ) {
                        binding.progressCircular.setVisibility(View.GONE)
                        if (response!!.isSuccessful && response!!.body() != null) {
//                            val intent = Intent(
//                                this@SignUpMobileNumberActivity,
//                                MobileNumberVerificationActivity::class.java
//                            )
//                            registrartionEntity.mobileNumber = mobileNumber
//                            intent.putExtra("registrartionEntity", registrartionEntity)
//                            startActivity(intent)
                            val updateMobileFragment: UpdateMobileOTPFragment =
                                UpdateMobileOTPFragment.newInstance(_mobileNumber)
                            updateMobileFragment.show(
                                parentFragmentManager,
                                UpdateMobileFragment.TAG
                            )

                        } else {
                            val jsonObject = JSONObject(response.errorBody()?.string())
                            val error: String = jsonObject.getString("error")
                            MyApplication.applicationContext().showErrorToast(""+ error)
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
        fun newInstance(mobileNumber: String, firstName: String): UpdateMobileFragment {
           var fragment : UpdateMobileFragment = UpdateMobileFragment()
            val args = Bundle()
            args.putString("mobileNumber", mobileNumber)
            args.putString("firstName", firstName)
            fragment.setArguments(args)
            return fragment
        }
    }
}