package com.example.weather

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CityDao {

    @Insert
    fun insert(city: City)

    @Insert
    fun insertAllCities(city: List<City>)

    @Update
    fun update(city: City)

    @Delete
    fun delete(city: City)

    @Query("SELECT * FROM city_table")
    fun getAllCities(): LiveData<List<City>>

}