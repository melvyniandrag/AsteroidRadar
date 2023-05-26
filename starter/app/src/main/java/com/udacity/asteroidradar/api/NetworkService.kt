package com.udacity.asteroidradar.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.Constants.BASE_URL
import com.udacity.asteroidradar.domain.ImageOfTheDay
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

const val API_KEY="you key here"

/****************************************************************************
 * Asteroid API Section
 ****************************************************************************/

/**
 // Cannot do it this way with the callback. See comment in AsteroidRepository.kt

interface NasaAPIService {
    @GET("neo/rest/v1/feed")
    fun getAsteroids(@Query("start_date") start_date: String, @Query("end_date") end_date: String, @Query("api_key") api_key: String) : Call<String>
}
*/
interface NasaAPIService {
    @GET("neo/rest/v1/feed")
    suspend fun getAsteroids(
        @Query("start_date") start_date: String,
        @Query("end_date") end_date: String,
        @Query("api_key") api_key: String
    ): String
}

object NasaAPI {
    // Configure retrofit to parse JSON and use coroutines
    private val retrofitAsteroid = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    val nasa = retrofitAsteroid.create(NasaAPIService::class.java)
}

/****************************************************************************
 * Image API Section
 ****************************************************************************/


interface ImageAPIService{
    @GET("planetary/apod")
    suspend fun getImageOfTheDay(@Query("api_key") api_key: String) : ImageOfTheDay
}

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

object ImageAPI{
    private val retrofitImageOfDay = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(BASE_URL)
        .build()

    val imageGetter = retrofitImageOfDay.create(ImageAPIService::class.java)
}