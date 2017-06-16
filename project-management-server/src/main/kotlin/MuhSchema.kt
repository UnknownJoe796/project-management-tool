import com.ivieleague.kotlin.server.core.Link
import com.ivieleague.kotlin.server.core.Scalar
import com.ivieleague.kotlin.server.core.ScalarType
import com.ivieleague.kotlin.server.core.TableImpl


object Note : TableImpl("note", "A note of some kind") {

    val title = Scalar(
            key = "title",
            description = "The title of the note.",
            type = ScalarType.ShortString
    ).register()

    val content = Scalar(
            key = "content",
            description = "The content of the note.",
            type = ScalarType.ShortString
    ).register()

    val related = Link(
            key = "related",
            description = "A related note.",
            table = Note
    ).register()
}