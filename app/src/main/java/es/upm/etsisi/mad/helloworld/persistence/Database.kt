package es.upm.etsisi.mad.helloworld.persistence

import androidx.room.Database

@Database(entities = [User::class, Mission::class], version = 1)
abstract class  AppDatabase : androidx.room.RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun missionDao(): MissionDao
}