package io.github.biligoldenwater.midiplayer.utils.particlegui.component

import io.github.biligoldenwater.midiplayer.utils.particlegui.PixelColor
import io.github.biligoldenwater.midiplayer.utils.particlegui.interfaces.Component
import org.bukkit.Color

open class Block(x: Int, y: Int, width: Int, height: Int) : Component(x, y, width, height) {
    var color: PixelColor = PixelColor(Color.BLACK)

    override fun render() {}
    override fun getPixel(x: Int, y: Int): PixelColor? {
        return color
    }
}