package com.tinfive.nearbyplace.Remote

import com.tinfive.nearbyplace.Model.DataMasjid
import com.tinfive.nearbyplace.Model.MyPlaces
import com.tinfive.nearbyplace.Model.Results
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url
import io.reactivex.Observable


interface IGoogleAPIService {
    @GET
    fun getNearbyPlaces(@Url url: String): Call<MyPlaces>

    /*@GET("location=latitude,longtitude&radius=500&type=mosque&key=AIzaSyBHkbWKsDCZtTUPn-qW-Lzjzmkbj7_1LmY")
    fun getMosque(@Url url: String): Observable<List<Results>>*/

    /*@GET("prices?key=743ac78ee6cf61494333b677a1381854")
    fun getData(): Observable<List<RetroCrypto>>*/

    @GET("achmade96/lokasimasjid/master/datamasjid.json")
    fun getMosque(): Observable<List<Results>>

   /* fun getMosque(): Observable<List<Results>>*/
}