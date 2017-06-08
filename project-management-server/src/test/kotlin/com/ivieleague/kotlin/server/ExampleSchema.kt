package com.ivieleague.kotlin.server

private object Permission {
    val any: AccessRules = listOf(listOf())
}

object Task : ServerType.TObject<Task.Instance>("Task") {
    val name = property("name", "Name of the task", TString, "", Permission.any, Permission.any)
    val description = property("description", "A description of the task", TString, "", Permission.any, Permission.any)
    val subtasks = property("subtasks", "The subtasks required to complete this task", TList(TPointer(Task)), listOf(), Permission.any, Permission.any)

    override fun instance(factory: PropertyDelegateFactory): Instance = Instance(factory)
    class Instance(factory: PropertyDelegateFactory) : TObject.InstanceImpl(id, factory) {
        var name by property(Task.name, "")
        var description by property(Task.description, "")
        var subtasks by property(Task.subtasks, listOf())
    }
}