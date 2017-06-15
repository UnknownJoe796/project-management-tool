/**
 * Created by josep on 5/31/2017.
 */

import com.ivieleague.kotlin.server.Input
import com.ivieleague.kotlin.server.MemoryDAO
import com.ivieleague.kotlin.server.restPlus
import org.jetbrains.ktor.host.embeddedServer
import org.jetbrains.ktor.http.ContentType
import org.jetbrains.ktor.netty.Netty
import org.jetbrains.ktor.response.respondText
import org.jetbrains.ktor.routing.get
import org.jetbrains.ktor.routing.route
import org.jetbrains.ktor.routing.routing

data class LoginInfo(var email: String = "", var password: String = "")

fun main(vararg strings: String) {

//    Database.connect("127.0.0.1:5432", "org.postgresql.Driver", user = "postgres", password = "4E&619>r0Kxl")
    val memoryDao = MemoryDAO()

    memoryDao.update(MuhSchema.Note, Input(
            id = null,
            scalars = mapOf(MuhSchema.Note.title to "Example", MuhSchema.Note.content to "Some example content"),
            links = mapOf(MuhSchema.Note.related to Input(
                    id = null,
                    scalars = mapOf(MuhSchema.Note.title to "Related", MuhSchema.Note.content to "Don't mind me.")
            ))
    ))

    embeddedServer(Netty, 8080) {
        routing {
            get("/") {
                it.respondText("Hello, world!", ContentType.Text.Html)
            }
            route("rest") {
                restPlus(memoryDao, MuhSchema)
            }
        }
        Unit
    }.start(wait = true)
}