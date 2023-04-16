package com.localfox.partner.entity

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class Category (

  @SerializedName("_id"             ) var Id              : String?  = null,
  @SerializedName("categoryName"    ) var categoryName    : String?  = null,
  @SerializedName("categoryCode"    ) var categoryCode    : String?  = null,
  @SerializedName("isActive"        ) var isActive        : Boolean? = null,
  @SerializedName("isPopular"       ) var isPopular       : Boolean? = null,
  @SerializedName("createdDate"     ) var createdDate     : String?  = null,
  @SerializedName("lastUpdatedDate" ) var lastUpdatedDate : String?  = null,
  @SerializedName("__v"             ) var _v              : Int?     = null,
  @SerializedName("categoryImage"   ) var categoryImage   : String?  = null

): Serializable