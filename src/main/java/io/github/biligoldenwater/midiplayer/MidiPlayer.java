package io.github.biligoldenwater.midiplayer;

import io.github.biligoldenwater.midiplayer.utils.MidiPlay;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class MidiPlayer extends JavaPlugin {
    private static MidiPlayer instance;

    private boolean debug = false;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("Enabled.");//输出已启用消息到日志
    }

    public static void main(String[] args) {
//        File file = new File("D:\\Music\\Midis\\U.N.オーエンは彼女なのか？.mid");
//        File file = new File("D:\\Music\\Midis\\When Christmas comes to town.mid");
//        File file = new File("D:\\Music\\Midis\\千本樱.mid");
        File file = new File("D:\\Music\\Midis\\HedwigsTheme.mid");
        new MidiPlay(file).run();
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

    public static MidiPlayer getInstance() {
        return instance;
    }
}
