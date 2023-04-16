package com.localfox.partner.entity

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class Partners (

  @SerializedName("emailAddress" ) var emailAddress : String? = null,
  @SerializedName("mobileNumber" ) var mobileNumber : String? = null,
  @SerializedName("profilePhoto" ) var profilePhoto : String? = null

): Serializable