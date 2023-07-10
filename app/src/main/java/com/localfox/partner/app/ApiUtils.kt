package com.localfox.partner.app;


object ApiUtils {

    val BASE_URL = "https://api.localfox.com.au"

    val apiService: APIInterface
        get() = APIClient.getClient(BASE_URL, MyApplication.applicationContext().getAppContext())!!.create(APIInterface::class.java)

}
