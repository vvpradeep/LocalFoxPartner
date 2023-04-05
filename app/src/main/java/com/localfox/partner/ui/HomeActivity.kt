package com.localfox.partner.ui


import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.gson.Gson
import com.localfox.partner.R
import com.localfox.partner.app.ApiUtils
import com.localfox.partner.app.MyApplication
import com.localfox.partner.databinding.ActivityHomeBinding
import com.localfox.partner.entity.JobsList
import com.localfox.partner.ui.fragments.LeadsFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    public val _binding get() = binding
    public var jobsData1: JobsList? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }


        binding.bottomNavigation.menu.getItem(0).isChecked = true
        val navController = findNavController(com.localfox.partner.R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        binding.bottomNavigation.setupWithNavController(navController)
        getjobs(binding)

    }


    override fun onWindowFocusChanged(hasFocus: Boolean) {

        var lMain: ConstraintLayout = findViewById(R.id.parent_ll);
        lMain.getLayoutParams().height = lMain.getHeight() - getNavigationBarHeight();
        super.onWindowFocusChanged(hasFocus)
    }

    fun getNavigationBarHeight(): Int {
        var resources = getResources();
        var resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    override fun onBackPressed() {

    }

    fun navigationPosition(position: Int) {
        binding.bottomNavigation.menu.getItem(position).isChecked = true
    }


    fun getjobs(binding: ActivityHomeBinding) {
        binding.progressCircular.setVisibility(View.VISIBLE)
        try {

            var headers = mutableMapOf<String, String>()
            headers["Content-Type"] = "application/json"
            headers["Authorization"] = "Bearer " + MyApplication.applicationContext().getUserToken()

            val call: Call<JobsList> = ApiUtils.apiService.getJobs(headers, 1, 10)
            call.enqueue(object : Callback<JobsList> {
                override fun onResponse(
                    call: Call<JobsList>?, response: Response<JobsList>?
                ) {
                    binding.progressCircular.setVisibility(View.GONE)
                    if (response!!.isSuccessful && response!!.body() != null) {
                        val gson = Gson()
                        val json = gson.toJson(response.body()) //
                        jobsData1 = response.body()!!
                        sendDataToFragment(response.body()!!)
                    } else {
                        MyApplication.applicationContext().showInvalidErrorToast()
                    }
                }

                override fun onFailure(call: Call<JobsList>?, t: Throwable?) {
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

    private fun sendDataToFragment(jobsData1: JobsList?) {
        val navController: NavController =
            Navigation.findNavController(this, com.localfox.partner.R.id.nav_host_fragment)

        val ff: List<Fragment> = supportFragmentManager.getFragments()

        val fragment: Fragment = ff.get(0).getChildFragmentManager().getFragments().get(0)

        if (fragment is LeadsFragment && fragment != null) {
            fragment.updateData(jobsData1)
        }
    }
}