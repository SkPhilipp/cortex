package com.hileco.cortex.processing.processes

abstract class BaseProcess : Runnable {
    private val thread: Thread = Thread {
        executeIndefinitely()
    }

    fun startThread() {
        if (!thread.isAlive) {
            Runtime.getRuntime().addShutdownHook(Thread(this::stopThread));
            thread.start();
        }
    }

    private fun stopThread() {
        thread.stop()
    }

    private fun executeIndefinitely() {
        while (true) {
            run()
            Thread.sleep(500)
        }
    }
}
