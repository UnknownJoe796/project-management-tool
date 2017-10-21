/**
 * A Nestful API
 * Created by josep on 5/31/2017.
 */

import com.auth0.jwt.algorithms.Algorithm
import com.ivieleague.kotlin.server.TokenInformation
import jetbrains.exodus.entitystore.PersistentEntityStores
import org.jetbrains.ktor.application.ApplicationCall
import org.jetbrains.ktor.application.install
import org.jetbrains.ktor.content.files
import org.jetbrains.ktor.content.static
import org.jetbrains.ktor.features.Compression
import org.jetbrains.ktor.host.embeddedServer
import org.jetbrains.ktor.logging.CallLogging
import org.jetbrains.ktor.netty.Netty
import org.jetbrains.ktor.request.header
import org.jetbrains.ktor.routing.get
import org.jetbrains.ktor.routing.route
import org.jetbrains.ktor.routing.routing

fun main(vararg strings: String) {

    val tokenInformation = TokenInformation(
            "ivieleague.com",
            1000L * 60 * 60 * 24 * 365,
            Algorithm.HMAC512("This is a bunch of test chaos! f j8a9w4hroaskj df89wp alksjd c8i9b0xoiwj4r")
    )

    val xodusEntityStore = PersistentEntityStores.newInstance("~/xodus_test")

    val schema = Schema()
    val userAccessDirect = User.xodus(xodusEntityStore).user(tokenInformation)
    val userAccess = userAccessDirect.security().register(schema)
    val noteAccess = Note.xodus(xodusEntityStore).security().register(schema)

    val authGetter = { call: ApplicationCall ->
        val token = call.request.header("Authorization")
        if (token == null)
            null
        else
            tokenInformation.getUser(userAccess, schema, token, userAccess.table.defaultRead())
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
                        schema = schema,
                        userGetter = authGetter
                )
                route("user/login") {
                    restLogin(schema, userAccessDirect, User.email)
                }
            }
            static("static") {
                files("static")
            }
        }
        Unit
    }.start(wait = true)
}