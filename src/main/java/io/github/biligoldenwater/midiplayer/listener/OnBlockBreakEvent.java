package io.github.biligoldenwater.midiplayer.listener;

import io.github.biligoldenwater.midiplayer.MidiPlayer;
import io.github.biligoldenwater.midiplayer.utils.particlegui.ParticleGui;
import io.github.biligoldenwater.midiplayer.utils.particlegui.PixelColor;
import io.github.biligoldenwater.midiplayer.utils.particlegui.component.Window;
import org.bukkit.Color;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class OnBlockBreakEvent implements Listener {
    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event) {
//        String playerName = event.getPlayer().getName();
//        Map<String, MidiPlay> playList = MidiPlayer.getInstance().getPlayList();
//
//        MidiPlay playing = playList.get(playerName);
//        if (playing != null) {
//            playing.stop();
//            playList.remove(playerName);
//            return;
//        }
//
////        File midiFile = new File("D:\\Music\\Midis\\U.N.オーエンは彼女なのか？.mid");
////        File midiFile = new File("D:\\Music\\Midis\\When Christmas comes to town.mid");
////        File midiFile = new File("D:\\Music\\Midis\\勾指起誓双轨.mid");
////        File midiFile = new File("D:\\Music\\Midis\\Luv letter -Dj Okawari.mid");
////        File midiFile = new File("D:\\Music\\Midis\\千本樱.mid");
//        File midiFile = new File("D:\\Music\\Midis\\Hedwigs Theme.mid");
////        File midiFile = new File("D:\\Music\\Midis\\Rubia.mid");
////        File midiFile = new File("D:\\Music\\Midis\\D大调卡农.mid");
//
//        PlayNote playNote = new PlayNote(PlayNote.ResourcePack.realPiano, event.getPlayer(), false, 0, true);
//
//        MidiPlay midiPlay = new MidiPlay(midiFile, false, playNote);
//        midiPlay.runTaskAsynchronously(MidiPlayer.getInstance());
//        playList.put(playerName, midiPlay);

        Window window = new Window(384, 240);
        window.setBorderColor(new PixelColor(Color.GRAY));

        new ParticleGui(event.getPlayer(), window)
                .runTaskTimerAsynchronously(MidiPlayer.getInstance(), 0, 0);
    }
}
