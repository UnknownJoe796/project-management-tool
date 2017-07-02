/**
 * Created by josep on 5/31/2017.
 */

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.ivieleague.kotlin.server.SecurityTableAccess
import com.ivieleague.kotlin.server.auth.makeTokenEndpoint
import com.ivieleague.kotlin.server.restPlus
import com.ivieleague.kotlin.server.xodus.XodusAccess
import jetbrains.exodus.entitystore.PersistentEntityStores
import org.jetbrains.ktor.application.install
import org.jetbrains.ktor.features.Compression
import org.jetbrains.ktor.host.embeddedServer
import org.jetbrains.ktor.http.ContentType
import org.jetbrains.ktor.logging.CallLogging
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

    val authUserTable = AuthUser(Person)
    val algorithm = Algorithm.HMAC512("This is a bunch of test chaos! f j8a9w4hroaskj df89wp alksjd c8i9b0xoiwj4r")
    val authGetter = AuthUser.makeUserGetter<Person>(
            tableAccess = xodus[authUserTable],
            verifier = JWT.require(algorithm)
                    .build()
    )

    embeddedServer(Netty, 8080) {
        install(CallLogging)
        install(Compression)
        routing {
            get("/") {
                it.respondText("Hello, world!", ContentType.Text.Html)
            }
            route("rest") {

                makeTokenEndpoint<Person>("token", xodus[authUserTable], "ivieleague.com", 10000000, algorithm)

                route("person") {
                    restPlus(SecurityTableAccess(xodus[Person]), authGetter)
                }

                route("note") {
                    restPlus(SecurityTableAccess(xodus[Note]), authGetter)
                }
            }
        }
        Unit
    }.start(wait = true)
}