package io.github.biligoldenwater.midiplayer.utils;

import javax.sound.midi.*;

public class PlayNote {
    Synthesizer midiSynth;
    Instrument[] instr;
    MidiChannel[] mChannels;

    public PlayNote() {
        try {
            /* Create a new Sythesizer and open it. Most of
             * the methods you will want to use to expand on this
             * example can be found in the Java documentation here:
             * https://docs.oracle.com/javase/7/docs/api/javax/sound/midi/Synthesizer.html
             */
            midiSynth = MidiSystem.getSynthesizer();
            midiSynth.open();

            //get and load default instrument and channel lists
            midiSynth.loadAllInstruments(midiSynth.getDefaultSoundbank());

            mChannels = midiSynth.getChannels();
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void noteOn(int channel, int instrument, int note, int velocity) {
        mChannels[channel].programChange(instrument);
        mChannels[channel].noteOn(note, velocity);//On channel 0, play note number 60 with velocity 100
    }

    public void noteOff(int channel, int instrument, int note, int velocity) {
        mChannels[channel].programChange(instrument);
        mChannels[channel].noteOff(note, velocity);//turn of the note
    }
}
