package com.localfox.partner.entity

import com.google.gson.annotations.SerializedName

class FCMResponse {


    @SerializedName("success")
    var success: String? = null

    @SerializedName("data")
    var fcmbody: FCMBody? = null
}