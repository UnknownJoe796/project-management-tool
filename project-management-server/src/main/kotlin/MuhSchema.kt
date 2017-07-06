import com.ivieleague.kotlin.server.auth.AbstractUserTable
import com.ivieleague.kotlin.server.model.*


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

    init {
        this.writeBeforePermission = { user -> if (user != null) Condition.Always else Condition.Never }
        this.writeAfterPermission = { user -> if (user != null) Condition.Always else Condition.Never }
    }
}

object User : AbstractUserTable("user", "An employee") {
    init {
        writeBeforePermission = { user -> user?.id?.let { Condition.IdEquals(equals = it) } ?: Condition.Never }
    }

    val firstName = Scalar(
            key = "first_name",
            description = "The first name",
            type = ScalarType.ShortString
    ).register()

    val lastName = Scalar(
            key = "last_name",
            description = "The last name",
            type = ScalarType.ShortString
    ).register()

    val email = Scalar(
            key = "email",
            description = "The email",
            type = ScalarType.ShortString
    ).register()
}

object Organization : AbstractUserTable("organization", "An organization that employees can belong to.") {

    init {
        //user must be an admin
        writeBeforePermission = { user -> user?.id?.let { Condition.MultilinkContains(multilink = admins, id = it) } ?: Condition.Never }
        writeAfterPermission = { user -> user?.id?.let { Condition.MultilinkContains(multilink = admins, id = it) } ?: Condition.Never }

        readPermission = { user ->
            user?.id?.let {
                Condition.MultilinkContains(multilink = Organization.members, id = it)
            } ?: Condition.Never
        }
    }

    val name = Scalar(
            key = "name",
            description = "The name of the organization",
            type = ScalarType.ShortString
    ).register()

    val members = Multilink(
            key = "members",
            description = "The members of this organization",
            table = User
    ).register()

    val admins = Multilink(
            key = "admins",
            description = "The admins of this organization",
            table = User
    ).register()
}

object Task : TableImpl("task", "A task, representing anything as large as a project and a small subtask.") {

    //Security rules: You must belong to the organization to view it.
    //user in it.organization.members
    init {
        readPermission = { user ->
            user?.id?.let {
                Condition.MultilinkContains(path = listOf(organization), multilink = Organization.members, id = it)
            } ?: Condition.Never
        }
        writeAfterPermission = { user ->
            user?.id?.let {
                Condition.MultilinkContains(path = listOf(organization), multilink = Organization.members, id = it)
            } ?: Condition.Never
        }
        writeBeforePermission = { user ->
            user?.id?.let {
                Condition.MultilinkContains(path = listOf(organization), multilink = Organization.members, id = it)
            } ?: Condition.Never
        }
    }

    val organization = Link(
            key = "organization",
            description = "The organization a task belongs to",
            table = Organization
    ).register()

    val name = Scalar(
            key = "name",
            description = "The name of the task",
            type = ScalarType.ShortString
    ).register()

    val description = Scalar(
            key = "description",
            description = "The description of the task",
            type = ScalarType.ShortString
    ).register()

    val subtasks = Multilink(
            key = "subtasks",
            description = "The subtasks of this task",
            table = Task
    ).register()

    val dependencies = Multilink(
            key = "dependencies",
            description = "The dependencies of this task",
            table = Task
    ).register()

    val estimatedTime = Scalar(
            key = "estimated_time",
            description = "The amount of time this task is estimated to take, in hours",
            type = ScalarType.Double
    ).register()

    val assignee = Link(
            key = "assignee",
            description = "The person currently assigned to this task",
            table = User
    ).register()

    val times = Multilink(
            key = "times",
            description = "The times of this task",
            table = Time
    ).register()

    val comments = Multilink(
            key = "comments",
            description = "The comments of this task",
            table = Comment
    ).register()

}

object Comment : TableImpl("comment", "A comment on a task") {
    val author = Link(
            key = "author",
            description = "The person who wrote this comment",
            table = User
    ).register()

    val posted = Scalar(
            key = "posted",
            description = "The time this comment was posted",
            type = ScalarType.Date
    ).register()

    val content = Scalar(
            key = "content",
            description = "The content of the comment",
            type = ScalarType.LongString
    ).register()
}

object Time : TableImpl("time", "A time segment, representing time an employee spent on a task.") {

    val person = Link(
            key = "person",
            description = "The person who did the work",
            table = User
    ).register()

    // /!\ Not normalized
    val task = Link(
            key = "task",
            description = "The task being worked on",
            table = Task
    ).register()

    val time = Scalar(
            key = "time",
            description = "Time taken total, measured in hours",
            type = ScalarType.Long
    ).register()

    val startTime = Scalar(
            key = "start_time",
            description = "Start time",
            type = ScalarType.Date
    ).register()

    val endTime = Scalar(
            key = "end_time",
            description = "End time",
            type = ScalarType.Date
    ).register()

}