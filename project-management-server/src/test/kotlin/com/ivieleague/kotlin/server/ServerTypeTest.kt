package com.ivieleague.kotlin.server

import org.junit.Test


class ServerTypeTest {

    @Test fun testRaw() {
        val task = Task.Instance(RawPDF)
        task.name = "Test Task"
        task.description = "This is a test task for kotlin-server"
        val subtask = Task.Instance(RawPDF)
        subtask.name = "Test Subtask"
        subtask.description = "Gotta do this first"
        task.subtasks = listOf(SimpleLink(subtask))
    }

    @Test fun testXodus() {
        val task = Task.Instance(RawPDF)
        task.name = "Test Task"
        task.description = "This is a test task for kotlin-server"
        val subtask = Task.Instance(RawPDF)
        subtask.name = "Test Subtask"
        subtask.description = "Gotta do this first"
        task.subtasks = listOf(SimpleLink(subtask))
    }
}