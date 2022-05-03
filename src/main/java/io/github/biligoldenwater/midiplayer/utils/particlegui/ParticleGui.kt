package io.github.biligoldenwater.midiplayer.utils.particlegui

import io.github.biligoldenwater.midiplayer.utils.particlegui.component.Window
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.Particle.DustOptions
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector

class ParticleGui @JvmOverloads constructor(
    private val player: Player,
    var window: Window,
    private val debug: Boolean = false
) : BukkitRunnable() {
    var distance = 10.0
    private var drawCall: Long = 0
    private var lastEnd: Long = 0
    private var tLoc // targetLoc
            : Location? = null
    private var yaw = 0.0
    private var pitch = 0.0
    private var pitchAxis: Vector? = null
    override fun run() {
        var start: Long = 0
        if (debug) {
            player.sendMessage("===========================================")
            start = System.currentTimeMillis()
            player.sendMessage(String.format("delay: %dms", start - lastEnd))
        }
        updateOffsetInfo()
        if (window.needRender()) window.render()
        drawWindow(window)
        if (debug) {
            player.sendMessage(String.format("drawCall: %d", drawCall))
            drawCall = 0
            val end = System.currentTimeMillis()
            player.sendMessage(String.format("cost: %dms", end - start))
            lastEnd = end
        }
    }

    fun drawWindow(window: Window) {
        val width = window.width
        val height = window.height
        val halfWidth = width / 2
        val halfHeight = height / 2
        for (y in 0 until height) {
            for (x in 0 until width) {
                val pixelColor = window.getPixel(x, y)
                if (pixelColor != null) {
                    drawPixel(halfWidth - x, y - halfHeight, pixelColor)
                }
            }
        }
    }

    fun drawPixel(x: Int, y: Int, pixelColor: PixelColor) {
        val pixelLoc = Vector()
        pixelLoc.x = x * 0.04
        pixelLoc.y = y * -1 * 0.04
        pixelLoc.z = distance
        pixelLoc.rotateAroundY(yaw)
        pixelLoc.rotateAroundAxis(pitchAxis!!, pitch)
        val tLoc = tLoc!!.clone().add(pixelLoc)
        val dust = DustOptions(pixelColor.toBukkitColor(), 0.2f)
        tLoc.world?.spawnParticle(Particle.REDSTONE, tLoc.x, tLoc.y, tLoc.z, 2, 0.0, 0.0, 0.0, 0.0, dust)
        if (debug) drawCall++
    }

    fun updateOffsetInfo() {
        if (player.isSneaking) return
        tLoc = player.eyeLocation // targetLoc
        var yaw = tLoc!!.yaw.toDouble()
        if (yaw < 0) yaw += 360
        yaw = Math.toRadians(yaw) * -1
        var pitch = tLoc!!.pitch.toDouble()
        pitch = Math.toRadians(pitch)
        val pitchAxis = Vector()
        pitchAxis.setX(1)
        pitchAxis.rotateAroundY(yaw)
        this.yaw = yaw
        this.pitch = pitch
        this.pitchAxis = pitchAxis
    }
}