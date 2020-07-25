package io.github.biligoldenwater.midiplayer.listener;

import io.github.biligoldenwater.midiplayer.PlayMidi;
import io.github.biligoldenwater.midiplayer.api.PlayingMidis;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class OnPlayerQuitEvent implements Listener {
    @EventHandler
    public static void onPlayerQuitEvent(PlayerQuitEvent event){
        PlayMidi playMidi = PlayingMidis.playingMidis.get(event.getPlayer().getName());// 获取退出的玩家的播放中midi
        if(playMidi != null){// 如果播放中的midi不为空则停止播放
            playMidi.stopSound();
        }
    }
}
