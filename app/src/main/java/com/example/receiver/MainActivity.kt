package com.example.receiver

import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.room.Room
import com.example.middleman.myServer
import com.example.receiver.dataAccessLayer.AppDatabase
import com.example.receiver.dataAccessLayer.Post
import com.example.receiver.dataAccessLayer.UserDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.ServerSocket
import java.util.*


class MainActivity : AppCompatActivity() {
    companion object{
        var db: AppDatabase? = null
        var userDao: UserDao? = null
        var users: List<Post> = listOf(Post(0, "ee", "ee", 8))

    }
   var server: myServer? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GlobalScope.launch(Dispatchers.IO){
            db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java, "User_database"
            ).build()
            userDao = db!!.userDao()
            users=userDao!!.getAll()
        }
        setContent{
            LazyColumn(modifier = Modifier.fillMaxSize()){
                itemsIndexed(users){
                        _,item -> Row(horizontalArrangement = Arrangement.SpaceAround,
                    modifier = Modifier.fillMaxWidth()){
                    Row(horizontalArrangement = Arrangement.SpaceAround,
                    modifier = Modifier.fillParentMaxWidth()){
                        Text(item.body)
                        Text(item.title)
                        Text(item.id.toString())

                    }
                }
                }
            }
        }
        GlobalScope.launch(Dispatchers.IO) {
            println("------------")
            if(server==null) {
                server=myServer()
                server!!.run();
            }
        }


    }
}