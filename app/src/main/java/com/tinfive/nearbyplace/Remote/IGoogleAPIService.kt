package com.tinfive.nearbyplace.Remote

import com.tinfive.nearbyplace.Model.MyPlaces
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface IGoogleAPIService {
    @GET
    fun getNearbyPlaces(@Url url: String): Call<MyPlaces>

    /*@GET("prices?key=743ac78ee6cf61494333b677a1381854")
    fun getData(): Observable<List<RetroCrypto>>*/

    /*@GET("achmade96/masjid/master/db.json")
    fun getData(): Observable<List<Model>>*/
}