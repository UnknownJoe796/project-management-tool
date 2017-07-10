package com.ivieleague.kotlin.web

import org.w3c.dom.HTMLElement
import kotlin.browser.document
import kotlin.dom.addClass
import kotlin.dom.hasClass

fun main(vararg arguments: String) {
    println("Hello world!")
    mainPage()
}

fun HTMLElement.removePage() = childSequence().firstOrNull { it is HTMLElement && it.hasClass("page") }?.let { removeChild(it) }

fun mainPage() {
    document.body!!.apply {
        removePage()
        style.margin = "0px"
        div {
            addClass("page")

            heading1 { innerText = "Hello World!" }
            paragraph { innerText = "This is a test of my new layout system." }
            button {
                innerText = "Press Me!"
                onclick = {
                    tableLayoutPage()
                    true
                }
            }
        }
    }
}

fun tableLayoutPage() {
    document.body!!.apply {
        removePage()
        style.margin = "0px"
        div {
            addClass("page")

            table {
                width = "100%"
                cellPadding = "0px"
                cellSpacing = "0px"
                row {
                    cell {
                        colSpan = 2
                        bgColor = "#b5dcb3"
                        heading1 { innerText = "This is Web Page Main title" }
                    }
                }
                row {
                    vAlign = "top"
                    cell {
                        bgColor = "#aaa"
                        menu {
                            heading5 { innerText = "Main Menu" }
                            br {}
                            anchor {
                                innerText = "Return to first"
                                onclick = {
                                    mainPage()
                                    true
                                }
                            }
                            br {}
                            anchor {
                                innerText = "Return other link"
                                onclick = {
                                    mainPage()
                                    true
                                }
                            }
                        }
                    }
                    cell {
                        main {
                            heading2 { innerText = "Main Content" }
                            paragraph { innerText = "This is some valuable information.  Don't skip it!" }
                        }
                    }
                }
                row {
                    cell {
                        colSpan = 2
                        bgColor = "#b5dcb3"
                        heading5 { innerText = "This is Web Page Main footer" }
                    }
                }
            }
        }
    }
}