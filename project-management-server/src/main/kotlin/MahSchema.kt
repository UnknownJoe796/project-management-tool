import com.ivieleague.kotlin.server.ScalarType
import com.ivieleague.kotlin.server.Schema
import com.ivieleague.kotlin.server.ServerEnum
import com.ivieleague.kotlin.server.TableImpl

/**
 * Created by josep on 6/8/2017.
 */
object MahSchema : Schema {

    object Note : TableImpl("Note", "A note") {
        val title = property(
                name = "title",
                description = "The title of the note.",
                type = ScalarType.TString,
                default = ""
        )
        val content = property(
                name = "content",
                description = "The content of the note.",
                type = ScalarType.TString,
                default = ""
        )
        val related = property(
                name = "related",
                description = "A related note.",
                type = ScalarType.TPointer(this),
                default = null
        )
    }

    override val tables = mapOf(Note.tableName to Note)
    override val enums: Map<String, ServerEnum> = mapOf()
}