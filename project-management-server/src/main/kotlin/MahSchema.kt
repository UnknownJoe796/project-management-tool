import com.ivieleague.kotlin.server.ServerType
import com.ivieleague.kotlin.server.Table

/**
 * Created by josep on 6/8/2017.
 */
object MahSchema {
    object Note : Table("Note", "A note") {
        val title = property(
                name = "title",
                description = "The title of the note.",
                type = ServerType.TString,
                default = ""
        )
        val content = property(
                name = "content",
                description = "The content of the note.",
                type = ServerType.TString,
                default = ""
        )
        val related = property(
                name = "related",
                description = "A related note.",
                type = ServerType.TPointer(this),
                default = null
        )
    }

    val tables = listOf(Note)
}