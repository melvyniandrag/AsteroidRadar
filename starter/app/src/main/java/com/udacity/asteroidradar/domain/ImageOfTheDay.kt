package com.udacity.asteroidradar.domain

import com.squareup.moshi.Json

data class ImageOfTheDay(
    val copyright: String,

    val date: String,

    val explanation: String,

    val hdurl: String,

    @Json(name="media_type")
    val mediaType: String,

    @Json(name="service_version")
    val serviceVersion: String,

    val title: String,

    val url: String
)
