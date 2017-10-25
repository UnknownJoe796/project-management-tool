import com.ivieleague.kotlin.server.access.IdField
import com.ivieleague.kotlin.server.type.SClass
import com.ivieleague.kotlin.server.type.SList
import com.ivieleague.kotlin.server.type.SString

object Note : SClass {

    override val name: String = "note"
    override val description: String = "A note of some kind"

    val title = SClass.Field(
            key = "title",
            description = "The title of the note.",
            type = SString
    )

    val content = SClass.Field(
            key = "content",
            description = "The content of the note.",
            type = SString
    )

    val related = SClass.Field(
            key = "related",
            description = "A related note.",
            type = Note
    )

    override val fields = listOf(IdField, title, content, related).associate { it.key to it }
}

object User : SClass {
    override val name: String = "User"
    override val description: String = "A user of the system."

    val firstName = SClass.Field(
            key = "firstName",
            description = "The user's first name.",
            type = SString
    )

    val lastName = SClass.Field(
            key = "lastName",
            description = "The user's last name.",
            type = SString
    )

    val email = SClass.Field(
            key = "email",
            description = "The user's email.",
            type = SString
    )

    override val fields = listOf(IdField, firstName, lastName, email).associate { it.key to it }
}

object Organization : SClass {

    override val name: String = "organization"
    override val description: String = "An organization that employees can belong to."

    val organizationName = SClass.Field(
            key = "name",
            description = "The name of the organization",
            type = SString
    )

    val members = SClass.Field(
            key = "members",
            description = "The members of this organization",
            type = SList[User]
    )

    val admins = SClass.Field(
            key = "admins",
            description = "The admins of this organization",
            type = SList[User]
    )

    override val fields = listOf(IdField, organizationName, members, admins).associate { it.key to it }
}

//object Task : TableImpl("task", "A task, representing anything as large as a project and a small subtask.") {
//
//    //Security rules: You must belong to the organization to view it.
//    //user in it.organization.members
//    init {
//        readPermission = { user ->
//            user?.id?.let {
//                Condition.MultilinkContains(path = listOf(organization), multilink = Organization.members, id = it)
//            } ?: Condition.Never
//        }
//        writeAfterPermission = { user ->
//            user?.id?.let {
//                Condition.MultilinkContains(path = listOf(organization), multilink = Organization.members, id = it)
//            } ?: Condition.Never
//        }
//        writeBeforePermission = { user ->
//            user?.id?.let {
//                Condition.MultilinkContains(path = listOf(organization), multilink = Organization.members, id = it)
//            } ?: Condition.Never
//        }
//    }
//
//    val organization = Link(
//            key = "organization",
//            description = "The organization a task belongs to",
//            table = Organization
//    ).register()
//
//    val name = Primitive(
//            key = "name",
//            description = "The name of the task",
//            type = PrimitiveType.ShortString
//    ).register()
//
//    val description = Primitive(
//            key = "description",
//            description = "The description of the task",
//            type = PrimitiveType.ShortString
//    ).register()
//
//    val subtasks = Multilink(
//            key = "subtasks",
//            description = "The subtasks of this task",
//            table = Task
//    ).register()
//
//    val dependencies = Multilink(
//            key = "dependencies",
//            description = "The dependencies of this task",
//            table = Task
//    ).register()
//
//    val estimatedTime = Primitive(
//            key = "estimated_time",
//            description = "The amount of time this task is estimated to take, in hours",
//            type = PrimitiveType.Double
//    ).register()
//
//    val assignee = Link(
//            key = "assignee",
//            description = "The person currently assigned to this task",
//            table = User
//    ).register()
//
//    val times = Multilink(
//            key = "times",
//            description = "The times of this task",
//            table = Time
//    ).register()
//
//    val comments = Multilink(
//            key = "comments",
//            description = "The comments of this task",
//            table = Comment
//    ).register()
//
//}
//
//object Comment : TableImpl("comment", "A comment on a task") {
//    val author = Link(
//            key = "author",
//            description = "The person who wrote this comment",
//            table = User
//    ).register()
//
//    val posted = Primitive(
//            key = "posted",
//            description = "The time this comment was posted",
//            type = PrimitiveType.Date
//    ).register()
//
//    val content = Primitive(
//            key = "content",
//            description = "The content of the comment",
//            type = PrimitiveType.LongString
//    ).register()
//}
//
//object Time : TableImpl("time", "A time segment, representing time an employee spent on a task.") {
//
//    val person = Link(
//            key = "person",
//            description = "The person who did the work",
//            table = User
//    ).register()
//
//    // /!\ Not normalized
//    val task = Link(
//            key = "task",
//            description = "The task being worked on",
//            table = Task
//    ).register()
//
//    val time = Primitive(
//            key = "time",
//            description = "Time taken total, measured in hours",
//            type = PrimitiveType.Long
//    ).register()
//
//    val startTime = Primitive(
//            key = "start_time",
//            description = "Start time",
//            type = PrimitiveType.Date
//    ).register()
//
//    val endTime = Primitive(
//            key = "end_time",
//            description = "End time",
//            type = PrimitiveType.Date
//    ).register()
//
//}