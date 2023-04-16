package com.localfox.partner.entity

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class Service (

  @SerializedName("_id"             ) var Id              : String?  = null,
  @SerializedName("serviceName"     ) var serviceName     : String?  = null,
  @SerializedName("category"        ) var category        : String?  = null,
  @SerializedName("isActive"        ) var isActive        : Boolean? = null,
  @SerializedName("createdDate"     ) var createdDate     : String?  = null,
  @SerializedName("lastUpdatedDate" ) var lastUpdatedDate : String?  = null,
  @SerializedName("__v"             ) var _v              : Int?     = null

): Serializable