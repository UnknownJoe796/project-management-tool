import com.ivieleague.kotlin.server.ServerType
import com.ivieleague.kotlin.server.Table

/**
 * Schema for managing projects.
 * Created by josep on 6/8/2017.
 */
object ProjectManagementSchema {
    object Person : Table("Person", "An employee of the company.") {
        val name = property(
                name = "name",
                type = ServerType.TString,
                default = ""
        )
        val type = property(
                name = "type",
                type = ServerType.TString,
                default = ""
        )
    }

    val tables = listOf(Person)
}