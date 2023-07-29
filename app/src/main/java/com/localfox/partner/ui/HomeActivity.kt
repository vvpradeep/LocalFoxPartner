package com.localfox.partner.ui


import android.content.Intent
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
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import com.localfox.partner.R
import com.localfox.partner.app.ApiUtils
import com.localfox.partner.app.MyApplication
import com.localfox.partner.databinding.ActivityHomeBinding
import com.localfox.partner.entity.FCMResponse
import com.localfox.partner.entity.Jobs
import com.localfox.partner.entity.JobsList
import com.localfox.partner.entity.profile.ProfileEntity
import com.localfox.partner.ui.fragments.LeadsFragment
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    public val _binding get() = binding
    public var jobsData1: JobsList? = null
    public  var pageNumber : Int = 1
    public var pageSize : Int = 1000
    public var jobsList : ArrayList<Jobs>  = arrayListOf()
    public var lastDataFetched  = false
    public var isDimensionApplied  = false

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
        var data: String = MyApplication.applicationContext()
            .getStringPrefsData(MyApplication.applicationContext().PROFILE_DATA)
        val gson = Gson()
        var profileData: ProfileEntity = gson.fromJson(
            data,
            ProfileEntity::class.java
        )
        if (!(profileData!!.data!!.isApproved!!)) {
            finish()
            startActivity(Intent(this,AccountInActiveActivity::class.java).putExtra("name", profileData!!.data!!.firstName))
        } else {
            getjobs(binding, pageNumber, pageSize)
        }

        if (MyApplication.applicationContext()
                .getStringPrefsData(MyApplication.applicationContext().FCM_TOKEN) == null ||
            MyApplication.applicationContext()
                .getStringPrefsData(MyApplication.applicationContext().FCM_TOKEN).equals("null")|| MyApplication.applicationContext()
                .getStringPrefsData(MyApplication.applicationContext().FCM_TOKEN).length <= 1 ) {
            registerFCM()
        }

    }

    fun registerFCM() {
        try {

            var headers = mutableMapOf<String, String>()
            headers["Content-Type"] = "application/json"

            var manufacturer: String = Build.MANUFACTURER;

            var model: String = Build.MODEL

            val json = JSONObject()
            val idToken = FirebaseInstanceId.getInstance().token
            json.put("fcmToken", idToken)
            json.put("deviceOS", "ANDRIOD")
            json.put("deviceOSVersion", ""+android.os.Build.VERSION.SDK_INT)
            json.put("deviceModel", manufacturer + " "+model)
            json.put("appVersion", "1.1")

            val requestBody: RequestBody =
                RequestBody.create(MediaType.parse("application/json"), json.toString())

            val call: Call<FCMResponse> = ApiUtils.apiService.registerFcmToken(requestBody ,headers)
            call.enqueue(
                object : Callback<FCMResponse> {
                    override fun onResponse(
                        call: Call<FCMResponse>?,
                        response: Response<FCMResponse>?
                    ) {

                        if (response!!.isSuccessful) {

                            val gson = Gson()
                            var json =  gson.toJson(response.body())
                            val data: FCMResponse = gson.fromJson(json, FCMResponse::class.java) //
                            MyApplication.applicationContext().saveStringPrefsData(data!!.fcmbody!!._id.toString(), MyApplication.applicationContext().FCM_TOKEN)

                        }
                    }

                    override fun onFailure(call: Call<FCMResponse>?, t: Throwable?) {

                        Log.d("response", "onFailure ")

                    }
                })
        } catch (e: Exception) {
            Log.d("response", "Exception " + e.printStackTrace())
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        if (!isDimensionApplied) {
            isDimensionApplied = true;
            var lMain: ConstraintLayout = findViewById(R.id.parent_ll);
            lMain.getLayoutParams().height = lMain.getHeight() - getNavigationBarHeight();
        }
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


    fun getjobs(binding: ActivityHomeBinding, pageNumber : Int, pageSize: Int) {
        binding.progressCircular.setVisibility(View.VISIBLE)
        try {

            var headers = mutableMapOf<String, String>()
            headers["Content-Type"] = "application/json"
            headers["Authorization"] = "Bearer " + MyApplication.applicationContext().getUserToken()

            val call: Call<JobsList> = ApiUtils.apiService.getJobs(headers, pageNumber, pageSize)
            call.enqueue(object : Callback<JobsList> {
                override fun onResponse(
                    call: Call<JobsList>?, response: Response<JobsList>?
                ) {
                    binding.progressCircular.setVisibility(View.GONE)
                    if (response!!.isSuccessful && response!!.body() != null) {
                        val gson = Gson()
                        val json = gson.toJson(response.body()) //
                        jobsData1 = response.body()!!
                        if (jobsData1 != null  && jobsData1!!.data != null && jobsData1!!.data!!.jobs != null &&  jobsData1!!.data!!.jobs!!.size > 0) {
                            jobsList.addAll(jobsData1!!.data!!.jobs)
                            lastDataFetched  = true
                            sendDataToFragment(response.body()!!)
                        } else {
                            lastDataFetched = false
                        }

                    } else {
                        if (response.code() == MyApplication.applicationContext().SESSION) {
                            MyApplication.applicationContext().sessionSignIn{ result ->
                                if (result) {
                                    getjobs(binding, pageNumber, pageSize)
                                }
                            }
                        } else {
                            MyApplication.applicationContext().showInvalidErrorToast()
                        }
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

    public fun getPaginationData() {
        if (lastDataFetched) {
            pageNumber = pageNumber + 1
            getjobs(_binding, pageNumber, pageSize)
        }
    }
}