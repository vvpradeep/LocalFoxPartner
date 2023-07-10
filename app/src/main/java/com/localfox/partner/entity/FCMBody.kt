package com.localfox.partner.entity

import com.google.gson.annotations.SerializedName

class FCMBody {

    @SerializedName("_id")
    var _id: String? = null


    @SerializedName("fcmToken")
    var fcmToken: String? = null
}