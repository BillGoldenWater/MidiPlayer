package io.github.biligoldenwater.midiplayer.utils;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MidiData {
    private Sequence sequence;
    private List<TrackData> tracks;
    private int fileType = 0;

    public MidiData(File file) throws Exception {
        this.loadData(file); // 加载数据
    }

    public void loadData(File file) throws Exception {
        this.tracks = new ArrayList<>();
        this.sequence = MidiSystem.getSequence(file); // 获取 Sequence
        this.fileType = MidiSystem.getMidiFileFormat(file).getType();

        for (Track track : sequence.getTracks()) { // 获取所有Track数据
            TrackData trackData = new TrackData(track);
            tracks.add(trackData);
        }
    }

    public long getTickLength() {
        return sequence.getTickLength();
    }

    public double getMicrosecondPerTick(int microsecondPerBeat) throws UnsupportedMidiFileType {
        if (sequence.getDivisionType() == Sequence.PPQ) {
            return ((double) sequence.getResolution()) * microsecondPerBeat;
        } else {
            throw new UnsupportedMidiFileType();
        }
    }

    public List<TrackData> getTracks() {
        return tracks;
    }

    public Sequence getSequence() {
        return sequence;
    }

    public int getFileType() {
        return fileType;
    }

    public static class UnsupportedMidiFileType extends Exception {
        public UnsupportedMidiFileType() {
            super();
        }
    }

}
