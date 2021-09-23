package com.example.middleman

import com.example.emitter.accessLayer.model.User
import com.example.receiver.MainActivity
import com.example.receiver.dataAccessLayer.Post
import com.example.receiver.dataAccessLayer.UserDao
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.OutputStream
import java.net.ServerSocket
import java.net.Socket
import java.nio.charset.Charset
import java.util.*
import kotlin.concurrent.thread

class myServer {
    companion object{
        var server: ServerSocket? = null
    }
    fun run() {
        if (server == null)
            server = ServerSocket(9999)
        println("Server is running on port ${server!!.localPort}")

        while (true) {
            val client = server!!.accept()
            println("Client connected: ${client.inetAddress.hostAddress}")
            // Run client in it's own thread.
            Thread {
                ClientHandler(client).run()

            }.run()
        }

    }
}

class ClientHandler(client: Socket) {
    private val client: Socket = client
    private val reader: Scanner = Scanner(client.getInputStream())
    private val writer: OutputStream = client.getOutputStream()
    private var running: Boolean = false

    fun run() {
        println("running.....")
        running = true
        // Welcome message


        while (running) {
            try {
                val text = reader.nextLine()
                if (text == "EXIT") {
                    shutdown()
                    continue
                } else {
                    val user: User = Gson().fromJson(text, User::class.java)
                    println(user.email)
                    var id = MainActivity.userDao?.insertAll(
                        Post(
                            id = user.id, title = user.name,
                            body = user.address.street, userId = user.id
                        )
                    )
                    write("id is: $id")
                    write("OK")
                    client.close();
                }

//                this.shutdown()

            } catch (ex: Exception) {
                print(ex)
//                shutdown()
            } finally {

            }

        }
    }

    private fun write(message: String) {
        writer.write((message + '\n').toByteArray(Charset.defaultCharset()))
    }

    private fun shutdown() {
        running = false
        client.close()
        println("${client.inetAddress.hostAddress} closed the connection")
    }

}