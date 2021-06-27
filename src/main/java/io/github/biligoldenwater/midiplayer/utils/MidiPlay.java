package io.github.biligoldenwater.midiplayer.utils;

import io.github.biligoldenwater.midiplayer.MidiPlayer;
import org.bukkit.scheduler.BukkitRunnable;

import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;
import java.io.File;
import java.util.Arrays;
import java.util.List;

public class MidiPlay extends BukkitRunnable {
    private final File midiFile;
    private MidiData midiData;

    private long tickLength;
    private long tick = 0;

    private double microsecondPerTick = 1000.0;
    private double microsecondPerOnce = 1000.0;

    private boolean running = false;

    private int ticksInOnce = 1;

    public MidiPlay(File midiFile) {
        this.midiFile = midiFile;
    }

    @Override
    public void run() {
        running = true;
        try {
            midiData = new MidiData(midiFile);
            if (midiData.getFileType() == 2) {
                MidiPlayer.getInstance().getLogger().warning("Unsupported MIDI file type; Name:" + midiFile.getName());
                return;
            }
            tickLength = midiData.getTickLength();

            while (running) {
                for (int i = 0; i < ticksInOnce; i++) {
                    tick();
                }
                Thread.sleep((long) (microsecondPerOnce / 1000), (int) (microsecondPerOnce % 1000));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void tick() {
        if (midiData == null) {
            this.stop();
            return;
        } else if (tick >= tickLength) {
            this.stop();
            return;
        }

        List<TrackData> tracks = midiData.getTracks();
        for (int i = 0; i < tracks.size(); i++) {
            TrackData track = tracks.get(i);

            List<MidiMessage> messages = track.getMessages(tick);
            if (messages == null) continue;
            for (MidiMessage message : messages) {
                if (message instanceof ShortMessage) {
                    ShortMessage shortMessage = (ShortMessage) message;
                } else if (message instanceof MetaMessage) {
                    MetaMessage metaMessage = (MetaMessage) message;
                    switch (metaMessage.getType()) {
                        case 0x51: {// 更改速度,微秒 每拍

                        }
                        default: {
                            System.out.printf("@%d Track: %d, Meta message: Type: %s, %s\n", tick, i, Integer.toHexString(metaMessage.getType()), Arrays.toString(metaMessage.getData()));
                        }
                    }
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
