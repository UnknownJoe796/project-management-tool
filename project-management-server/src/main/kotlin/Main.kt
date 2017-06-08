/**
 * Created by josep on 5/31/2017.
 */

import org.jetbrains.ktor.application.receive
import org.jetbrains.ktor.host.embeddedServer
import org.jetbrains.ktor.http.ContentType
import org.jetbrains.ktor.netty.Netty
import org.jetbrains.ktor.response.respondText
import org.jetbrains.ktor.routing.get
import org.jetbrains.ktor.routing.routing
import java.io.InputStream

data class LoginInfo(var email: String = "", var password: String = "")

fun main(vararg strings: String) {

//    Database.connect("127.0.0.1:5432", "org.postgresql.Driver", user = "postgres", password = "4E&619>r0Kxl")

    embeddedServer(Netty, 8080) {
        routing {
            get("/") {
                it.respondText("Hello, world!", ContentType.Text.Html)
            }
            get("/") {
                it.request.receive<InputStream>()
                it.respondText("Hello, world!", ContentType.Text.Html)
            }
        }
    }.start(wait = true)
}