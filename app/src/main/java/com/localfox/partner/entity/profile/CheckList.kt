package com.localfox.partner.entity.profile

import com.google.gson.annotations.SerializedName


data class CheckList (

  @SerializedName("isBusinessDetailsCaptured" ) var isBusinessDetailsCaptured : Boolean? = null,
  @SerializedName("isContractSigned"          ) var isContractSigned          : Boolean? = null,
  @SerializedName("isLicenceVerified"         ) var isLicenceVerified         : Boolean? = null,
  @SerializedName("isIdCheckComplete"         ) var isIdCheckComplete         : Boolean? = null,
  @SerializedName("isPoliceCheckComplete"     ) var isPoliceCheckComplete     : Boolean? = null,
  @SerializedName("isInsuranceVerified"       ) var isInsuranceVerified       : Boolean? = null

)