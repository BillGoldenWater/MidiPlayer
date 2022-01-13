package io.github.biligoldenwater.midiplayer.listener;

import io.github.biligoldenwater.midiplayer.MidiPlayer;
import io.github.biligoldenwater.midiplayer.utils.MidiPlay;
import io.github.biligoldenwater.midiplayer.utils.PlayNote;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.io.File;
import java.util.Map;

public class OnBlockBreakEvent implements Listener {
    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event) {
        String playerName = event.getPlayer().getName();
        Map<String, MidiPlay> playList = MidiPlayer.getInstance().getPlayList();

        MidiPlay playing = playList.get(playerName);
        if (playing != null) {
            playing.stop();
            playList.remove(playerName);
            return;
        }

//        File midiFile = new File("D:\\Music\\Midis\\U.N.オーエンは彼女なのか？.mid");
//        File midiFile = new File("D:\\Music\\Midis\\When Christmas comes to town.mid");
//        File midiFile = new File("D:\\Music\\Midis\\千本樱.mid");
//        File midiFile = new File("D:\\Music\\Midis\\Hedwigs Theme.mid");
//        File midiFile = new File("D:\\Music\\Midis\\Rubia.mid");
        File midiFile = new File("D:\\Music\\Midis\\D大调卡农.mid");

        MidiPlay midiPlay = new MidiPlay(midiFile, false, PlayNote.ResourcePack.realPiano, event.getBlock().getLocation(), 50);
        midiPlay.runTaskAsynchronously(MidiPlayer.getInstance());
        playList.put(playerName, midiPlay);
    }
}
