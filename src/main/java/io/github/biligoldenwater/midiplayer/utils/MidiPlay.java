package io.github.biligoldenwater.midiplayer.utils;

import io.github.biligoldenwater.midiplayer.MidiPlayer;
import org.bukkit.craftbukkit.libs.org.apache.commons.codec.binary.Hex;
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
                long timeStart = System.currentTimeMillis();
                for (int i = 0; i < ticksInOnce; i++) {
                    tick();
                }
                long timeEnd = System.currentTimeMillis();
                long timeCost = timeEnd - timeStart;

                long millis = (long) (microsecondPerOnce / 1000);
                int nanos = (int) (microsecondPerOnce % 1000);

                long finalMillisDelay = millis - timeCost;
                if (finalMillisDelay < 0) finalMillisDelay = 0;

                Thread.sleep(finalMillisDelay, nanos);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void tick() throws Exception {
        if (midiData == null || tick >= tickLength) { // 如果数据为空 或 已经播放完毕
            this.stop();
            return;
        }

        List<TrackData> tracks = midiData.getTracks(); // 获取所有轨道
        for (int i = 0, len = tracks.size(); i < len; i++) {
            TrackData track = tracks.get(i); // 获取当前轨道

            List<MidiMessage> messages = track.getMessages(tick); // 获取指定 tick 的 MidiMessage 列表
            if (messages == null) continue; // 如果无消息
            for (MidiMessage message : messages) {
                System.out.printf("@%d Track: %d, ", tick, i);

                if (message instanceof ShortMessage) {
                    ShortMessage shortMessage = (ShortMessage) message;

                } else if (message instanceof MetaMessage) {
                    MetaMessage metaMessage = (MetaMessage) message;
                    System.out.printf("Meta message: Type: %s, ", Integer.toHexString(metaMessage.getType()));

                    switch (metaMessage.getType()) {
                        case 0x51: {// 更改速度,微秒 每拍
                            String dataHex = Hex.encodeHexString(metaMessage.getData());
                            changeSpeed(Integer.parseInt(dataHex, 16));
                            System.out.print("Change speed to:" + dataHex);
                            break;
                        }
                        default: {
                            System.out.printf("Data: %s; RawMessage:%s", Arrays.toString(metaMessage.getData()), Arrays.toString(metaMessage.getMessage()));
                        }
                    }
                } else if (message instanceof SysexMessage) {
                    SysexMessage sysexMessage = (SysexMessage) message;

                } else {
                    System.out.println("Other Message.");
                }

                System.out.println();
            }
        }

        this.tick++;
    }

    public void calcTicksInOnce() {
        int min = 1000;
        double percent = microsecondPerTick / min;
        int times = (int) Math.ceil(1 / percent);
        if (times < 1) times = 1;
        ticksInOnce = times;
    }

    public void changeSpeed(int microsecondPerBeat) throws MidiData.UnsupportedMidiFileType {
        microsecondPerTick = midiData.getMicrosecondPerTick(microsecondPerBeat);
        calcTicksInOnce();
        microsecondPerOnce = microsecondPerTick * ticksInOnce;
    }

    public void stop() {
        running = false;
    }
}
