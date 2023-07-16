package com.localfox.partner.ui.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.localfox.partner.databinding.FragmentSearchBinding
import com.localfox.partner.entity.Jobs
import com.localfox.partner.ui.HomeActivity
import com.localfox.partner.ui.JobDetailsActivity
import com.localfox.partner.ui.adapter.JobsAdapter


class SearchFragment : Fragment(), JobsAdapter.OnItemClickListener  {
    private lateinit var _binding: FragmentSearchBinding
    private val binding get() = _binding
    private var adapter : JobsAdapter? = null
    var phoneNumber: String = "";

    private val CALL_PERMISSION_REQUEST_CODE = 1010

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        _binding.root.viewTreeObserver.addOnGlobalFocusChangeListener { _, newFocus ->
            if (newFocus ==  _binding.root) {
                // Fragment has gained focus
                showKeyboard()
            } else {
                // Fragment has lost focus
                dismissKeyboard()
            }
        }


        // this creates a vertical layout Manager
        _binding.jobsRecyclerview.layoutManager = LinearLayoutManager(activity)
        _binding.jobsRecyclerview.setNestedScrollingEnabled(false);


        var normalLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        _binding.jobsRecyclerview.layoutParams = normalLayoutParams;



        _binding.searchEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (adapter != null) adapter!!.filter(s.toString()!!)
            }
        })

        return _binding.root
    }

    override fun onResume() {
        val activity = requireActivity() as HomeActivity
        if (activity.jobsList != null && activity.jobsList!!.size  > 0) {
            adapter = JobsAdapter(activity.jobsList, requireContext()!!, this, true)
            _binding.jobsRecyclerview.adapter = adapter
        }

        binding.searchEt.requestFocus()
        showKeyboard(binding.searchEt)
        super.onResume()
    }
    override fun onPause() {
        super.onPause()
        dismissKeyboard()
    }

    private fun showKeyboard() {
        val view = view // Get the fragment's root view
        if (view != null) {
         var inputMethodManager =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun showKeyboard(editText: EditText) {
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun dismissKeyboard() {
        val view = view // Get the fragment's root view
        if (view != null) {
           var inputMethodManager =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == CALL_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission granted
            // Retry the phone call after permission is granted
            makePhoneCall(phoneNumber)
        } else {
            // Permission denied
            Toast.makeText(requireContext(), "Call permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun makePhoneCall(phoneNumber: String) {
        val callIntent = Intent(Intent.ACTION_CALL)
        callIntent.data = Uri.parse("tel:$phoneNumber")
        startActivity(callIntent)
    }

    override fun onCallClick(phoneNumber: String) {
        this.phoneNumber = phoneNumber;
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CALL_PHONE),
                CALL_PERMISSION_REQUEST_CODE
            )
        } else {
            makePhoneCall(phoneNumber)
        }
    }

    override fun onMailClick(mailID: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_EMAIL, arrayOf(mailID))
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    override fun onLocationClick(location: String, lat: Double, long: Double) {
        val geoUri = Uri.parse("geo:$lat,$long")
        val intent = Intent(Intent.ACTION_VIEW, geoUri)
        val shareIntent = Intent.createChooser(intent, null)
        startActivity(shareIntent)
    }

    override fun onItemClick(job: Jobs) {
        val intent = Intent(
            activity,
            JobDetailsActivity::class.java
        )
        intent.putExtra("Jobs", job)
        startActivity(intent)
    }

    override fun getNewPageData() {

    }
}
