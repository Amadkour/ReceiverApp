package com.example.receiver.dataAccessLayer

import androidx.room.*

@Entity(tableName = "posts")
data class Post(
    @PrimaryKey(autoGenerate = true)
    val id: Int?=null,
    var title: String,
    var body: String,
    var userId: Int,
    )

//---------(DAO-> Data Access Object)------------//

@Dao
interface UserDao {
    @Query("SELECT * FROM posts")
    fun getAll(): List<Post>

    @Query("SELECT * FROM posts WHERE userId IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<Post>

    @Query(
        "SELECT * FROM posts WHERE title LIKE :title AND " +
                "body LIKE :body LIMIT 1"
    )
    fun findByName(title: String, body: String): Post

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll( users: Post):Long

    @Delete
    fun delete(user: Post)
}

/**
-------------------------Database-----------------------
 */

@Database(entities = [Post::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}