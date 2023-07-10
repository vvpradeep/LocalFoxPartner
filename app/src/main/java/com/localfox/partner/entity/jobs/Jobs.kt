package com.localfox.partner.entity

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class Jobs (

  @SerializedName("location"    ) var location    : Location?           = Location(),
  @SerializedName("_id"         ) var Id          : String?             = null,
  @SerializedName("customer"    ) var customer    : Customer?           = Customer(),
  @SerializedName("partners"    ) var partners    : ArrayList<String> = arrayListOf(),
  @SerializedName("description" ) var description : String?             = null,
  @SerializedName("type"        ) var type        : String?             = null,
  @SerializedName("category"    ) var category    : Category?           = Category(),
  @SerializedName("service"     ) var service     : Service?            = Service(),
  @SerializedName("urgency"     ) var urgency     : String?             = null,
  @SerializedName("images"      ) var images      : ArrayList<String>   = arrayListOf(),
  @SerializedName("status"      ) var status      : String?             = null,
  @SerializedName("createdDate" ) var createdDate : String?             = null

) : Serializable