package io.github.biligoldenwater.midiplayer.utils;

import javax.annotation.Nullable;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Track;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrackData {
    private Map<Long, List<MidiMessage>> midiMessages;
    private final Map<Integer, Integer> channelInstrument = new HashMap<>();

    public TrackData(Track track) {
        this.loadData(track); // 加载数据
    }

    public void loadData(Track track) {

        midiMessages = new HashMap<>();
        for (int i = 0; i < track.size(); i++) { // 获取每个消息
            MidiEvent event = track.get(i); // 获取事件
            List<MidiMessage> messages = midiMessages.get(event.getTick()); // 尝试获取已存在的消息列表
            MidiMessage message = event.getMessage();

            if (messages == null) { // 如果为空新建一个
                messages = new ArrayList<>();
            }
            messages.add(message); // 添加消息

            midiMessages.put(event.getTick(), messages); // 存入Map
        }
    }

    public int getChannelInstrument(int channelId) {
        Integer instrument = channelInstrument.get(channelId);
        if (instrument == null) {
            instrument = 0;
        }
        return instrument;
    }

    public void setChannelInstrument(int channelId, int instrument) {
        channelInstrument.put(channelId, instrument);
    }

    @Nullable
    public List<MidiMessage> getMessages(long tick) {
        return midiMessages.get(tick);
    }
}
