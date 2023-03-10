package com.localfox.partner.entity.profile

import com.google.gson.annotations.SerializedName


data class NotificationSettings (

  @SerializedName("events"             ) var events             : Boolean? = null,
  @SerializedName("announcements"      ) var announcements      : Boolean? = null,
  @SerializedName("emailNotifications" ) var emailNotifications : Boolean? = null,
  @SerializedName("pushNotifications"  ) var pushNotifications  : Boolean? = null,
  @SerializedName("smsNotifications"   ) var smsNotifications   : Boolean? = null

)