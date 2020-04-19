package com.example.weather

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface JsonApiCall {

    @GET("weather")
    fun getJsonResponse(
        @Query("q") q: String?, @Query("appid") appid: String?
    ): Call<JsonResponse?>?
}