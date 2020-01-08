package com.tinfive.nearbyplace.Common

import com.tinfive.nearbyplace.Remote.IGoogleAPIService
import com.tinfive.nearbyplace.Remote.RetrofitClient

object Common {

    private val GOOGLE_API_URL = "https://raw.githubusercontent.com/"

    ////////////////////////

    val googleApiService: IGoogleAPIService
        get() = RetrofitClient.getClient(GOOGLE_API_URL).create(IGoogleAPIService::class.java)


}
