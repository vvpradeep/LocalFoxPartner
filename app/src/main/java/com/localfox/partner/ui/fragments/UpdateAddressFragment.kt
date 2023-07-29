package com.localfox.partner.ui.fragments

import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.localfox.partner.BuildConfig
import com.localfox.partner.R
import com.localfox.partner.app.ApiUtils
import com.localfox.partner.app.MyApplication
import com.localfox.partner.databinding.FragmentUpdateAddressBinding
import com.localfox.partner.ui.adapter.ProfileAddressListAdapter
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class UpdateAddressFragment : BottomSheetDialogFragment(),
    ProfileAddressListAdapter.AdapterListener {

    private lateinit var _binding: FragmentUpdateAddressBinding

    private var apiKey: String = BuildConfig.GOOGLE_PLACES_API_KEY

    private var isSelected: Boolean = false


    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUpdateAddressBinding.inflate(inflater, container, false)
        _binding.closeButtonLl.setOnClickListener {
            dismiss()
        }


        _binding.addressEt.setText(getArguments()?.getString("address", ""))
        _binding.searchResult.layoutManager = LinearLayoutManager(context)


        var divider: DividerItemDecoration =
            DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        divider.setDrawable(ResourcesCompat.getDrawable(resources, R.drawable.line_divider, null)!!)
        _binding.searchResult.addItemDecoration(divider)

        if (!Places.isInitialized()) {
            Places.initialize(context, apiKey)
        }
        val placesClient = Places.createClient(context);
        _binding.addressEt.addTextChangedListener(textWatcher);

        _binding.updateButton.setOnClickListener {
            if (!isSelected) {
                _binding.addressEt.setError("Select valid adress")
                return@setOnClickListener
            }
            if (!_binding.addressEt.text.toString()
                    .isNullOrBlank()
            )
                sendAddress(
                    _binding.addressEt.text.toString(),
                    _binding
                )
            else _binding.addressEt.setError("Select valid adress")
        }
        return _binding.root
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onItemClicked(item: Boolean) {
        this.isSelected = item
    }


    val textWatcher = object : TextWatcher {

        override fun afterTextChanged(s: Editable) {

        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            val placesClient = Places.createClient(context);
            isSelected = false
            if (count > 0) {
                _binding.progressCircular.setVisibility(View.VISIBLE)
                // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
                // and once again when the user makes a selection (for example when calling fetchPlace()).
                var token: AutocompleteSessionToken = AutocompleteSessionToken.newInstance();
                // Create a RectangularBounds object.

                var bounds: RectangularBounds = RectangularBounds.newInstance(
                    LatLng(-33.880490, 151.184363),
                    LatLng(-33.858754, 151.229596)
                )
//
                // Use the builder to create a FindAutocompletePredictionsRequest.
                var request: FindAutocompletePredictionsRequest =
                    FindAutocompletePredictionsRequest.builder()
                        // Call either setLocationBias() OR setLocationRestriction().
                        .setLocationBias(bounds)
                        //.setLocationRestriction(bounds)
                        .setCountry("au")
                        .setTypeFilter(TypeFilter.ADDRESS)
                        .setSessionToken(token)
                        .setQuery(_binding.addressEt.getText().toString())
                        .build();


                placesClient.findAutocompletePredictions(request)
                    .addOnSuccessListener { response ->
                        val chaptersList: ArrayList<String> = ArrayList()
                        for (prediction in response.autocompletePredictions) {
                            chaptersList.add(prediction.getFullText(null).toString())
                        }

                        var addressListAdapter: ProfileAddressListAdapter =
                            ProfileAddressListAdapter(
                                requireContext(),
                                chaptersList,
                                _binding.addressEt,
                                _binding.searchResult,
                                this,
                                this@UpdateAddressFragment
                            )
                        _binding.progressCircular.setVisibility(View.GONE)
                        _binding.searchResult.setVisibility(View.VISIBLE)
                        _binding.searchResult.adapter = addressListAdapter


                    }.addOnFailureListener { exception ->
                        if (exception is ApiException) {
                            _binding.progressCircular.setVisibility(View.GONE)
                            Log.e("TAG", "Place not found: " + exception.statusCode)
                        }
                    }

            }
        }
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
                        if (response!!.isSuccessful && response!!.body() != null) {
                            binding.progressCircular.setVisibility(View.GONE)
                            MyApplication.applicationContext().showToast(
                                true,
                                "Address details changed successfully",
                                MyApplication.applicationContext()
                            )
                            dismiss()
                            requireActivity().setResult(RESULT_OK)
                            requireActivity().finish()
                        } else {
                            if (response!!.code() == MyApplication.applicationContext().SESSION) {
                                MyApplication.applicationContext().sessionSignIn { result ->
                                    if (result) {
                                        sendAddress(address, binding)
                                    } else {
                                        binding.progressCircular.setVisibility(View.GONE)
                                        MyApplication.applicationContext().showInvalidErrorToast()
                                    }
                                }
                                Log.d("res", "res")
                            } else {
                                binding.progressCircular.setVisibility(View.GONE)
                                MyApplication.applicationContext().showInvalidErrorToast()
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
            MyApplication.applicationContext().showInvalidErrorToast()
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


    override fun onDetach() {
        super.onDetach()
    }

    companion object {
        const val TAG = "ActionBottomDialog"
        fun newInstance(address: String): UpdateAddressFragment {


            var fragment: UpdateAddressFragment = UpdateAddressFragment()
            val args = Bundle()
            args.putString("address", address)
            fragment.setArguments(args)
            return fragment

            return UpdateAddressFragment()
        }
    }
}