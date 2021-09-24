package com.example.receiver.dataAccessLayer

import androidx.room.*
import com.example.emitter.accessLayer.model.Geo


//---------(DAO-> Data Access Object)------------//

@Dao
interface UserDao {
    @Query("SELECT * FROM user_table")
    fun getAll(): List<UserTable>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser( users: UserTable):Long


}

/**
-------------------------Database-----------------------
 */

@Database(entities = [UserTable::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}


@Entity(tableName = "user_table")

data class UserTable(
    @PrimaryKey val id:Int,
    val name:String,
    val username:String,
    val email:String,
    @Embedded val address: Address,
    val phone:String,
    @Embedded val company: Company,
)
data class Company(
    @ColumnInfo(name = "company_name") val name:String,
    val catchPhrase:String,
    val bs:String,
)
data class Address(
    val street:String,
    val suite:String,
    val zipcode:String,
    @Embedded val geo: Geo
)



