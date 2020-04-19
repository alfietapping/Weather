package com.example.weather

import com.google.gson.annotations.SerializedName

data class Weather
    (
    @SerializedName("id") val weatherConditionId: String,
    @SerializedName("main") val weatherCondition: String,
    @SerializedName("description") val weatherDescription: String
)