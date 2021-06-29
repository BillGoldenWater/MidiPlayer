package io.github.biligoldenwater.midiplayer.listener;

import io.github.biligoldenwater.midiplayer.MidiPlayer;
import io.github.biligoldenwater.midiplayer.utils.MidiPlay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.io.File;
import java.util.Map;

public class OnBlockBreakEvent implements Listener {
    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event) {
        String playerName = event.getPlayer().getName();
        if (playerName.equalsIgnoreCase("Golden_Water")) {
            Map<String, MidiPlay> playList = MidiPlayer.getInstance().getPlayList();

            MidiPlay playing = playList.get(playerName);
            if (playing != null) {
                playing.stop();
            }

//            File midiFile = new File("D:\\Music\\Midis\\U.N.オーエンは彼女なのか？.mid");
//            File midiFile = new File("D:\\Music\\Midis\\When Christmas comes to town.mid");
//            File midiFile = new File("D:\\Music\\Midis\\千本樱.mid");
//            File midiFile = new File("D:\\Music\\Midis\\Hedwigs Theme.mid");
            File midiFile = new File("D:\\Music\\Midis\\Rubia.mid");

            MidiPlay midiPlay = new MidiPlay(midiFile, event.getPlayer(), true, false);
            midiPlay.runTaskAsynchronously(MidiPlayer.getInstance());
            playList.put(playerName, midiPlay);
        }
    }
}
