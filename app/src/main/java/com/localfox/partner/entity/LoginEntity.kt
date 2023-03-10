package com.localfox.partner.entity

import com.google.gson.annotations.SerializedName

class LoginEntity {

    @SerializedName("success")
    var success: String? = null
    @SerializedName("token")
    var token: String? = null
    @SerializedName("expiry")
    var expiry: String? = null
    @SerializedName("isMobileVerified")
    var isMobileVerified: String? = null
}