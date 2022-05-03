package io.github.biligoldenwater.midiplayer.utils.particlegui.component

import io.github.biligoldenwater.midiplayer.utils.particlegui.PixelColor
import io.github.biligoldenwater.midiplayer.utils.particlegui.interfaces.Component
import org.bukkit.Color

class ProgressBar constructor(x: Int, y: Int, width: Int, height: Int, isHorizontal: Boolean = true) :
    Component(x, y, width, height, false) {
    private var isFitParent = false
    var isHorizontal: Boolean = true
        set(value) {
            field = value
            onChange()
        }
    var percent = 0.0
        set(value) {
            field = value.coerceAtMost(100.0)
            onChange()
        }
    var color: PixelColor = PixelColor()
        set(value) {
            field = value
            onChange()
        }

    init {
        color = PixelColor(Color.BLUE)
        this.isHorizontal = isHorizontal
        render()
    }

    override fun render() {
        if (isFitParent && parent != null) {
            parent?.let {
                setSize(it.clientWidth, it.clientHeight)
            }
        } else {
            pixels = arrayOfNulls(width * height)
        }
        if (!isHorizontal) {
            var i = 0
            while (i < pixels.size * percent * 0.01) {
                pixels[i] = color
                i++
            }
        } else {
            var pixelNum: Long = 1
            val width = width
            val height = height
            for (x in 0 until width) {
                if (pixelNum > pixels.size * percent * 0.01) break
                for (y in height - 1 downTo 0) {
                    if (pixelNum > pixels.size * percent * 0.01) break
                    pixels[x + y * width] = color
                    pixelNum++
                }
            }
        }
    }
}