package com.udacity.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AsteroidDatabaseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroids: DatabaseAsteroid)

    @Query("SELECT * FROM asteroid_data WHERE closeApproachDate >= :today ORDER BY closeApproachDate ASC")
    fun getAsteroidsFromTodayForwards(today: String): LiveData<List<DatabaseAsteroid>>

    @Query("SELECT * FROM asteroid_data ORDER BY closeApproachDate ASC")
    fun getAllAsteroids(): LiveData<List<DatabaseAsteroid>>
}