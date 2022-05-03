package io.github.biligoldenwater.midiplayer.utils.particlegui.component

import io.github.biligoldenwater.midiplayer.utils.particlegui.PixelColor
import io.github.biligoldenwater.midiplayer.utils.particlegui.interfaces.Component
import org.bukkit.Color

open class Panel(x: Int, y: Int, width: Int, height: Int, drawBorder: Boolean) : Component(x, y, width, height, false) {
    // final option
    val isDrawBorder: Boolean

    // final data
    val children: MutableList<Component>
    val childrenSize: Int
        get() = children.size

    // option
    var borderColor: PixelColor

    init {
        if (drawBorder) {
            clientWidth = Math.max(width - 2, 0)
            clientHeight = Math.max(height - 2, 0)
        }
        isDrawBorder = drawBorder
        children = ArrayList()
        borderColor = PixelColor(Color.BLACK)
        render()
    }

    override fun render() {
        val width = width
        val children: List<Component> = ArrayList(
            children
        )
        pixels = arrayOfNulls(width * height)

        //region draw children
        for (child in children) {
            if (!child.needRender()) continue
            child.render()
        }
        for (child in children) {
            val childX = child.x + if (isDrawBorder) 1 else 0
            val childY = child.y + if (isDrawBorder) 1 else 0
            for (y in 0 until child.height) {
                for (x in 0 until child.width) {
                    val posX = childX + x
                    val posY = childY + y

                    // 当超出窗口边界时
                    if (posX < (if (isDrawBorder) 1 else 0) || posY < (if (isDrawBorder) 1 else 0)) continue
                    if (posX >= this.width - (if (isDrawBorder) 1 else 0) ||
                        posY >= height - (if (isDrawBorder) 1 else 0)
                    ) continue
                    val pixelColor = child.getPixel(x, y)
                    if (pixelColor == null || pixelColor.alpha == 0.0) continue  // 组件像素颜色为空或完全透明时
                    var originalPixelColor = getPixel(posX, posY)
                    if (originalPixelColor == null) { // 如果窗口当前像素为空
                        originalPixelColor = pixelColor
                    } else {
                        originalPixelColor.overlap(pixelColor)
                    }
                    pixels[posX + posY * width] = originalPixelColor
                }
            }
        }
        //endregion

        //region draw border
        if (isDrawBorder) {
            for (y in 0 until height) {
                pixels[y * width] = borderColor
                pixels[this.width - 1 + y * width] = borderColor
            }
            for (x in 0 until this.width) {
                pixels[x] = borderColor
                pixels[x + (height - 1) * width] = borderColor
            }
        }
        //endregion
        changeApplied()
    }

    override fun needRender(): Boolean {
        for (child in children) {
            if (child.needRender()) return true
        }
        return super.needRender()
    }

    fun addChild(child: Component) {
        onChange()
        child.parent = this
        children.add(child)
    }

    fun removeChild(child: Component) {
        child.parent = null
        children.remove(child)
    }


    override fun setSize(width: Int, height: Int) {
        super.setSize(width, height)
        if (isDrawBorder) {
            clientWidth = Math.max(width - 2, 0)
            clientHeight = Math.max(height - 2, 0)
        }
    }
}