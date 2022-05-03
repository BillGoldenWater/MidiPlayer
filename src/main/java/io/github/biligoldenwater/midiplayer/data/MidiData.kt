package io.github.biligoldenwater.midiplayer.data

import java.io.File
import javax.sound.midi.MidiSystem
import javax.sound.midi.Sequence

class MidiData(file: File?) {
    var sequence: Sequence
    val tracks: MutableList<TrackData> = mutableListOf()
    var fileType = 0

    init {
        sequence = MidiSystem.getSequence(file) // 获取 Sequence
        fileType = MidiSystem.getMidiFileFormat(file).type
        sequence.tracks?.forEach {
            tracks.add(TrackData(it))
        }
    }

    val tickLength: Long
        get() = sequence.tickLength

    @Throws(UnsupportedMidiFileType::class)
    fun getMicrosecondPerTick(microsecondPerBeat: Int): Double {
        return if (sequence.divisionType == Sequence.PPQ) {
            microsecondPerBeat / sequence.resolution.toDouble()
        } else {
            throw UnsupportedMidiFileType()
        }
    }

    class UnsupportedMidiFileType : Exception()
}