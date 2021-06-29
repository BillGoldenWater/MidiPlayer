package io.github.biligoldenwater.midiplayer;

import io.github.biligoldenwater.midiplayer.listener.OnBlockBreakEvent;
import io.github.biligoldenwater.midiplayer.utils.MidiPlay;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public final class MidiPlayer extends JavaPlugin {
    private static MidiPlayer instance;
    private final Map<String, MidiPlay> playList = new HashMap<>();

    private boolean debug = false;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic

        Bukkit.getPluginManager().registerEvents(new OnBlockBreakEvent(), this);

        getLogger().info("Enabled.");//输出已启用消息到日志
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Disabled.");//输出已禁用消息到日志
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public Map<String, MidiPlay> getPlayList() {
        return playList;
    }

    public static MidiPlayer getInstance() {
        return instance;
    }
}
