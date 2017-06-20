/**
 * Created by josep on 5/31/2017.
 */

import com.ivieleague.kotlin.server.SecurityTableAccess
import com.ivieleague.kotlin.server.core.Instance
import com.ivieleague.kotlin.server.restPlus
import com.ivieleague.kotlin.server.xodus.XodusAccess
import jetbrains.exodus.entitystore.PersistentEntityStores
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
    val xodusEntityStore = PersistentEntityStores.newInstance("C:\\XodusTest\\")
    val xodus = XodusAccess(xodusEntityStore)

    embeddedServer(Netty, 8080) {
        routing {
            get("/") {
                it.respondText("Hello, world!", ContentType.Text.Html)
            }
            route("rest") {
                route("note") {
                    restPlus(SecurityTableAccess(xodus[Note]), {
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