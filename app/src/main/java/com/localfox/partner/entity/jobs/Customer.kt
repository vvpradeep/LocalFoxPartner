package com.localfox.partner.entity

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class Customer (

  @SerializedName("fullName"     ) var fullName     : String? = null,
  @SerializedName("mobileNumber" ) var mobileNumber : String? = null,
  @SerializedName("_id" ) var id : String? = null,
  @SerializedName("profilePhoto" ) var profilePhoto : String? = null,
  @SerializedName("isMobileVerified" ) var isMobileVerified : Boolean? = false,
  @SerializedName("emailAddress" ) var emailAddress : String? = null


): Serializable