package com.example.weather

import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration


object NonStrictJsonSerializer {
    @OptIn(UnstableDefault::class)
    val serializer = Json(
        JsonConfiguration(
            isLenient = true,
            ignoreUnknownKeys = true,
            serializeSpecialFloatingPointValues = true,
            useArrayPolymorphism = true
        )
    )
}