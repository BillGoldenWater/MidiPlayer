package io.github.biligoldenwater.midiplayer.utils.particlegui

import org.bukkit.Color

class PixelColor {
    var red = 0.0
    var green = 0.0
    var blue = 0.0
    var alpha = 0.0

    constructor() {
        red = 0.0
        green = 0.0
        blue = 0.0
        alpha = 1.0
    }

    @JvmOverloads
    constructor(red: Double, green: Double, blue: Double, alpha: Double = 1.0) {
        this.red = red
        this.green = green
        this.blue = blue
        this.alpha = alpha
    }

    constructor(color: Color) : this(color.red / 255.0, color.green / 255.0, color.blue / 255.0) {}

    fun overlap(pixelColor: PixelColor): PixelColor {
        if (pixelColor.alpha == 1.0) {
            red = pixelColor.red
            green = pixelColor.green
            blue = pixelColor.blue
            return this
        }
        red = red * (1 - pixelColor.alpha) + pixelColor.red * pixelColor.alpha
        green = green * (1 - pixelColor.alpha) + pixelColor.green * pixelColor.alpha
        blue = blue * (1 - pixelColor.alpha) + pixelColor.blue * pixelColor.alpha
        return this
    }

    fun equals(pixelColor: PixelColor): Boolean {
        return red == pixelColor.red && green == pixelColor.green && blue == pixelColor.blue && alpha == pixelColor.alpha
    }

    fun toBukkitColor(): Color {
        return Color.fromRGB((red * 255).toInt(), (green * 255).toInt(), (blue * 255).toInt())
    }
}