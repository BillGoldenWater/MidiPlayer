package io.github.biligoldenwater.midiplayer.utils

import java.nio.ByteBuffer

fun ByteBuffer.toHexString(): String {
    this.position(0)
    val result = StringBuilder()
    while (this.hasRemaining()) {
        val v = this.get().toUByte()
        result.append(v.toString(16).padStart(2,'0'))
    }
    return result.toString()
}