package io.github.biligoldenwater.midiplayer.utils;

import io.github.biligoldenwater.midiplayer.MidiPlayer;
import io.github.biligoldenwater.midiplayer.data.MidiData;
import io.github.biligoldenwater.midiplayer.data.TrackData;
import org.apache.commons.codec.binary.Hex;
import org.bukkit.scheduler.BukkitRunnable;

import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;
import java.io.File;
import java.util.Arrays;
import java.util.List;

public class MidiPlay extends BukkitRunnable {
    private final PlayNote playNote;
    private final File midiFile;
    private final int minimumDelayInMicrosecond = 10 * 1000; // 50 ms
    private final boolean useStop;

    private MidiData midiData;

    private long tickLength;
    private long tick = 0;

    private double microsecondPerTick = 1000.0;
    private double microsecondPerOnce = minimumDelayInMicrosecond;

    private boolean running = false;

    private int ticksInOnce = 1;

    public MidiPlay(File midiFile, boolean useStop, PlayNote playNote) {
        this.playNote = playNote;
        this.midiFile = midiFile;
        this.useStop = useStop;
    }

    @SuppressWarnings("BusyWait")
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
                Thread.sleep(Math.max(finalMillisDelay, 0), nanos);
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

        calcTicksInOnce();

        List<TrackData> tracks = midiData.getTracks(); // 获取所有轨道
        for (int i = 0, len = tracks.size(); i < len; i++) {
            TrackData track = tracks.get(i); // 获取当前轨道

            List<MidiMessage> messages = track.getMessages(tick); // 获取指定 tick 的 MidiMessage 列表
            if (messages == null) continue; // 如果无消息

            for (MidiMessage message : messages) {
                Debug.print(String.format("@%d Track: %d, ", tick, i));

                if (message instanceof ShortMessage) {
                    ShortMessage shortMessage = (ShortMessage) message;
                    Debug.print(String.format("Short message: Command: %s, ", Integer.toHexString(shortMessage.getCommand())));
                    switch (shortMessage.getCommand()) {
                        case ShortMessage.NOTE_ON: {
                            int channelId = shortMessage.getChannel();
                            int instrumentId = track.getChannelInstrument(channelId);
                            int note = shortMessage.getData1();
                            int velocity = shortMessage.getData2();

                            playNote.noteOn(instrumentId, note, velocity);
                            Debug.print(String.format("Channel: %d, Instrument: %d, NoteOn: %d, Velocity: %d",
                                    channelId,
                                    instrumentId,
                                    note,
                                    velocity));
                            break;
                        }
                        case ShortMessage.NOTE_OFF: {
                            int channelId = shortMessage.getChannel();
                            int instrumentId = track.getChannelInstrument(channelId);
                            int note = shortMessage.getData1();
                            int velocity = shortMessage.getData2();

                            if (useStop) {
                                playNote.noteOff(instrumentId, note);
                            }
                            Debug.print(String.format("Channel: %d, Instrument: %d, NoteOff: %d, Velocity: %d",
                                    channelId,
                                    instrumentId,
                                    note,
                                    velocity));
                            break;
                        }
                        case ShortMessage.PROGRAM_CHANGE: {
                            track.setChannelInstrument(shortMessage.getChannel(), shortMessage.getData1());
                            Debug.print(String.format("Channel: %d, Program Change: %d",
                                    shortMessage.getChannel(),
                                    shortMessage.getData1()));
                            break;
                        }
                        default: {
                            Debug.print(String.format("Channel: %s; Data1:%s, Data2:%s",
                                    Integer.toHexString(shortMessage.getChannel()),
                                    Integer.toHexString(shortMessage.getData1()),
                                    Integer.toHexString(shortMessage.getData1())));
                        }
                    }
                } else if (message instanceof MetaMessage) {
                    MetaMessage metaMessage = (MetaMessage) message;
                    Debug.print(String.format("Meta message: Type: %s, ", Integer.toHexString(metaMessage.getType())));

                    switch (metaMessage.getType()) {
                        case 0x01: { // 文字事件
                            Debug.print("Text Event: " + new String(metaMessage.getData()));
                            break;
                        }
                        case 0x02: { // 版权公告 wait to finish
                            Debug.print("Copyright: " + new String(metaMessage.getData()));
                            break;
                        }
                        case 0x03: { // 歌曲名称/音轨名称 wait to finish
                            if (i == 0) {
                                Debug.print("Song name: " + new String(metaMessage.getData()));
                            } else {
                                Debug.print("Track name: " + new String(metaMessage.getData()));
                            }
                            break;
                        }
                        case 0x04: { // 乐器名 wait to finish
                            Debug.print("Instrument Name: " + new String(metaMessage.getData()));
                            break;
                        }
                        case 0x05: { // 歌词 wait to finish
                            Debug.print("Lyrics: " + new String(metaMessage.getData()));
                            break;
                        }
                        case 0x2f: { // 音轨终止 ignore
                            Debug.print("End of track");
                            break;
                        }
                        case 0x51: { // 更改速度,微秒 每拍
                            String dataHex = Hex.encodeHexString(metaMessage.getData());
                            setSpeed(Integer.parseInt(dataHex, 16));
                            Debug.print(String.format("Change speed to: %s", dataHex));
                            break;
                        }
                        case 0x58:// 指定节拍 ignore
                        case 0x59:// 秘钥签名 ignore
                            Debug.print("Ignored");
                            break;
                        default: {
                            Debug.print(String.format("Data: %s; RawMessage:%s",
                                    Arrays.toString(metaMessage.getData()),
                                    Arrays.toString(metaMessage.getMessage())));
                        }
                    }
                } else if (message instanceof SysexMessage) {
                    SysexMessage sysexMessage = (SysexMessage) message;

                } else {
                    Debug.print("Other Message.");
                }

                Debug.print("\n");
            }
        }

        this.tick++;
    }

    public void calcTicksInOnce() {
        double percent = microsecondPerTick / minimumDelayInMicrosecond;
        int times = (int) Math.ceil(1 / percent);
        if (times < 1) times = 1;
        ticksInOnce = times;
    }

    public void setSpeed(int microsecondPerBeat) throws MidiData.UnsupportedMidiFileType {
        microsecondPerTick = midiData.getMicrosecondPerTick(microsecondPerBeat);
        calcTicksInOnce();
        microsecondPerOnce = microsecondPerTick * ticksInOnce;
    }

    public void stop() {
        running = false;
    }
}
