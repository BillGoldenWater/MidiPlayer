package io.github.biligoldenwater.midiplayer.utils

import io.github.biligoldenwater.midiplayer.utils.PlayNote.ResourcePack.*
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.entity.Player
import kotlin.math.pow

fun getPitch(note: Int): Float {
    return 2.0.pow(((note - 12) / 12f).toDouble()).toFloat()
}

fun playNote(
    resourcePack: PlayNote.ResourcePack,
    player: Player,
    loc: Location,
    instrumentId: Int,
    note: Int,
    velocity: Float
) {
    @Suppress("NAME_SHADOWING") var note = note
    when (resourcePack) {
        Vanilla -> {
            when (instrumentId) {
                0, 1, 2, 3, 4, 5, 6, 7, 46 -> {
                    // block.note_block.harp F#3-F#5
                    note -= 54
                    if (note > 24 || note < 0) return
                    player.playSound(
                        loc,
                        Sound.BLOCK_NOTE_BLOCK_HARP,
                        SoundCategory.RECORDS,
                        velocity,
                        getPitch(note)
                    )
                }
                8, 9, 10 -> {
                    // block.note_block.bell F#5-F#7
                    note -= 78
                    if (note > 24 || note < 0) return
                    player.playSound(
                        loc,
                        Sound.BLOCK_NOTE_BLOCK_BELL,
                        SoundCategory.RECORDS,
                        velocity,
                        getPitch(note)
                    )
                }
                12 -> {
                    // block.note_block.iron_xylophone F#3-F#5
                    note -= 54
                    if (note > 24 || note < 0) return
                    player.playSound(
                        loc,
                        Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE,
                        SoundCategory.RECORDS,
                        velocity,
                        getPitch(note)
                    )
                }
                13 -> {
                    // block.note_block.xylophone F#5-F#7
                    note -= 78
                    if (note > 24 || note < 0) return
                    player.playSound(
                        loc,
                        Sound.BLOCK_NOTE_BLOCK_XYLOPHONE,
                        SoundCategory.RECORDS,
                        velocity,
                        getPitch(note)
                    )
                }
                14 -> {
                    // block.note_block.chime F#5-F#7
                    note -= 78
                    if (note > 24 || note < 0) return
                    player.playSound(
                        loc,
                        Sound.BLOCK_NOTE_BLOCK_CHIME,
                        SoundCategory.RECORDS,
                        velocity,
                        getPitch(note)
                    )
                }
                24, 25, 26, 27, 28, 29, 30, 31 -> {
                    // block.note_block.guitar F#2-F#4
                    note -= 42
                    if (note > 24 || note < 0) return
                    player.playSound(
                        loc,
                        Sound.BLOCK_NOTE_BLOCK_GUITAR,
                        SoundCategory.RECORDS,
                        velocity,
                        getPitch(note)
                    )
                }
                32, 33, 34, 35, 36, 37, 38, 39 -> {
                    // block.note_block.bass F#1-F#3
                    note -= 30
                    if (note > 24 || note < 0) return
                    player.playSound(
                        loc,
                        Sound.BLOCK_NOTE_BLOCK_BASS,
                        SoundCategory.RECORDS,
                        velocity,
                        getPitch(note)
                    )
                }
                72, 73, 74, 75, 76, 77, 78, 79 -> {
                    // block.note_block.flute F#4–F#6
                    note -= 66
                    if (note > 24 || note < 0) return
                    player.playSound(
                        loc,
                        Sound.BLOCK_NOTE_BLOCK_FLUTE,
                        SoundCategory.RECORDS,
                        velocity,
                        getPitch(note)
                    )
                }
                80, 81 -> {
                    // block.note_block.bit F#3-F#5
                    note -= 54
                    if (note > 24 || note < 0) return
                    player.playSound(
                        loc,
                        Sound.BLOCK_NOTE_BLOCK_BIT,
                        SoundCategory.RECORDS,
                        velocity,
                        getPitch(note)
                    )
                }
                105 -> {
                    // block.note_block.banjo F#3-F#5
                    note -= 54
                    if (note > 24 || note < 0) return
                    player.playSound(
                        loc,
                        Sound.BLOCK_NOTE_BLOCK_BANJO,
                        SoundCategory.RECORDS,
                        velocity,
                        getPitch(note)
                    )
                }
            }
        }
        RealPiano -> {
            if (instrumentId == 0) {
                player.playSound(loc, "minecraft:lkrb.piano.p" + note + "fff", SoundCategory.RECORDS, velocity * 2, 1f)
            } else {
                playNote(Vanilla, player, loc, instrumentId, note, velocity)
            }
        }
        MSGSPiano -> {
            if (instrumentId == 0) {
                player.playSound(loc, "minecraft:piano.$note", SoundCategory.RECORDS, velocity * 2, 1f)
            } else {
                playNote(Vanilla, player, loc, instrumentId, note, velocity)
            }
        }
    }
}

fun stopNote(resourcePack: PlayNote.ResourcePack, player: Player, instrumentId: Int, note: Int) {
    when (resourcePack) {
        RealPiano -> {
            if (instrumentId == 0) {
                player.stopSound("minecraft:lkrb.piano.p" + note + "fff")
            }
        }
        MSGSPiano -> {
            if (instrumentId == 0) {
                player.stopSound("minecraft:piano.$note")
            }
        }
        Vanilla -> {}
    }
}