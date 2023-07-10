package com.localfox.partner.app;


import com.localfox.partner.entity.*
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

    @POST("/v1/partner/authenticate")
    fun loginPost(@Body request: RequestBody): Call<LoginEntity>

    @POST("/v1/partner/verification/sendMobileCode")
    fun sendMobileCode(@Body request: RequestBody): Call<ResponseBody>

    @POST("/v1/partner/verification/validateMobileCode")
    fun validateMobileCode(@Body request: RequestBody): Call<MobileVerificationEntity>

    @POST("/v1/partner/verification/sendEmailCode")
    fun sendEmailCode(@Body request: RequestBody): Call<ResponseBody>

    @POST("/v1/partner/verification/validateEmailCode")
    fun validateEmailCode(@Body request: RequestBody): Call<EmailVerificationEntity>

    @POST("/v1/partner/registerPartner")
    fun registerPartner(@Body request: RequestBody): Call<ResponseBody>

    @POST("/v1/partner/resetPassword")
    fun resetPassword(@Body request: RequestBody): Call<ResponseBody>

    @POST("/v1/partner/verification/validateResetPasswordCode")
    fun validateResetPasswordCode(@Body request: RequestBody): Call<EmailVerificationEntity>

    @POST("/v1/partner/setNewPassword")
    fun setNewPassword(@Body request: RequestBody): Call<ResponseBody>

    @GET("/v1/partner/profile/getProfile")
    fun getProfile(@HeaderMap headers: Map<String, String>): Call<ProfileEntity>

    @PUT("/v1/partner/profile/updateNotificationSettings")
    fun updateNotifications(@Body request: RequestBody, @HeaderMap headers: Map<String, String>): Call<ResponseBody>

    @PUT("/v1/partner/profile/updateMobileNumber")
    fun updateMobileNumber(@Body request: RequestBody, @HeaderMap headers: Map<String, String>): Call<ResponseBody>

    @PUT("/v1/partner/profile/updateAddress")
    fun updateAddress(@Body request: RequestBody, @HeaderMap headers: Map<String, String>): Call<ResponseBody>

    @Multipart
    @POST("/v1/partner/profile/uploadProfilePhoto")
    fun uploadProfilePhoto(@HeaderMap headers: Map<String, String>, @Part photo: MultipartBody.Part): Call<ResponseBody>

    @PUT("/v1/partner/logout")
    fun logout(@HeaderMap headers: Map<String, String>): Call<ResponseBody>

    @GET("/v1/partner/jobs/getJobs?")
    fun getJobs(@HeaderMap headers: Map<String, String>, @Query("pageNumber") pageNumber: Int,  @Query("pageSize") pageSize: Int): Call<JobsList>

    @POST("/v1/partner/jobs/acceptJob/{id}")
    fun acceptJob(@HeaderMap headers: Map<String, String>, @Path("id") id: String): Call<JobsList>

    @POST("/v1/partner/jobs/declineJob/{id}")
    fun declineJob(@HeaderMap headers: Map<String, String>, @Path("id") id: String): Call<JobsList>

    @POST("/v1/partner/fcmToken/registerFcmToken")
    fun registerFcmToken(@Body request: RequestBody, @HeaderMap headers: Map<String, String>): Call<FCMResponse>

    @PUT("/v1/partner/fcmToken/linkPartner/{id}")
    fun linkPartner(@HeaderMap headers: Map<String, String>, @Path("id") id: String): Call<ResponseBody>


}



