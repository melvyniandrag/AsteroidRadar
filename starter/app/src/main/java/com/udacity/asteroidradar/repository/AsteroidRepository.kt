package com.udacity.asteroidradar.repository

import android.media.Image
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.APPLICATION_TAG
import com.udacity.asteroidradar.api.*
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.domain.ImageOfTheDay
import com.udacity.asteroidradar.domain.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class NasaRepository(private val database: AsteroidDatabase) {
    val asteroids : LiveData<List<Asteroid>> = Transformations.map( database.asteroidDatabaseDao.getAsteroidsFromTodayForwards( getNextSevenDaysFormattedDates()[0] ) ){
        it.asDomainModel()
    }

    private val _imageOfTheDay = MutableLiveData(ImageOfTheDay("","","","","","","",""))

    val imageOfTheDay : LiveData<ImageOfTheDay>
        get() = _imageOfTheDay
    /**
     * Refresh the asteroids stored in the offline cache.
     *
     * This function uses the IO dispatcher to ensure the database insert database operation
     * happens on the IO dispatcher. By switching to the IO dispatcher using `withContext` this
     * function is now safe to call from any thread including the Main thread.
     *
     * To actually load the asteroids for use, observe [asteroids]
     */
    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            try {
                val nextSevenDays = getNextSevenDaysFormattedDates()
                val today = nextSevenDays[0]
                val seventhDay = nextSevenDays[6]
                val response = NasaAPI.nasa.getAsteroids(today, seventhDay, API_KEY)
                Log.i(APPLICATION_TAG, response)
                val parsed = parseAsteroidsJsonResult(JSONObject(response))
                database.asteroidDatabaseDao.insertAll(*parsed.asDatabaseModel())
            }
            catch(e: java.lang.Exception){
                Log.e(APPLICATION_TAG, "Failed to get asteroid data from REST API. Are you connected to the internet?")
            }
        }
    }

    /**
     * Refresh the image of the day.
     * Why is this here in the repository?
     * 1. to be able to trigger a refresh
     * 2. To have a tidy place to hold the try/catch so it doesnt mess up the ViewModel code.
     */
    suspend fun refreshImageOfTheDay(){
        try {
            _imageOfTheDay.value = ImageAPI.imageGetter.getImageOfTheDay(API_KEY)
        }
        catch(e: Exception){
            Log.e(APPLICATION_TAG, "unable to get image of the day")
        }
    }


}