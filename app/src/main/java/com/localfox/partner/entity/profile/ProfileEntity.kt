package com.localfox.partner.entity.profile

import com.google.gson.annotations.SerializedName


data class ProfileEntity (

  @SerializedName("success" ) var success : Boolean? = null,
  @SerializedName("data"    ) var data    : Data?    = Data()

)