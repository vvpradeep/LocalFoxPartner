package com.localfox.partner.entity

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class RegistrartionEntity: Serializable{

    @SerializedName("firstName")
    var firstName: String? = null
    @SerializedName("lastName")
    var lastName: String? = null
    @SerializedName("emailAddress")
    var emailAddress: String? = null
    @SerializedName("password")
    var password: String? = null
    @SerializedName("mobileNumber")
    var mobileNumber: String? = null
    @SerializedName("mobileVerificationReference")
    var mobileVerificationReference: String? = null
    @SerializedName("emailVerificationReference")
    var emailVerificationReference: String? = null


}