package com.localfox.partner.entity

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class Location (

  @SerializedName("type"             ) var type             : String?           = null,
  @SerializedName("coordinates"      ) var coordinates      : ArrayList<Double> = arrayListOf(),
  @SerializedName("formattedAddress" ) var formattedAddress : String?           = null,
  @SerializedName("unit"             ) var unit             : String?           = null,
  @SerializedName("streetNumber"     ) var streetNumber     : String?           = null,
  @SerializedName("streetName"       ) var streetName       : String?           = null,
  @SerializedName("suburb"           ) var suburb           : String?           = null,
  @SerializedName("state"            ) var state            : String?           = null,
  @SerializedName("postCode"         ) var postCode         : String?           = null,
  @SerializedName("country"          ) var country          : String?           = null,
  @SerializedName("googlePlaceId"    ) var googlePlaceId    : String?           = null

): Serializable