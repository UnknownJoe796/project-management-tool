/**
 * A Nestful API
 * Created by josep on 5/31/2017.
 */

import com.auth0.jwt.algorithms.Algorithm
import com.ivieleague.kotlin.server.TokenInformation
import com.ivieleague.kotlin.server.access.IdField
import com.ivieleague.kotlin.server.rpc.GetMethodsRPCMethod
import com.ivieleague.kotlin.server.rpc.GetTypesRPCMethod
import com.ivieleague.kotlin.server.rpc.RPCMethod
import com.ivieleague.kotlin.server.rpc.rpc
import com.ivieleague.kotlin.server.type.SInt
import com.ivieleague.kotlin.server.type.SimpleTypedObject
import jetbrains.exodus.entitystore.PersistentEntityStores
import org.jetbrains.ktor.application.ApplicationCall
import org.jetbrains.ktor.application.install
import org.jetbrains.ktor.features.Compression
import org.jetbrains.ktor.host.embeddedServer
import org.jetbrains.ktor.logging.CallLogging
import org.jetbrains.ktor.netty.Netty
import org.jetbrains.ktor.request.header
import org.jetbrains.ktor.routing.route
import org.jetbrains.ktor.routing.routing
import kotlin.collections.set

fun main(vararg strings: String) {

    val tokenInformation = TokenInformation(
            "ivieleague.com",
            1000L * 60 * 60 * 24 * 365,
            Algorithm.HMAC512("This is a bunch of test chaos! f j8a9w4hroaskj df89wp alksjd c8i9b0xoiwj4r")
    )

    val xodusEntityStore = PersistentEntityStores.newInstance("~/xodus_test")


    val authGetter = { call: ApplicationCall ->
        val token = call.request.header("Authorization")
        if (token == null)
            null
        else {
            val id = tokenInformation.getUserId(token)
            SimpleTypedObject(User).apply {
                this[IdField] = id
            }
        }
    }

    val methods = HashMap<String, RPCMethod>()

    methods["test"] = object : RPCMethod {
        override val description: String = "A test function.  Will return the value given plus 2."
        override val arguments: List<RPCMethod.Argument> = listOf(RPCMethod.Argument("value", "The value to manipulate", SInt))
        override val returns: RPCMethod.Returns = RPCMethod.Returns("The input value plus 2", SInt)
        override val potentialExceptions: Map<Int, RPCMethod.PotentialException<*>> = mapOf()

        override fun invoke(user: SimpleTypedObject?, arguments: Map<String, Any?>): Any? = (arguments["value"] as Int) + 2
    }
    methods["getMethods"] = GetMethodsRPCMethod(methods)
    methods["getTypes"] = GetTypesRPCMethod(methods)

    embeddedServer(Netty, 8080) {
        install(CallLogging)
        install(Compression)
        routing {
            route("rpc") {
                rpc(methods, authGetter)
            }
        }
        Unit
    }.start(wait = true)
}