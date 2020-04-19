package com.example.weather

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.serialization.builtins.ListSerializer
import java.util.concurrent.Executors


@Database(entities = [City::class], version = 1)
abstract class CityDatabase: RoomDatabase() {

    abstract fun cityDao(): CityDao



    companion object {
        @Volatile
        private var INSTANCE: CityDatabase? = null

        fun getDatabase(context: Context): CityDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CityDatabase::class.java,
                    "word_database")
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            val json: String = context.resources.openRawResource(R.raw.currentcities).bufferedReader().use { it.readText() }
                            val cities = NonStrictJsonSerializer.serializer.parse(ListSerializer(City.serializer()), json)

                            Executors.newSingleThreadExecutor().execute {
                                INSTANCE?.cityDao()?.insertAllCities(cities)
                            }
                        }
                    })

                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}