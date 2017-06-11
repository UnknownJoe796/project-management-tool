/**
 * Created by josep on 5/31/2017.
 */

import com.github.salomonbrys.kotson.jsonObject
import com.lightningkite.kotlin.networking.MyGson
import com.lightningkite.kotlin.networking.gsonToString
import com.lightningkite.kotlin.networking.toJsonArray
import graphql.GraphQL
import org.jetbrains.ktor.application.receive
import org.jetbrains.ktor.host.embeddedServer
import org.jetbrains.ktor.http.ContentType
import org.jetbrains.ktor.netty.Netty
import org.jetbrains.ktor.response.respondText
import org.jetbrains.ktor.routing.get
import org.jetbrains.ktor.routing.post
import org.jetbrains.ktor.routing.routing

data class LoginInfo(var email: String = "", var password: String = "")

fun main(vararg strings: String) {

//    Database.connect("127.0.0.1:5432", "org.postgresql.Driver", user = "postgres", password = "4E&619>r0Kxl")
    val memoryDao = SimpleMemoryDao()
    val graphql = GraphQL.newGraphQL(MahSchema.graphQL(memoryDao)).build()

    memoryDao.set(MahSchema.Note, null, mapOf(MahSchema.Note.title to "Example", MahSchema.Note.content to "Some example content"), listOf())

    embeddedServer(Netty, 8080) {
        routing {
            get("/") {
                it.respondText("Hello, world!", ContentType.Text.Html)
            }
            post("/graphql") {
                val stringData = it.request.receive<String>()
                val data = MyGson.json.parse(stringData).asJsonObject
                val query = data["query"].asString
                try {
                    val result = graphql.execute(query)
                    if (result.errors.size > 0) {
                        it.respondText(jsonObject(
                                "errors" to result.errors.map { it.message }.toJsonArray()
                        ).toString(), ContentType.Application.Json)
                    } else {
                        it.respondText(result?.getData<Map<String, Any>>()?.gsonToString() ?: "{}", ContentType.Application.Json)
                    }
                } catch(e: Exception) {
                    e.printStackTrace()
                    it.respondText("Failed", ContentType.Text.Html)
                }
            }
        }
        Unit
    }.start(wait = true)
}