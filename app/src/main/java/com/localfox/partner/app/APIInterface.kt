package com.localfox.partner.app;


import com.localfox.partner.entity.EmailVerificationEntity
import com.localfox.partner.entity.LoginEntity
import com.localfox.partner.entity.MobileVerificationEntity
import com.localfox.partner.entity.profile.ProfileEntity
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

/**
 * Created by anupamchugh on 09/01/17.
 */

interface APIInterface {

    @POST("/api/v1/partner/authenticate")
    fun loginPost(@Body request: RequestBody): Call<LoginEntity>

    @POST("/api/v1/partner/verification/sendMobileCode")
    fun sendMobileCode(@Body request: RequestBody): Call<ResponseBody>

    @POST("/api/v1/partner/verification/validateMobileCode")
    fun validateMobileCode(@Body request: RequestBody): Call<MobileVerificationEntity>

    @POST("/api/v1/partner/verification/sendEmailCode")
    fun sendEmailCode(@Body request: RequestBody): Call<ResponseBody>

    @POST("/api/v1/partner/verification/validateEmailCode")
    fun validateEmailCode(@Body request: RequestBody): Call<EmailVerificationEntity>

    @POST("/api/v1/partner/registerPartner")
    fun registerPartner(@Body request: RequestBody): Call<ResponseBody>

    @POST("/api/v1/partner/resetPassword")
    fun resetPassword(@Body request: RequestBody): Call<ResponseBody>

    @POST("/api/v1/partner/verification/validateResetPasswordCode")
    fun validateResetPasswordCode(@Body request: RequestBody): Call<EmailVerificationEntity>

    @POST("/api/v1/partner/setNewPassword")
    fun setNewPassword(@Body request: RequestBody): Call<ResponseBody>

    @GET("/api/v1/partner/profile/getProfile")
    fun getProfile(@HeaderMap headers: Map<String, String>): Call<ProfileEntity>

    @PUT("/api/v1/partner/profile/updateNotificationSettings")
    fun updateNotifications(@Body request: RequestBody, @HeaderMap headers: Map<String, String>): Call<ResponseBody>

    @PUT("/api/v1/partner/profile/updateMobileNumber")
    fun updateMobileNumber(@Body request: RequestBody, @HeaderMap headers: Map<String, String>): Call<ResponseBody>

    @PUT("/api/v1/partner/profile/updateAddress")
    fun updateAddress(@Body request: RequestBody, @HeaderMap headers: Map<String, String>): Call<ResponseBody>

    @Multipart
    @POST("/api/v1/partner/profile/uploadProfilePhoto")
    fun uploadProfilePhoto(@HeaderMap headers: Map<String, String>, @Part photo: MultipartBody.Part): Call<ResponseBody>

    @PUT("/api/v1/partner/logout")
    fun logout(@HeaderMap headers: Map<String, String>): Call<ResponseBody>


}



