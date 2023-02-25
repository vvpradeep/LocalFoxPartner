package com.localfox.partner.app;


import com.localfox.partner.entity.LoginEntity
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
    fun validateMobileCode(@Body request: RequestBody): Call<ResponseBody>

    @POST("/api/v1/partner/verification/sendEmailCode")
    fun sendEmailCode(@Body request: RequestBody): Call<ResponseBody>

    @POST("/api/v1/partner/verification/validateEmailCode")
    fun validateEmailCode(@Body request: RequestBody): Call<ResponseBody>

    @POST("/api/v1/partner/registerPartner")
    fun registerPartner(@Body request: RequestBody): Call<ResponseBody>

}



