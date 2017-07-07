/**
 * Created by josep on 5/31/2017.
 */

import com.auth0.jwt.algorithms.Algorithm
import com.ivieleague.kotlin.server.SecurityTableAccess
import com.ivieleague.kotlin.server.auth.TokenInformation
import com.ivieleague.kotlin.server.auth.UserTableAccess
import com.ivieleague.kotlin.server.respondJson
import com.ivieleague.kotlin.server.restNest
import com.ivieleague.kotlin.server.xodus.XodusAccess
import jetbrains.exodus.entitystore.PersistentEntityStores
import org.jetbrains.ktor.application.ApplicationCall
import org.jetbrains.ktor.application.install
import org.jetbrains.ktor.features.Compression
import org.jetbrains.ktor.host.embeddedServer
import org.jetbrains.ktor.logging.CallLogging
import org.jetbrains.ktor.netty.Netty
import org.jetbrains.ktor.request.header
import org.jetbrains.ktor.routing.get
import org.jetbrains.ktor.routing.route
import org.jetbrains.ktor.routing.routing

data class LoginInfo(var email: String = "", var password: String = "")

fun main(vararg strings: String) {

    val tokenInformation = TokenInformation(
            "ivieleague.com",
            1000L * 60 * 60 * 24 * 365,
            Algorithm.HMAC512("This is a bunch of test chaos! f j8a9w4hroaskj df89wp alksjd c8i9b0xoiwj4r")
    )

//    Database.connect("127.0.0.1:5432", "org.postgresql.Driver", user = "postgres", password = "4E&619>r0Kxl")
    val xodusEntityStore = PersistentEntityStores.newInstance("C:\\XodusTest\\")
    val xodus = XodusAccess(xodusEntityStore)

    val userTableAccess = UserTableAccess(xodus[User], tokenInformation)
    val authGetter = { call: ApplicationCall ->
        val token = call.request.header("Authorization")
        if (token == null)
            null
        else
            tokenInformation.getUser(userTableAccess, token, userTableAccess.table.defaultRead())
    }

    embeddedServer(Netty, 8080) {
        install(CallLogging)
        install(Compression)
        routing {
            get("/") {
                it.respondJson("Hello, world!")
            }
            route("rest") {
                restNest(
                        tableAccesses = listOf(
                                SecurityTableAccess(userTableAccess),
                                SecurityTableAccess(xodus[Note])
                        ),
                        userGetter = authGetter
                )
            }
        }
        Unit
    }.start(wait = true)
}