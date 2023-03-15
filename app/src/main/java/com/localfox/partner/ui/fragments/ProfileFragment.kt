package com.localfox.partner.ui.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.localfox.partner.R
import com.localfox.partner.app.ApiUtils
import com.localfox.partner.app.MyApplication
import com.localfox.partner.databinding.FragmentProfileBinding
import com.localfox.partner.entity.profile.ProfileEntity
import com.localfox.partner.ui.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream

class ProfileFragment : Fragment() {
    private lateinit var _binding: FragmentProfileBinding
    private val binding get() = _binding
    var imagePath: String? = null
    private var mUri: Uri? = null
    private var profilePhoto: String = ""
    private var catId: String = ""
    var uri: Uri? = null
    var imageUri: Uri? = null

    companion object {
        //image pick code
        private val IMAGE_PICK_CODE = 1000

        //Permission code
        private val GALARY_PERMISSION_CODE = 1001
        private val CAMERA_PERMISSION_CODE = 1002
        private val CAMERA_PICK_CODE = 1003

        //1000 * 1024 = 2 MB
        private const val MAX_IMAGE_SIZE = 1024000
    }


    @Throws(IOException::class)
    fun getBytes(inputs: InputStream): ByteArray? {
        val byteBuff = ByteArrayOutputStream()
        val buffSize = 1024
        val buff = ByteArray(buffSize)
        var len = 0
        while (inputs.read(buff).also { len = it } != -1) {
            byteBuff.write(buff, 0, len)
        }
        return byteBuff.toByteArray()
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

        binding.logoffTv.setOnClickListener {
            logoutDialog()
        }
        setData()

    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).navigationPosition(2)
    }

    fun setData() {
        var data: String = MyApplication.applicationContext()
            .getStringPrefsData(MyApplication.applicationContext().PROFILE_DATA)
        val gson = Gson()
        var profileData: ProfileEntity = gson.fromJson(
            data,
            ProfileEntity::class.java
        )

        binding.profileImage.setOnClickListener {
            selectImage(profileData.data!!.profilePhoto.toString())
        }

        if (TextUtils.isEmpty(imagePath))
            Glide.with(requireContext())
                .load(profileData.data?.profilePhoto)
                .into(binding.profileImage)
        else {
            binding.profileImage?.setImageURI(Uri.parse(imagePath))
        }

        binding.nameTv.setText(profileData.data?.firstName)
        binding.roleTv.setText(profileData.data?.role);
        binding.addressTv.setText(profileData.data?.address)


    }

    fun logoutDialog() {
        val builder = AlertDialog.Builder(requireContext())
        //set title for alert dialog
        builder.setTitle("Logout")
        //set message for alert dialog
        builder.setMessage("Are you sure you want to logout?")


        //performing positive action
        builder.setPositiveButton("Yes") { dialogInterface, which ->
            logout()
            var intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            MyApplication.applicationContext()
                .saveStringPrefsData("", MyApplication.applicationContext().LOGIN_DATA)
            MyApplication.applicationContext()
                .saveStringPrefsData("", MyApplication.applicationContext().EMAIL)
            MyApplication.applicationContext()
                .saveStringPrefsData("", MyApplication.applicationContext().PASSWORD)
            MyApplication.applicationContext()
                .saveStringPrefsData("false", MyApplication.applicationContext().ISVAIDLOGIN)
            MyApplication.applicationContext().client.cache()!!.evictAll()

            startActivity(intent)

        }
//        //performing cancel action
//        builder.setNeutralButton("Cancel"){dialogInterface , which ->
//
//        }
        //performing negative action
        builder.setNegativeButton("No") { dialogInterface, which ->

        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()

    }

    fun logout() {
        try {
            var headers = mutableMapOf<String, String>()
            headers["Content-Type"] = "application/json"
            headers["Authorization"] = "Bearer " + MyApplication.applicationContext().getUserToken()

            val call: Call<ResponseBody> = ApiUtils.apiService.logout(headers)
            call.enqueue(
                object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>?,
                        response: Response<ResponseBody>?
                    ) {
                        if (response!!.isSuccessful) {

                        } else {

                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {

                    }
                })
        } catch (e: Exception) {
            Log.d("response", "Exception " + e.printStackTrace())
        }
    }

    fun selectImage(profileId: String) {

        var options: Array<String> =
            arrayOf("View Profile Photo", "Take Photo", "Choose from Gallery", "Cancel")

        val builder = AlertDialog.Builder(requireContext())
        //set title for alert dialog
//         builder.setTitle(R.string.dialogTitle)
        //set message for alert dialog
//         builder.setMessage(R.string.dialogMessage)
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        // Set items form alert dialog
        builder.setItems(options, { dialog, which ->
            if (options[which].equals("View Profile Photo")) {

                var intent = Intent(requireContext(), ProfilePhotoActivity::class.java)
                if (mUri == null) {
                    intent.putExtra("id", profileId)
                    intent.putExtra("islocal", false)
                } else {
                    intent.putExtra("id", mUri.toString())
                    intent.putExtra("islocal", true)
                }
                startActivity(intent)
            } else if (options[which].equals("Take Photo")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ) ==
                        PackageManager.PERMISSION_DENIED
                    ) {
                        //permission denied
                        val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        //show popup to request runtime permission
                        requestPermissions(permissions, CAMERA_PERMISSION_CODE);
                    } else {
                        capturePhoto()
                    }
                } else {
                    capturePhoto()
                }
            } else if (options[which].equals("Choose from Gallery")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ) ==
                        PackageManager.PERMISSION_DENIED
                    ) {
                        //permission denied
                        val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE);
                        //show popup to request runtime permission
                        requestPermissions(permissions, GALARY_PERMISSION_CODE);
                    } else {
                        //permission already granted
                        pickImageFromGallery()
                    }
                } else {
                    //system OS is < Marshmallow
                    pickImageFromGallery()
                }
            } else if (options[which].equals("Cancel")) {
                dialog.dismiss();
            }
        })

        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    private fun getRealPathFromURI(contentURI: Uri, context: Context): String {
//        val result: String
//        val cursor = context.contentResolver.query(contentURI, null, null, null, null)
//        if (cursor == null) { // Source is Dropbox or other similar local file path
//            result = contentURI.path.toString()
//        } else {
//            cursor.moveToFirst()
//            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
//            result = cursor.getString(idx)
//            cursor.close()
//        }
//        return result
        val projection =
            arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? =
            requireActivity().contentResolver.query(contentURI, projection, null, null, null)

        if (cursor == null) { // Source is Dropbox or other similar local file path
            return contentURI.path.toString()
        } else {
            val column_index: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            val columnIndex: Int = cursor.getColumnIndex(projection[0])
            if (cursor.getString(columnIndex) != null) {
                val filePath: String = cursor.getString(columnIndex)
                cursor.close()
                return filePath
            } else {
                return contentURI.path.toString()
            }
        }
    }

    private fun capturePhoto() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        mUri = requireActivity().contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values
        )
        //camera intent
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mUri)
        startActivityForResult(cameraIntent, CAMERA_PICK_CODE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            imagePath = data?.data.toString()
            uri = data?.data
            imageUri = uri
            mUri = imageUri
            requireActivity().runOnUiThread { binding.profileImage.setImageURI(data?.data) }
            addProfilePhoto()
        } else if (resultCode == Activity.RESULT_OK && requestCode == GALARY_PERMISSION_CODE) {
            pickImageFromGallery()

        } else if (resultCode == Activity.RESULT_OK && requestCode == CAMERA_PERMISSION_CODE) {
            capturePhoto()
        } else if (resultCode == Activity.RESULT_OK && requestCode == CAMERA_PICK_CODE) {

            imagePath = mUri.toString()
            uri = mUri
            imageUri = uri

            requireActivity().runOnUiThread { binding.profileImage.setImageURI(mUri) }
            addProfilePhoto()
        }
    }

    fun getPath(uri: Uri?): String? {
        val projection =
            arrayOf(MediaStore.Images.Media.DATA)
        val cursor = requireContext().contentResolver.query(uri!!, projection, null, null, null)
            ?: return null
        val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        val s = cursor.getString(column_index)
        cursor.close()
        return s
    }

    fun addProfilePhoto() {
        try {
            var headers = mutableMapOf<String, String>()
            headers["Authorization"] = "Bearer " + MyApplication.applicationContext().getUserToken()


            var photo = File(getPath(uri!!))
//            try {
//                do {
//                    photo = compressFile(photo)
//                } while (photo.length() > MAX_IMAGE_SIZE)
//            } catch (e: IOException) {
//                var photo = File(getRealPathFromURI(requireContext(), uri))
//                do {
//                    photo = compressFile(photo)
//                } while (photo.length() > MAX_IMAGE_SIZE)
//            }

            var requestFile: RequestBody =
                RequestBody.create(MediaType.parse("image/*"), photo)

            var requestImage: MultipartBody.Part =
                MultipartBody.Part.createFormData("file", "photo.jpg", requestFile);

            val call: Call<ResponseBody> =
                ApiUtils.apiService.uploadProfilePhoto(headers, requestImage)
            call.enqueue(
                object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>?,
                        response: Response<ResponseBody>?
                    ) {

//                            progress_circular.setVisibility(View.GONE)
                        if (response!!.isSuccessful) {
                            Log.d("response", "response isSuccessful")
                            val gson = Gson()
                            val json = gson.toJson(response.body()) //
                            MyApplication.applicationContext().showToast(
                                true,
                                "Your profile image is successfully uploaded.",
                                requireContext()
                            )
                        } else {
                            mUri = null
                            imagePath = ""
                            try {
                                if (response.code() == MyApplication.applicationContext().SESSION) {
                                    // MyApplication.applicationContext().sessionSignIn()
                                } else {
                                    val jObjError = JSONObject(response.errorBody()?.string())
                                    MyApplication.applicationContext()
                                        .showErrorToast(jObjError.getString("error"))
                                }
                            } catch (e: Exception) {
                                MyApplication.applicationContext()
                                    .showErrorToast("Unable to upload the image. Please try again with another image.")

                            }
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                        mUri = null
//                            progress_circular.setVisibility(View.GONE)
                        imagePath = ""
                        MyApplication.applicationContext().showInvalidErrorToast()
                        Log.d("response", "onFailure ")

                    }
                })
        } catch (e: Exception) {
            mUri = null
            MyApplication.applicationContext().showToast(
                false,
                "Unable to upload the image. Please try again with another image.",
                requireContext()
            )
//                progress_circular.setVisibility(View.GONE)
            Log.d("response", "Exception " + e.printStackTrace())
        }

    }

}