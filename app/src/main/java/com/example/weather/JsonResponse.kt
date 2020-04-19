package com.example.weather

import androidx.room.Entity
import com.google.gson.annotations.SerializedName


data class JsonResponse
    (
    val weather: List<Weather>,
    @SerializedName("main") val temperature: Temperature,
    @SerializedName("timezone") val secondsOffsetFromUtc: Int,
    @SerializedName("name") val locationName: String
)