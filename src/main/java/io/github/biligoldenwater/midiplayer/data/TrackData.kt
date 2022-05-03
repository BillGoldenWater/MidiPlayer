package io.github.biligoldenwater.midiplayer.data

import javax.sound.midi.MidiMessage
import javax.sound.midi.Track

class TrackData(track: Track) {
    private val midiMessages: MutableMap<Long, MutableList<MidiMessage>> = mutableMapOf()
    private val channelInstrument: MutableMap<Int, Int> = HashMap()

    init {
        for (i in 0 until track.size()) { // 获取每个消息
            val event = track[i] // 获取事件
            midiMessages.getOrPut(event.tick) { mutableListOf() }.add(event.message)
        }
    }

    fun getChannelInstrument(channelId: Int): Int = channelInstrument[channelId] ?: 0

    fun setChannelInstrument(channelId: Int, instrument: Int) {
        channelInstrument[channelId] = instrument
    }

    fun getMessages(tick: Long): List<MidiMessage> {
        return midiMessages[tick] ?: mutableListOf()
    }
}