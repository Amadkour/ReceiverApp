package com.example.receiver

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.room.Room
import com.example.receiver.dataAccessLayer.AppDatabase
import com.example.receiver.dataAccessLayer.UserDao
import com.example.receiver.dataAccessLayer.UserTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    companion object {
        var db: AppDatabase? = null
        var userDao: UserDao? = null
        var context: Context? = null
    }

    var server: myServer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context=applicationContext

        GlobalScope.launch(Dispatchers.IO) {
            db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java, "UserDB"
            ).fallbackToDestructiveMigration()
                .build()
            userDao = db!!.userDao()
        }
        setContent {
            var users by remember {
                mutableStateOf(listOf<UserTable>())
            }
            Box(Modifier.fillMaxSize()) {

                Box(
                    Modifier.align(Alignment.TopCenter).fillMaxWidth()
                ) {

                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                            .padding(5.dp)
                    ) {
                        itemsIndexed(users) { _, item ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 1.dp)
                                    .clip(RoundedCornerShape(20))
                                    .background(Color.Magenta.copy(alpha = 0.4f))
                                    .padding(10.dp)
                            ) {
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    TextDesign(
                                        modifier = Modifier.weight(1F),
                                        false,
                                        item.name
                                    )
                                    TextDesign(
                                        modifier = Modifier.weight(1F),
                                        false,
                                        item.username,
                                    )
                                }
                                TextDesign(modifier = Modifier, true, item.email)
                            }
                        }
                    }
                }
                Box(
                    Modifier.align(Alignment.BottomCenter).fillMaxWidth()
                ) {
                    Button(modifier = Modifier.fillMaxWidth(), onClick = {
                        GlobalScope.launch(Dispatchers.IO) {
                            users = userDao!!.getAll()
                        }

                    }) {
                        Text("Show all Received Data")
                    }
                }
            }
        }
        GlobalScope.launch(Dispatchers.IO) {
            if (server == null) {
                server = myServer()
                server!!.run();
            }
        }


    }

    @Composable
    fun TextDesign(modifier: Modifier, isEmail: Boolean, text: String) = Text(
        text = text,
        style = TextStyle(
            color = if (isEmail) Color.Blue else Color.Black,
            fontSize = if (isEmail) 10.sp else 15.sp
        ),
        modifier = modifier
    )
}