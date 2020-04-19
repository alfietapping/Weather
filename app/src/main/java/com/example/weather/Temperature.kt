package com.example.weather

import com.google.gson.annotations.SerializedName

data class Temperature
    (
    @SerializedName("temp") val temperatureInKelvin: String,
    @SerializedName("pressure") val atmosphericPressure: String,
    @SerializedName("humidity") val humidityPercent: String
)