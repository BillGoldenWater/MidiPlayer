package io.github.biligoldenwater.midiplayer.utils

import io.github.biligoldenwater.midiplayer.instance

object Debug {
    fun print(debugMessage: String) {
        if (instance.isDebug) {
            kotlin.io.print(debugMessage)
        }
    }
}