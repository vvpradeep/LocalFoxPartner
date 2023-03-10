package com.localfox.partner.entity

import com.google.gson.annotations.SerializedName

class EmailVerificationEntity {

    @SerializedName("success")
    var success: String? = null
    @SerializedName("emailVerificationReference")
    var emailVerificationReference: String? = null
    @SerializedName("data")
    var data: String? = null
}