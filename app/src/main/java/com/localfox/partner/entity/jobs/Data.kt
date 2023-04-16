package com.localfox.partner.entity

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class Data (

  @SerializedName("jobs"          ) var jobs          : ArrayList<Jobs>          = arrayListOf(),
  @SerializedName("jobInviations" ) var jobInviations : ArrayList<JobInviations> = arrayListOf()

): Serializable