package io.github.biligoldenwater.midiplayer.utils.particlegui.interfaces

import io.github.biligoldenwater.midiplayer.utils.particlegui.PixelColor

abstract class Component constructor(
    var x: Int,
    var y: Int,
    var width: Int,
    var height: Int,
    renderImmediately: Boolean = true
) {
    var parent: Component? = null
    private var changed = false
    var clientWidth: Int
        protected set
    var clientHeight: Int
        protected set
    protected var pixels: Array<PixelColor?>

    init {
        clientWidth = width
        clientHeight = height
        pixels = arrayOfNulls(width * height)
        if (renderImmediately) render()
    }

    abstract fun render()
    open fun needRender(): Boolean {
        return changed
    }

    fun onChange() {
        changed = true
    }

    fun changeApplied() {
        changed = false
    }

    fun setPixel(x: Int, y: Int, pixelColor: PixelColor?) {
        pixels[x + y * width] = pixelColor
    }

    open fun getPixel(x: Int, y: Int): PixelColor? {
        return pixels[x + y * width]
    }

    open fun setSize(width: Int, height: Int) {
        onChange()
        this.width = width
        this.height = height
        clientWidth = width
        clientHeight = height
        pixels = arrayOfNulls(width * height)
    }
}