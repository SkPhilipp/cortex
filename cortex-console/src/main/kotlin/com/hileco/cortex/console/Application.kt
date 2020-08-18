package com.hileco.cortex.console

fun main() {
    val console = Console.build()
    var redraw = true
    while (true) {
        if (redraw) {
            console.refresh()
        }
        Thread.sleep(10)
        redraw = console.checkInput()
    }
}
