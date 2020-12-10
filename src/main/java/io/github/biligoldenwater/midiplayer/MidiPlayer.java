package io.github.biligoldenwater.midiplayer;

import io.github.biligoldenwater.midiplayer.api.PlayingMidis;
import io.github.biligoldenwater.midiplayer.api.PlayingNoteParticles;
import io.github.biligoldenwater.midiplayer.command.CommandMidiPlayer;
import io.github.biligoldenwater.midiplayer.command.TabMidiPlayer;
import io.github.biligoldenwater.midiplayer.listener.OnPlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class MidiPlayer extends JavaPlugin {

    private static MidiPlayer instance;
    private static File musicsPath;

    @Override
    public void onEnable() {
        // Plugin startup logic
        // Init plugin
        saveDefaultConfig(); // 创建默认配置文件
        instance = this;
        initMusicsPath();
        getServer().getPluginManager().registerEvents(new OnPlayerQuitEvent(),this);
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
        PlayingNoteParticles.playingNoteParticles.forEach(noteParticle -> { // 遍历所有播放中的音符粒子效果并停止它们
            if (noteParticle == null)return;
            if (noteParticle.isRunning()){
                noteParticle.stop();
            }
        });

        PlayingMidis.playingMidis.forEach((playerName, playMidi) -> {//遍历所有播放中的midi并停止它们
            getLogger().info(" Force stop midi player for player:"+playerName+".");
            playMidi.stopSound();
        });

        getLogger().info("Disabled");//输出已禁用消息到日志
    }

    private void initMusicsPath() {
        File[] filesInDataFolder = getDataFolder().listFiles(); // 获取数据文件夹内所有目录和文件
        if(filesInDataFolder != null){ // 如果数据文件夹不为空
            boolean isMusicsPathExists = false;

            for (File file : filesInDataFolder){
                if (file.getName().equalsIgnoreCase("musics") && file.isDirectory()){ // 如果名字为music并且为一个目录则
                    musicsPath = file;
                    isMusicsPathExists = true;
                    break;

                } else if (file.getName().equalsIgnoreCase("musics") && !file.isDirectory()){ // 如果名字为music并且不为一个目录则
                    if (file.delete()){ // 删除该文件
                        if(file.mkdir()){ // 创建目录
                            musicsPath = file;
                            isMusicsPathExists = true;
                            break;

                        } else {
                            getLogger().warning("Fail to create musics folder."); // 创建失败

                        }

                    } else {
                        getLogger().warning("Fail to delete a same name file for musics folder."); // 删除失败

                    }
                }
            }
            if (!isMusicsPathExists){
                musicsPath = new File(getDataFolder().getPath() + "/musics");
                if(!musicsPath.mkdirs()){
                    getLogger().warning("Fail to create musics folder.");
                    musicsPath = null;
                }
            }
        }
    }

    public static File getMusicsPath(){
        return musicsPath;
    }

    public static MidiPlayer getInstance(){
        return instance;
    }
}
