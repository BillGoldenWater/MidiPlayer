package io.github.biligoldenwater.midiplayer.utils;

import org.bukkit.scheduler.BukkitRunnable;

import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;
import java.io.File;
import java.util.Arrays;
import java.util.List;

public class MidiPlay extends BukkitRunnable {
    private boolean running = false;
    private long tick = 0;
    private MidiData midiData;

    @Override
    public void run() {
        running = true;
//        File file = new File("D:\\Music\\Midis\\U.N.オーエンは彼女なのか？.mid");
        File file = new File("D:\\Music\\Midis\\When Christmas comes to town.mid");
//        File file = new File("D:\\Music\\Midis\\千本樱.mid");

        try {
            midiData = new MidiData(file);

            while (running){

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void tick() {
        if (midiData == null) {
            this.stop();
            return;
        }

        List<MidiData.TrackData> tracks = midiData.getTracks();
        for (int i = 0; i < tracks.size(); i++) {
            MidiData.TrackData track = tracks.get(i);

            List<MidiMessage> messages = track.getMessages(tick);
            if (messages == null) continue;
            for (MidiMessage message : messages) {
                if (message instanceof ShortMessage) {
                    ShortMessage shortMessage = (ShortMessage) message;
                } else if (message instanceof MetaMessage) {
                    MetaMessage metaMessage = (MetaMessage) message;
                    System.out.printf("Track: %d, Meta message: Type: %s, %s\n", i, Integer.toHexString(metaMessage.getType()), Arrays.toString(metaMessage.getData()));
                } else if (message instanceof SysexMessage) {
                    SysexMessage sysexMessage = (SysexMessage) message;
                } else {
                    System.out.println("Other Message.");
                }
            }
        }

        this.tick++;
    }

    public void stop() {
        running = false;
    }
}
