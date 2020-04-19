package com.example.weather


import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity(tableName = "city_table")
@Serializable
data class City(

    @PrimaryKey
    @SerialName("id")
    val cityId: String,

    @SerialName("country")
    val countryCode: String,

    @SerialName("name")
    val cityName: String

)