package com.localfox.partner.entity

import com.google.gson.annotations.SerializedName

class MobileVerificationEntity {

    @SerializedName("success")
    var success: String? = null
    @SerializedName("mobileVerificationReference")
    var mobileVerificationReference: String? = null
    @SerializedName("data")
    var data: String? = null
}