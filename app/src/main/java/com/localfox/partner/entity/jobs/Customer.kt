package com.localfox.partner.entity

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class Customer (

  @SerializedName("fullName"     ) var fullName     : String? = null,
  @SerializedName("mobileNumber" ) var mobileNumber : String? = null,
  @SerializedName("emailAddress" ) var emailAddress : String? = null

): Serializable