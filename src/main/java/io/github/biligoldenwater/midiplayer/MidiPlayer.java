package io.github.biligoldenwater.midiplayer;

import io.github.biligoldenwater.midiplayer.api.PlayingMidis;
import io.github.biligoldenwater.midiplayer.commands.CommandMidiPlayer;
import io.github.biligoldenwater.midiplayer.commands.TabMidiPlayer;
import org.bukkit.plugin.java.JavaPlugin;

public final class MidiPlayer extends JavaPlugin {

    private static MidiPlayer instance;
    private static String musicsPathName;

    @Override
    public void onEnable() {
        // Plugin startup logic
        // Init plugin
        saveDefaultConfig();
        instance = this;
        musicsPathName = getDataFolder().getPath() + "\\musics";
        CommandMidiPlayer.registerCommandMidiPlayer();
        TabMidiPlayer.registerTabMidiPlayer();
        // End of init

//        getLogger().info(musicsPathName);
//        getLogger().info(String.valueOf(GetMidis.getMidis(musicsPathName)));
//
//        List<File> midis = GetMidis.getMidis(musicsPathName);
//
//        PlayingMidis.playingMidis.put("Golden_Water",new PlayMidi());
//
//        PlayingMidis.playingMidis.get("Golden_Water").initMidi(this,midis.get(0));
//        PlayingMidis.playingMidis.get("Golden_Water").playMidi(getServer().getPlayer("Golden_Water"), false, PlayNote.realPiano);

        getLogger().info("Enabled");//输出已启用消息到日志
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        PlayingMidis.playingMidis.forEach((playerName, playMidi) -> {//遍历所有播放中的midi并停止它们
            getLogger().info(" Force stop midi player for player:"+playerName+".");
            playMidi.stopSound();
        });
        getLogger().info("Disabled");//输出已禁用消息到日志
    }

    public static String getMusicsPathName(){
        return musicsPathName;
    }

    public static MidiPlayer getInstance(){
        return instance;
    }
}
