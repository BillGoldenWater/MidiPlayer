package io.github.biligoldenwater.midiplayer.utils

import org.bukkit.Location
import org.bukkit.entity.Player

class PlayNote {
    private val resourcePack: ResourcePack
    private val broadcast: Boolean
    private val forcePiano: Boolean
    private val range: Int
    private val player: Player?
    private val loc: Location

    constructor(resourcePack: ResourcePack, targetPlayer: Player, broadcast: Boolean, range: Int, forcePiano: Boolean) {
        this.resourcePack = resourcePack
        this.broadcast = broadcast
        this.range = range
        this.forcePiano = forcePiano
        player = targetPlayer
        loc = targetPlayer.location
    }

    constructor(resourcePack: ResourcePack, loc: Location, range: Int, forcePiano: Boolean) {
        this.resourcePack = resourcePack
        broadcast = false
        this.range = range
        this.forcePiano = forcePiano
        player = null
        this.loc = loc
    }

    fun noteOn(instrumentId: Int, note: Int, velocity: Int) {
        @Suppress("NAME_SHADOWING") var instrumentId = instrumentId
        if (forcePiano) instrumentId = 0
        if (player == null || broadcast) { // 如果玩家为null(固定位置) 或 为广播
            val loc: Location = player?.location ?: this.loc
            assert(loc.world != null)
            for (p in loc.world!!.players) { // 获取世界内所有玩家
                if (p.location.distance(loc) <= range) { // 如果在范围内则播放
                    playNote(resourcePack, p, loc, instrumentId, note, velocity.toFloat() / 127)
                }
            }
        } else {
            playNote(resourcePack, player, player.location, instrumentId, note, velocity / 127.0f)
        }
    }

    fun noteOff(instrumentId: Int, note: Int) {
        @Suppress("NAME_SHADOWING") var instrumentId = instrumentId
        if (forcePiano) instrumentId = 0
        if (player == null || broadcast) { // 如果玩家为null(固定位置) 或 为广播
            val loc: Location = player?.location ?: this.loc
            assert(loc.world != null)
            for (p in loc.world!!.players) { // 获取世界内所有玩家
                if (p.location.distance(loc) <= range) { // 如果在范围内则停止
                    stopNote(resourcePack, p, instrumentId, note)
                }
            }
        } else {
            stopNote(resourcePack, player, instrumentId, note)
        }
    }

    enum class ResourcePack(val str: String) {
        Vanilla("Vanilla"), RealPiano("RealPiano"), MSGSPiano("MSGSPiano");
    }
}