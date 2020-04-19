package com.example.weather


import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.serialization.builtins.ListSerializer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private val popularCities: Lazy<List<String>> = lazy {
        resources.getStringArray(R.array.popularCities).toList()
    }

    private val cityStringList: Lazy<List<String>> = lazy {
        readJson().map {
            it.cityName + ", " + it.countryCode
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getWeather("London, GB")

        locationTv.setOnClickListener{displayCities()}

    }


    private fun getWeather(selectedLocation: String) {

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val jsonApi = retrofit.create(JsonApiCall::class.java)


        val response: Call<JsonResponse?>? =
            jsonApi.getJsonResponse(selectedLocation,
                "c88de9aca4ef112f59e127f756e80f10")

        response?.enqueue(object : Callback<JsonResponse?> {

            override fun onResponse(
                call: Call<JsonResponse?>, response: Response<JsonResponse?>) {
                Log.d("onResponse", "called")

                if (!response.isSuccessful) {
                    Log.d("onResponse", "not successful")
                    return
                } else {
                    Log.d("onResponse", "successful")

                    val jsonResponse  = response.body()

                    jsonResponse?.let {

                        val locationName     = jsonResponse.locationName
                        val secondsOffset    = jsonResponse.secondsOffsetFromUtc
                        val temperature      = jsonResponse.temperature.temperatureInKelvin
                        val weatherCondition = jsonResponse.weather[0].weatherCondition

                        locationTv.text      = locationName

                        setWeatherCondition(weatherCondition, setTime(secondsOffset))
                        setTemperature(temperature)

                    }
                }

            }

            override fun onFailure(call: Call<JsonResponse?>, t: Throwable) {
                Log.d("onFailure", t.toString())
            }

        })
    }

    private fun setTime(secondsOffset: Int): Int {

        val time      = System.currentTimeMillis()
        val localtime = (secondsOffset.times(1000)).plus(time)

        val dateFormat       = SimpleDateFormat("EEEE, MMMM d")
        dateFormat.timeZone  = TimeZone.getTimeZone("UTC")

        val timeFormat       = SimpleDateFormat("HH:mm")
        timeFormat.timeZone  = TimeZone.getTimeZone("UTC")

        val currentDate      = dateFormat.format(Date(localtime))
        val currentTime      = timeFormat.format(Date(localtime))

        dateTv.text          = currentDate
        timeTv.text          = currentTime

        val subString        = currentTime.subSequence(0,2)
        val timeCheck        = Integer.parseInt(subString as String)

        val background       = if (isNightTime(timeCheck))
        {
            R.drawable.nightbackground
        } else {
            R.drawable.daybackground
        }
        screenBackground.setBackgroundResource(background)

        return timeCheck
    }

    private fun setWeatherCondition (weather: String, timeCheck: Int) {

        val condition = when (weather) {

            "Clear"   -> {

                if (isNightTime(timeCheck)) {
                    Pair(R.drawable.ic_moon, "Clear")
                } else {
                    Pair(R.drawable.ic_sun_1, "Sunny")
                }
            }

            "Clouds"  -> Pair(R.drawable.ic_clouds, "Cloudy")

            "Rain"    -> Pair (R.drawable.ic_rain, "Rainy")

             else     -> Pair (R.drawable.ic_question, "")
        }

        conditionImageView.setImageResource(condition.first)
        conditionTv.text = condition.second

    }

    private fun setTemperature (tempInKelvin: String) {

        val tempConversion  = tempInKelvin.toDouble() - 273.15

        val numberFormat    = DecimalFormat.getInstance()
        numberFormat.maximumFractionDigits  = 0

        val tempCelsius = numberFormat.format(tempConversion) + "°"

        temperatureTv.text = tempCelsius
    }

    private fun isNightTime(timeCheck: Int): Boolean {

            if (timeCheck > 19 || timeCheck < 6) {
                return true
        }
        return false
    }

    private fun displayCities() {

        val alertBuilder: AlertDialog = AlertDialog.Builder(this).create()
        val layoutInflater = layoutInflater
        val view = layoutInflater.inflate(R.layout.cities_list__view, null)
        alertBuilder.setView(view)

        val listView = view.findViewById<ListView>(R.id.citiesListView)

        val allCitiesAdapter     =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, cityStringList.value)

        val popularCitiesAdapter =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, popularCities.value)

        listView.adapter = popularCitiesAdapter

        view.findViewById<SearchView>(
            R.id.searchView).setOnQueryTextListener( object: SearchView.OnQueryTextListener {


            override fun onQueryTextChange(newText: String): Boolean {

                    if (newText.isEmpty()) {
                        listView.adapter = popularCitiesAdapter
                    } else {
                        listView.adapter = allCitiesAdapter
                        allCitiesAdapter.filter.filter(newText)
                    }

                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
        })

        listView.setOnItemClickListener { _, view, position, _ ->
            val text = listView.getItemAtPosition(position).toString()
            getWeather(text)
            alertBuilder.dismiss()
        }

        alertBuilder.show()
    }

    private fun readJson(): List<City> {
        val json: String = this.resources.openRawResource(R.raw.currentcities).bufferedReader().use { it.readText() }
        val cities = NonStrictJsonSerializer.serializer.parse(ListSerializer(City.serializer()), json)
        return cities
    }
}
