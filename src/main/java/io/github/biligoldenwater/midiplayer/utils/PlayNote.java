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
            instr = midiSynth.getDefaultSoundbank().getInstruments();

            mChannels = midiSynth.getChannels();
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void noteOn(int note, int instrument, int velocity) {
        midiSynth.loadInstrument(instr[instrument]);//load an instrument

        mChannels[0].noteOn(note, velocity);//On channel 0, play note number 60 with velocity 100
    }

    public void noteOff(int note, int instrument) {
        midiSynth.loadInstrument(instr[instrument]);//load an instrument

        mChannels[0].noteOff(note);//turn of the note
    }
}
