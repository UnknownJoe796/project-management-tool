/**
 * Created by josep on 5/31/2017.
 */

import com.ivieleague.kotlin.server.MemoryDatabaseAccess
import com.ivieleague.kotlin.server.SecurityTableAccess
import com.ivieleague.kotlin.server.core.Instance
import com.ivieleague.kotlin.server.core.Write
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

    MemoryDatabaseAccess[Note].update(null, Write(
            id = null,
            scalars = mapOf(Note.title to "Example", Note.content to "Some example content"),
            links = mapOf(Note.related to Write(
                    id = null,
                    scalars = mapOf(Note.title to "Related", Note.content to "Don't mind me.")
            ))
    ))

    embeddedServer(Netty, 8080) {
        routing {
            get("/") {
                it.respondText("Hello, world!", ContentType.Text.Html)
            }
            route("rest") {
                route("note") {
                    restPlus(SecurityTableAccess(MemoryDatabaseAccess[Note]), {
                        if (it.request.headers["Authorization"] != null)
                            Instance("283912", mapOf(), mapOf(), mapOf())
                        else null
                    })
                }
            }
        }
        Unit
    }.start(wait = true)
}