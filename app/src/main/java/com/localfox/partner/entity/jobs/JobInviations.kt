package com.localfox.partner.entity

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class JobInviations (

  @SerializedName("_id" ) var Id  : String? = null,
  @SerializedName("job" ) var job : Job?    = Job()

) : Serializable