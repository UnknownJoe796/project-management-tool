import com.ivieleague.kotlin.server.*

/**
 * Created by josep on 6/8/2017.
 */
object MuhSchema : Schema {

    object Note : TableImpl("note", "A note") {
        val title = Scalar.Standard(
                name = "title",
                description = "The title of the note.",
                type = ScalarType.ShortString,
                default = { "<no name>" }
        ).register()
        val content = Scalar.Standard(
                name = "content",
                description = "The content of the note.",
                type = ScalarType.ShortString,
                default = { "<no name>" }
        ).register()
        val related = Link.Standard(
                name = "related",
                description = "A related note.",
                table = Note
        ).register()
    }

    override val tables = mapOf(Note.tableName to Note)
    override val enums: Map<String, ServerEnum> = mapOf()
}