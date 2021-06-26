package io.github.biligoldenwater.midiplayer;

import org.bukkit.plugin.java.JavaPlugin;

public final class MidiPlayer extends JavaPlugin {

    private static MidiPlayer instance;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("Enabled.");//输出已启用消息到日志
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Disabled.");//输出已禁用消息到日志
    }

    public static MidiPlayer getInstance() {
        return instance;
    }
}
