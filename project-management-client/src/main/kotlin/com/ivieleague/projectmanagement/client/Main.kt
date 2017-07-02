package com.ivieleague.projectmanagement.client

import com.ivieleague.kotlin.javafx._StackPane
import com.ivieleague.kotlin.javafx.button
import javafx.application.Application
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.stage.Stage


class MainApplication() : Application() {
    override fun start(primaryStage: Stage) {
        primaryStage.apply {
            title = "HAI"
            scene = Scene(_StackPane().apply {
                button {
                    text = "Test Button"
                    setOnAction {
                        text = "Success!"
                    }
                    prefWidth = 200.0
                }.lparams {
                    alignment = Pos.TOP_CENTER
                    margin = Insets(12.0)
                }
            }, 200.0, 200.0)
            show()
        }
    }
}

fun main(vararg args: String) {
    Application.launch(MainApplication::class.java)
}