package io.github.biligoldenwater.midiplayer

import io.github.biligoldenwater.midiplayer.listener.OnAsyncPlayerChatEvent
import io.github.biligoldenwater.midiplayer.utils.MidiPlay
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

lateinit var instance: MidiPlayer

class MidiPlayer : JavaPlugin() {
    val playList: MutableMap<String, MidiPlay> = mutableMapOf()
    var isDebug = true
    override fun onLoad() {
        instance = this
    }

    override fun onEnable() {
        // Plugin startup logic
        Bukkit.getPluginManager().registerEvents(OnAsyncPlayerChatEvent(), this)
        logger.info("Enabled.") //输出已启用消息到日志
    }

    override fun onDisable() {
        // Plugin shutdown logic
        playList.forEach { (key: String?, value: MidiPlay) -> value.stop() }
        logger.info("Disabled.") //输出已禁用消息到日志
    }
}