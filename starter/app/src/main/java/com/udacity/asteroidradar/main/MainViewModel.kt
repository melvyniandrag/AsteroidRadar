package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.repository.NasaRepository
import kotlinx.coroutines.launch

class MainViewModel(
    application: Application) : AndroidViewModel(application) {

    private val database = AsteroidDatabase.getInstance(application)
    private val nasaRepository = NasaRepository(database)

    val asteroids = nasaRepository.asteroids
    val imageOfTheDay = nasaRepository.imageOfTheDay


    private val _clickedAsteroid = MutableLiveData<Asteroid>()

    val clickedAsteroid : LiveData<Asteroid>
        get() = _clickedAsteroid

    init{
        viewModelScope.launch {
            nasaRepository.refreshImageOfTheDay()
            nasaRepository.refreshAsteroids()
        }
    }

    fun onAsteroidClicked(asteroid: Asteroid){
        // default asteroid value has id -1
        _clickedAsteroid.value = asteroid
    }

    fun doneNavigating(){
        _clickedAsteroid.value = Asteroid(-1, "", "", 0.0, 0.0, 0.0, 0.0, false)
    }

}