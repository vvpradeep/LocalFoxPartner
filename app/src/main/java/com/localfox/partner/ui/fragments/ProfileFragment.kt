package com.localfox.partner.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.localfox.partner.R
import com.localfox.partner.databinding.FragmentProfileBinding
import com.localfox.partner.ui.HomeActivity
import com.localfox.partner.ui.NotificationActivity
import com.localfox.partner.ui.ProfileSettingsActivity
import com.localfox.partner.ui.SecurityActivity

class ProfileFragment : Fragment() {
    private lateinit var _binding: FragmentProfileBinding
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        onClickViews()
        return binding.root
    }

    fun onClickViews() {

        binding.profileNotificationLl.setOnClickListener() {
            val intent = Intent(requireActivity(), NotificationActivity::class.java)
            startActivity(intent)
        }

        binding.profileSecurityLl.setOnClickListener() {
            val intent = Intent(requireActivity(), SecurityActivity::class.java)
            startActivity(intent)
        }

        binding.profileSettingsLl.setOnClickListener() {
            val intent = Intent(requireActivity(), ProfileSettingsActivity::class.java)
            startActivity(intent)
        }

    }
    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).navigationPosition(2)
    }

}