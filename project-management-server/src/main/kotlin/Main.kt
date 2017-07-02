/**
 * Created by josep on 5/31/2017.
 */

import com.auth0.jwt.algorithms.Algorithm
import com.ivieleague.kotlin.server.*
import com.ivieleague.kotlin.server.auth.TokenInformation
import com.ivieleague.kotlin.server.auth.UserTableAccess
import com.ivieleague.kotlin.server.xodus.XodusAccess
import jetbrains.exodus.entitystore.PersistentEntityStores
import org.jetbrains.ktor.application.ApplicationCall
import org.jetbrains.ktor.application.install
import org.jetbrains.ktor.application.receive
import org.jetbrains.ktor.features.Compression
import org.jetbrains.ktor.host.embeddedServer
import org.jetbrains.ktor.http.ContentType
import org.jetbrains.ktor.logging.CallLogging
import org.jetbrains.ktor.netty.Netty
import org.jetbrains.ktor.request.header
import org.jetbrains.ktor.response.respondText
import org.jetbrains.ktor.routing.*

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
                it.respondText("Hello, world!", ContentType.Text.Html)
            }
            route("rest") {

                route("user") {
                    restNest(SecurityTableAccess(userTableAccess), authGetter)
                    login(userTableAccess)
                }

                route("note") {
                    restNest(SecurityTableAccess(xodus[Note]), authGetter)
                }
            }
        }
        Unit
    }.start(wait = true)
}

private fun Route.login(userTableAccess: UserTableAccess) {
    post("/login") {
        exceptionWrap {
            val requestString = it.request.receive<String>()
            val (email, password) = try {
                val request = json.readValue(requestString, Map::class.java) as Map<String, Any?>
                (request["email"] as String) to (request["password"] as String)
            } catch(e: Exception) {
                throw exceptionBadRequest(e.message)
            }
            val result = userTableAccess.login(User.email, email, password)
            val stringResult = result?.let { json.writeValueAsString(JSON.serializeInstance(it)) }
            it.respondText(stringResult ?: "{}", ContentType.Application.Json)
        }
    }
}