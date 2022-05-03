package io.github.biligoldenwater.midiplayer.utils

import io.github.biligoldenwater.midiplayer.component.MidiDisplay
import io.github.biligoldenwater.midiplayer.data.MidiData
import io.github.biligoldenwater.midiplayer.data.MidiData.UnsupportedMidiFileType
import io.github.biligoldenwater.midiplayer.instance
import io.github.biligoldenwater.midiplayer.utils.particlegui.ParticleGui
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import java.io.File
import java.nio.ByteBuffer
import java.util.*
import javax.sound.midi.MetaMessage
import javax.sound.midi.ShortMessage
import javax.sound.midi.SysexMessage
import kotlin.math.ceil

class MidiPlay(
    private val midiFile: File,
    private val playNote: PlayNote,
    private val player: Player?,
    private val useStop: Boolean
) : BukkitRunnable() {
    private val minimumDelayInMicrosecond = 40 * 1e3 // 50 ms max
    private var midiData: MidiData? = null
    private var midiDisplay: MidiDisplay? = null
    private var particleGui: ParticleGui? = null
    private var tickLength: Long = 0
    private var tick: Long = 0
    private var microsecondPerTick = 1000.0
    private var microsecondPerOnce = minimumDelayInMicrosecond
    private var running = false
    private var ticksInOnce = 1

    override fun run() {
        running = true
        try {
            midiData = MidiData(midiFile)
            if (midiData!!.fileType == 2) {
                instance.logger.warning("Unsupported MIDI file type; Name:" + midiFile.name)
                return
            }
            tickLength = midiData!!.tickLength
            if (player != null) {
                midiDisplay = MidiDisplay(tickLength)
                particleGui = ParticleGui(player, midiDisplay!!.window)
                particleGui!!.distance = 5.0
                particleGui!!.runTaskTimerAsynchronously(instance, 0, 2)
            }
            while (running) {
                val timeStart = System.currentTimeMillis()
                for (i in 0 until ticksInOnce) {
                    tick()
                }
                val timeEnd = System.currentTimeMillis()
                val timeCost = timeEnd - timeStart
                val millis = (microsecondPerOnce / 1000).toLong()
                val nanos = (microsecondPerOnce % 1000).toInt()
                val finalMillisDelay = millis - timeCost
                Thread.sleep(Math.max(finalMillisDelay, 0), nanos)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Throws(Exception::class)
    fun tick() {
        if (midiData == null || tick >= tickLength) { // 如果数据为空 或 已经播放完毕
            stop()
            return
        }
        calcTicksInOnce()
        if (midiDisplay != null) {
            midiDisplay!!.setTick(tick)
        }
        val tracks = midiData!!.tracks // 获取所有轨道
        var i = 0
        val len = tracks.size
        while (i < len) {
            val track = tracks[i] // 获取当前轨道
            val messages = track.getMessages(tick) // 获取指定 tick 的 MidiMessage 列表
            for (message in messages) {
                Debug.print(String.format("@%d Track: %d, ", tick, i))
                when (message) {
                    is ShortMessage -> {
                        Debug.print(
                            String.format(
                                "Short message: Command: %s, ",
                                Integer.toHexString(message.command)
                            )
                        )
                        when (message.command) {
                            ShortMessage.NOTE_ON -> {
                                val channelId = message.channel
                                val instrumentId = track.getChannelInstrument(channelId)
                                val note = message.data1
                                val velocity = message.data2
                                if (midiDisplay != null) {
                                    midiDisplay!!.noteOn(note)
                                }
                                playNote.noteOn(instrumentId, note, velocity)
                                Debug.print(
                                    String.format(
                                        "Channel: %d, Instrument: %d, NoteOn: %d, Velocity: %d",
                                        channelId,
                                        instrumentId,
                                        note,
                                        velocity
                                    )
                                )
                            }
                            ShortMessage.NOTE_OFF -> {
                                val channelId = message.channel
                                val instrumentId = track.getChannelInstrument(channelId)
                                val note = message.data1
                                val velocity = message.data2
                                if (midiDisplay != null) {
                                    midiDisplay!!.noteOff(note)
                                }
                                if (useStop) {
                                    playNote.noteOff(instrumentId, note)
                                }
                                Debug.print(
                                    String.format(
                                        "Channel: %d, Instrument: %d, NoteOff: %d, Velocity: %d",
                                        channelId,
                                        instrumentId,
                                        note,
                                        velocity
                                    )
                                )
                            }
                            ShortMessage.PROGRAM_CHANGE -> {
                                track.setChannelInstrument(message.channel, message.data1)
                                Debug.print(
                                    String.format(
                                        "Channel: %d, Program Change: %d",
                                        message.channel,
                                        message.data1
                                    )
                                )
                            }
                            else -> {
                                Debug.print(
                                    String.format(
                                        "Channel: %s; Data1:%s, Data2:%s",
                                        Integer.toHexString(message.channel),
                                        Integer.toHexString(message.data1),
                                        Integer.toHexString(message.data1)
                                    )
                                )
                            }
                        }
                    }
                    is MetaMessage -> {
                        Debug.print(String.format("Meta message: Type: %s, ", Integer.toHexString(message.type)))
                        when (message.type) {
                            0x01 -> {
                                // 文字事件
                                Debug.print("Text Event: " + String(message.data))
                            }
                            0x02 -> {
                                // 版权公告 wait to finish
                                Debug.print("Copyright: " + String(message.data))
                            }
                            0x03 -> {
                                // 歌曲名称/音轨名称 wait to finish
                                if (i == 0) {
                                    Debug.print("Song name: " + String(message.data))
                                } else {
                                    Debug.print("Track name: " + String(message.data))
                                }
                            }
                            0x04 -> {
                                // 乐器名 wait to finish
                                Debug.print("Instrument Name: " + String(message.data))
                            }
                            0x05 -> {
                                // 歌词 wait to finish
                                Debug.print("Lyrics: " + String(message.data))
                            }
                            0x2f -> {
                                // 音轨终止 ignore
                                Debug.print("End of track")
                            }
                            0x51 -> {
                                // 更改速度,微秒 每拍
                                val speed = ByteBuffer.wrap(message.data, 0, message.data.size).toHexString()

                                Debug.print(
                                    "Change speed to: $speed, ${
                                        message.data.joinToString(
                                            prefix = "[",
                                            postfix = "]",
                                            separator = ", "
                                        )
                                    }"
                                )
                                setSpeed(speed.toInt(16))
                            }
                            0x58, 0x59 -> Debug.print("Ignored")
                            else -> {
                                Debug.print(
                                    String.format(
                                        "Data: %s; RawMessage:%s",
                                        Arrays.toString(message.data),
                                        Arrays.toString(message.message)
                                    )
                                )
                            }
                        }
                    }
                    is SysexMessage -> {
                    }
                    else -> {
                        Debug.print("Other Message.")
                    }
                }
                Debug.print("\n")
            }
            i++
        }
        tick++
    }

    private fun calcTicksInOnce() {
        val percent = microsecondPerTick / minimumDelayInMicrosecond
        var times = ceil(1 / percent).toInt()
        if (times < 1) times = 1
        ticksInOnce = times
    }

    @Throws(UnsupportedMidiFileType::class)
    fun setSpeed(microsecondPerBeat: Int) {
        microsecondPerTick = midiData!!.getMicrosecondPerTick(microsecondPerBeat)
        calcTicksInOnce()
        microsecondPerOnce = microsecondPerTick * ticksInOnce
    }

    fun stop() {
        running = false
        if (particleGui != null) {
            particleGui!!.cancel()
        }
    }
}