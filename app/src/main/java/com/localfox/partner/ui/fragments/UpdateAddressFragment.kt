package com.localfox.partner.ui.fragments

import android.app.Dialog
import android.content.Context
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
import com.localfox.partner.databinding.FragmentUpdateAddressBinding
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class UpdateAddressFragment : BottomSheetDialogFragment() {

    private lateinit var _binding: FragmentUpdateAddressBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =  FragmentUpdateAddressBinding.inflate(inflater, container, false)
        _binding.closeButtonLl.setOnClickListener{
            dismiss()
        }
        _binding.updateButton.setOnClickListener {
            if (!_binding.addressEt.text.toString()
                    .isNullOrBlank())

                sendAddress(
                    _binding.addressEt.text.toString(),
                    _binding
                )
            else _binding.addressEt.setError("enter valid adress")
        }
        return _binding.root
    }

    fun sendAddress(address: String, binding: FragmentUpdateAddressBinding) {

        try {
            binding.progressCircular.setVisibility(View.VISIBLE)
            val json = JSONObject()
            json.put("newAddress", address)
            val requestBody: RequestBody =
                RequestBody.create(MediaType.parse("application/json"), json.toString())
            var headers = mutableMapOf<String, String>()
            headers["Content-Type"] = "application/json"
            headers["Authorization"] = "Bearer " + MyApplication.applicationContext().getUserToken()
            val call: Call<ResponseBody> = ApiUtils.apiService.updateAddress(requestBody, headers)
            call.enqueue(
                object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>?,
                        response: Response<ResponseBody>?
                    ) {
                        binding.progressCircular.setVisibility(View.GONE)
                        if (response!!.isSuccessful && response!!.body() != null) {
//                            val intent = Intent(
//                                activity,
//                                MobileNumberVerificationActivity::class.java
//                            )
//                            startActivity(intent)
                        } else {
                            MyApplication.applicationContext().showInvalidErrorToast()
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
        fun newInstance(): UpdateAddressFragment {
            return UpdateAddressFragment()
        }
    }
}