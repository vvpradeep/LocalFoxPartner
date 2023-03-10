package com.localfox.partner.entity.profile

import com.google.gson.annotations.SerializedName


data class Data (

  @SerializedName("location"             ) var location             : Location?             = Location(),
  @SerializedName("checkList"            ) var checkList            : CheckList?            = CheckList(),
  @SerializedName("NotificationSettings" ) var NotificationSettings : NotificationSettings? = NotificationSettings(),
  @SerializedName("_id"                  ) var Id                   : String?               = null,
  @SerializedName("firstName"            ) var firstName            : String?               = null,
  @SerializedName("lastName"             ) var lastName             : String?               = null,
  @SerializedName("emailAddress"         ) var emailAddress         : String?               = null,
  @SerializedName("isMobileVerified"     ) var isMobileVerified     : Boolean?              = null,
  @SerializedName("isEmailVerified"      ) var isEmailVerified      : Boolean?              = null,
  @SerializedName("mobileNumber"         ) var mobileNumber         : String?               = null,
  @SerializedName("profilePhoto"         ) var profilePhoto         : String?               = null,
  @SerializedName("isActive"             ) var isActive             : Boolean?              = null,
  @SerializedName("isApproved"           ) var isApproved           : Boolean?              = null,
  @SerializedName("role"                 ) var role                 : String?               = null,
  @SerializedName("serviceArea"          ) var serviceArea          : Int?                  = null,
  @SerializedName("createdDate"          ) var createdDate          : String?               = null,
  @SerializedName("lastUpdatedDate"      ) var lastUpdatedDate      : String?               = null,
  @SerializedName("__v"                  ) var _v                   : Int?                  = null,
  @SerializedName("address"              ) var address              : String?               = null

)