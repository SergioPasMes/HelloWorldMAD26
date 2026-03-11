package es.upm.etsisi.mad.helloworld.persistence

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import es.upm.etsisi.mad.helloworld.ProfilePicture

@Entity
data class User(
    @PrimaryKey(autoGenerate = true) val id: UInt,
    val alias: String,
    val age: UInt,
    val profilePicture: String
)

data class UserWithMissions(
    @Embedded val user: User,
    @Relation(
        parentColumn = "id",
        entityColumn = "userAssigned"
    )
    val missions: List<Mission>
)

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getAll(): List<User>

    @Insert
    fun add(vararg users: User)

    @Delete
    fun delete(vararg users: User)

    @Transaction
    @Query("SELECT * FROM user")
    fun getAllWithMissions(): List<UserWithMissions>
}
