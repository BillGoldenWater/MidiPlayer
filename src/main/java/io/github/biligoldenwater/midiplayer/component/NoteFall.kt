package io.github.biligoldenwater.midiplayer.component

import io.github.biligoldenwater.midiplayer.utils.particlegui.PixelColor
import io.github.biligoldenwater.midiplayer.utils.particlegui.component.Block
import io.github.biligoldenwater.midiplayer.utils.particlegui.component.Panel
import io.github.biligoldenwater.midiplayer.utils.particlegui.interfaces.Component
import org.bukkit.Color

class NoteFall(x: Int, y: Int, width: Int, height: Int, drawBorder: Boolean) : Panel(x, y, width, height, drawBorder) {
    fun update() {
        if (childrenSize > 100) {
            val children: MutableList<Component> = children.map { it }.toMutableList()
            children.sortBy {
                if (it is Note) {
                    return@sortBy it.startTs
                }
                0
            }
            for (i in 0 until children.size - 50) {
                removeChild(children[i])
            }
        }
        children.removeIf {
            if (it is Note) {
                val block = it
                val startTs = block.startTs / 25
                val tsNow = System.currentTimeMillis() / 25
                if (!block.isEnded) {
                    block.setSize(1, (tsNow - startTs).toInt().coerceAtLeast(1))
                } else {
                    block.y = (tsNow - startTs).toInt() - block.height
                }
                if (block.y > clientHeight) {
                    return@removeIf true
                }
            }
            false
        }
        onChange()
    }

    fun noteOn(note: Int) {
        addChild(Note(note, System.currentTimeMillis()))
    }

    fun noteOff(note: Int) {
        children.forEach {
            if (it is Note) {
                if (!it.isEnded && it.note == note) {
                    it.isEnded = true
                }
            }
        }
    }

    class Note(val note: Int, val startTs: Long) : Block((note + 1) * 2, 0, 1, 1) {
        var isEnded: Boolean

        init {
            color = PixelColor(Color.LIME)
            isEnded = false
        }

        fun end() {
            isEnded = true
        }
    }
}