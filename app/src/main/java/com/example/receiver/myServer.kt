package com.example.receiver

import android.content.ComponentName
import android.content.Intent
import com.example.emitter.accessLayer.model.Geo
import com.example.emitter.accessLayer.model.User
import com.example.receiver.MainActivity.Companion.context
import com.example.receiver.dataAccessLayer.Address
import com.example.receiver.dataAccessLayer.Company
import com.example.receiver.dataAccessLayer.UserTable
import com.google.gson.Gson
import java.io.OutputStream
import java.net.ServerSocket
import java.net.Socket
import java.nio.charset.Charset
import java.util.*
import kotlin.concurrent.thread

class myServer {
    companion object {
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
        running = true

        while (running) {
            try {
                val text = reader.nextLine()
                if (text == "EXIT") {
                    shutdown()
                    continue
                } else {
                    val user: User = Gson().fromJson(text, User::class.java)
                    try {
                        MainActivity.userDao?.insertUser(
                            UserTable(
                                id = user.id,
                                name = user.name,
                                username = user.username,
                                phone = user.phone,
                                address = Address(
                                    street = user.address.street,
                                    suite = user.address.suite,
                                    zipcode = user.address.zipcode,
                                    geo = Geo(
                                        lat = user.address.geo.lat,
                                        lng = user.address.geo.lng
                                    )
                                ),
                                company = Company(
                                    name = user.company.name,
                                    catchPhrase = user.company.catchPhrase,
                                    bs = user.company.bs
                                ),
                                email = user.email
                            )
                        )!!
                        write("OK")

                    } catch (e: Exception) {
                        println(e.message)
                        write("NOK")
                    } finally {
                        //            ---------------------(pop Emitter app to  front)-----------//
                        thread {
                            val emitterIntent = Intent()
                            popApp(emitterIntent, "emitter")
                        }.run()
                    }
//                    client.close();
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

    fun popApp(intent: Intent, app: String) {
        intent.flags =
            Intent.FLAG_INCLUDE_STOPPED_PACKAGES
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.action = "android.intent.action.VIEW"
        intent.component =
            ComponentName.unflattenFromString("com.example.$app/com.example.$app.MainActivity")
        context?.startActivity(intent)
    }
}