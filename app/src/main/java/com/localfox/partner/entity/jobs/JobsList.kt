package com.localfox.partner.entity

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class JobsList (

  @SerializedName("success"          ) var success          : Boolean? = null,
  @SerializedName("pageNumber"       ) var pageNumber       : Int?     = null,
  @SerializedName("pageSize"         ) var pageSize         : Int?     = null,
  @SerializedName("jobsCount"        ) var jobsCount        : Int?     = null,
  @SerializedName("invitationsCount" ) var invitationsCount : Int?     = null,
  @SerializedName("data"             ) var data             : Data?    = Data()

) : Serializable {

}


