package es.upm.etsisi.mad.helloworld.persistence

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query

@Entity
data class Mission(
    @PrimaryKey(autoGenerate = true) val id: UInt,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val userAssigned: UInt
)

@Dao
interface MissionDao {
    @Query("SELECT * FROM mission")
    fun getAll(): List<Mission>

    @Insert
    fun add(vararg missions: Mission)

    @Delete
    fun delete(vararg missions: Mission)
}