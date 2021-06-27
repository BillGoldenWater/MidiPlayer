package io.github.biligoldenwater.midiplayer.utils;

import javax.annotation.Nullable;
import javax.sound.midi.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MidiData {
    private Sequence sequence;
    private List<TrackData> tracks;
    private int resolution;

    public MidiData(File file) throws Exception {
        this.loadData(file); // 加载数据
    }

    public void loadData(File file) throws Exception {
        this.tracks = new ArrayList<>();
        this.sequence = MidiSystem.getSequence(file); // 获取 Sequence
        this.resolution = sequence.getResolution();

        for (Track track : sequence.getTracks()) { // 获取所有Track数据
            TrackData trackData = new TrackData(track);
            tracks.add(trackData);
        }
    }

    public long getTickLength() {
        return sequence.getTickLength();
    }

    public List<TrackData> getTracks() {
        return tracks;
    }

    public Sequence getSequence() {
        return sequence;
    }

    public static class TrackData {
        private Map<Long, List<MidiMessage>> midiMessages;
        private final Map<Byte, Byte> channelInstrument = new HashMap<>();

        public TrackData(Track track) {
            this.loadData(track); // 加载数据
        }

        public void loadData(Track track) {

            midiMessages = new HashMap<>();
            for (int i = 0; i < track.size(); i++) { // 获取每个消息
                MidiEvent event = track.get(i); // 获取事件
                List<MidiMessage> messages = midiMessages.get(event.getTick()); // 尝试获取已存在的消息列表
                if (messages == null) { // 如果为空新建一个
                    messages = new ArrayList<>();
                }
                messages.add(event.getMessage()); // 添加消息
                midiMessages.put(event.getTick(), messages); // 存入Map
            }
        }

        public byte getChannelInstrument(byte channelId) {
            Byte instrument = channelInstrument.get(channelId);
            if (instrument == null) {
                instrument = 0;
            }
            return instrument;
        }

        public void setChannelInstrument(byte channelId, byte instrument) {
            channelInstrument.put(channelId, instrument);
        }

        @Nullable
        public List<MidiMessage> getMessages(long tick) {
            return midiMessages.get(tick);
        }
    }
}
