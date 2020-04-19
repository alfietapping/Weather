package com.example.weather

import androidx.lifecycle.LiveData

class CityRepository (private val cityDao: CityDao) {

    val allCities: LiveData<List<City>> = cityDao.getAllCities()

    fun insert(city: City) {
        cityDao.insert(city)
    }

    fun insertAllCities(city: List<City>) {
        cityDao.insertAllCities(city)
    }

    fun update(city: City) {
        cityDao.update(city)
    }

    fun delete(city: City) {
        cityDao.delete(city)
    }
}