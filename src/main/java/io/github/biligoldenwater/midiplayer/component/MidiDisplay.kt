package io.github.biligoldenwater.midiplayer.component

import io.github.biligoldenwater.midiplayer.utils.particlegui.PixelColor
import io.github.biligoldenwater.midiplayer.utils.particlegui.component.Block
import io.github.biligoldenwater.midiplayer.utils.particlegui.component.ProgressBar
import io.github.biligoldenwater.midiplayer.utils.particlegui.component.Window
import org.bukkit.Color

class MidiDisplay(private val tickLength: Long) {
    val window: Window
    private val progressBar: ProgressBar
    private val noteFall: NoteFall

    init {
        var width = 2 * 128 + 7
        var height = 100 + 9
        window = Window(width, height)
        window.borderColor = PixelColor(Color.GRAY)
        width = window.clientWidth
        height = window.clientHeight
        progressBar = ProgressBar(1, 1, width - 2, 2)
        window.addChild(progressBar)
        val block1 = Block(0, 4, width, 1)
        block1.color = PixelColor(Color.GRAY)
        window.addChild(block1)
        noteFall = NoteFall(1, 6, width - 2, height - 7, true)
        noteFall.borderColor = PixelColor(Color.GRAY)
        window.addChild(noteFall)
    }

    fun setTick(tick: Long) {
        progressBar.percent = (1f * tick / tickLength * 100).toDouble()
        noteFall.update()
    }

    fun noteOn(note: Int) {
        noteFall.noteOn(note)
    }

    fun noteOff(note: Int) {
        noteFall.noteOff(note)
    }
}