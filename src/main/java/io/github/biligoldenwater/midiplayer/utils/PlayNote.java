package io.github.biligoldenwater.midiplayer.utils;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

public class PlayNote {
    private final ResourcePack resourcePack;
    private final boolean broadcast;
    private final boolean forcePiano;
    private final int range;

    private final Player player;
    private final Location loc;

    public PlayNote(ResourcePack resourcePack, Player targetPlayer, boolean broadcast, int range, boolean forcePiano) {
        this.resourcePack = resourcePack;
        this.broadcast = broadcast;
        this.range = range;
        this.forcePiano = forcePiano;

        this.player = targetPlayer;
        this.loc = targetPlayer.getLocation();
    }

    public PlayNote(ResourcePack resourcePack, Location loc, int range, boolean forcePiano) {
        this.resourcePack = resourcePack;
        this.broadcast = false;
        this.range = range;
        this.forcePiano = forcePiano;

        this.player = null;
        this.loc = loc;
    }

    public void noteOn(int instrumentId, int note, int velocity) {
        if (forcePiano) instrumentId = 0;

        if (player == null || broadcast) { // 如果玩家为null(固定位置) 或 为广播
            Location loc;
            if (player != null) { // 如果玩家不为空 则使用玩家的位置
                loc = player.getLocation();
            } else {
                loc = this.loc;
            }

            assert loc.getWorld() != null;
            for (Player p : loc.getWorld().getPlayers()) { // 获取世界内所有玩家
                if (p.getLocation().distance(loc) <= range) { // 如果在范围内则播放
                    playNote(resourcePack, p, loc, instrumentId, note, (float) velocity / 127);
                }
            }
        } else {
            playNote(resourcePack, player, player.getLocation(), instrumentId, note, velocity / 127.0F);
        }
    }

    public void noteOff(int instrumentId, int note) {
        if (forcePiano) instrumentId = 0;

        if (player == null || broadcast) { // 如果玩家为null(固定位置) 或 为广播
            Location loc;
            if (player != null) { // 如果玩家不为空 则使用玩家的位置
                loc = player.getLocation();
            } else {
                loc = this.loc;
            }

            assert loc.getWorld() != null;
            for (Player p : loc.getWorld().getPlayers()) { // 获取世界内所有玩家
                if (p.getLocation().distance(loc) <= range) { // 如果在范围内则停止
                    stopNote(resourcePack, p, instrumentId, note);
                }
            }
        } else {
            stopNote(resourcePack, player, instrumentId, note);
        }
    }

    public static float getPitch(int startAt, int note) {
        return (float) Math.pow(2.0, (note - 12) / 12f);
    }

    public static void playNote(ResourcePack resourcePack, Player player, Location loc, int instrumentId, int note, float velocity) {
        switch (resourcePack) {
            case vanilla: {
                switch (instrumentId) {
                    // block.note_block.pling F#3-F#5
                    // block.note_block.didgeridoo F#1-F#3
                    // block.note_block.cow_bell F#4-F#6

                    // block.note_block.basedrum
                    // block.note_block.hat
                    // block.note_block.snare
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 46: { // block.note_block.harp F#3-F#5
                        note = note - 54;
                        if (note > 24 || note < 0) return;
                        player.playSound(loc, Sound.BLOCK_NOTE_BLOCK_HARP, SoundCategory.RECORDS, velocity, getPitch(54, note));
                        break;
                    }
                    case 8:
                    case 9:
                    case 10: { // block.note_block.bell F#5-F#7
                        note = note - 78;
                        if (note > 24 || note < 0) return;
                        player.playSound(loc, Sound.BLOCK_NOTE_BLOCK_BELL, SoundCategory.RECORDS, velocity, getPitch(54, note));
                        break;
                    }
                    case 12: {// block.note_block.iron_xylophone F#3-F#5
                        note = note - 54;
                        if (note > 24 || note < 0) return;
                        player.playSound(loc, Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE, SoundCategory.RECORDS, velocity, getPitch(54, note));
                        break;
                    }
                    case 13: { // block.note_block.xylophone F#5-F#7
                        note = note - 78;
                        if (note > 24 || note < 0) return;
                        player.playSound(loc, Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, SoundCategory.RECORDS, velocity, getPitch(54, note));
                        break;
                    }
                    case 14: { // block.note_block.chime F#5-F#7
                        note = note - 78;
                        if (note > 24 || note < 0) return;
                        player.playSound(loc, Sound.BLOCK_NOTE_BLOCK_CHIME, SoundCategory.RECORDS, velocity, getPitch(54, note));
                        break;
                    }
                    case 24:
                    case 25:
                    case 26:
                    case 27:
                    case 28:
                    case 29:
                    case 30:
                    case 31: { // block.note_block.guitar F#2-F#4
                        note = note - 42;
                        if (note > 24 || note < 0) return;
                        player.playSound(loc, Sound.BLOCK_NOTE_BLOCK_GUITAR, SoundCategory.RECORDS, velocity, getPitch(54, note));
                        break;
                    }
                    case 32:
                    case 33:
                    case 34:
                    case 35:
                    case 36:
                    case 37:
                    case 38:
                    case 39: { // block.note_block.bass F#1-F#3
                        note = note - 30;
                        if (note > 24 || note < 0) return;
                        player.playSound(loc, Sound.BLOCK_NOTE_BLOCK_BASS, SoundCategory.RECORDS, velocity, getPitch(54, note));
                        break;
                    }
                    case 72:
                    case 73:
                    case 74:
                    case 75:
                    case 76:
                    case 77:
                    case 78:
                    case 79: { // block.note_block.flute F#4–F#6
                        note = note - 66;
                        if (note > 24 || note < 0) return;
                        player.playSound(loc, Sound.BLOCK_NOTE_BLOCK_FLUTE, SoundCategory.RECORDS, velocity, getPitch(54, note));
                        break;
                    }
                    case 80:
                    case 81: { // block.note_block.bit F#3-F#5
                        note = note - 54;
                        if (note > 24 || note < 0) return;
                        player.playSound(loc, Sound.BLOCK_NOTE_BLOCK_BIT, SoundCategory.RECORDS, velocity, getPitch(54, note));
                        break;
                    }
                    case 105: { // block.note_block.banjo F#3-F#5
                        note = note - 54;
                        if (note > 24 || note < 0) return;
                        player.playSound(loc, Sound.BLOCK_NOTE_BLOCK_BANJO, SoundCategory.RECORDS, velocity, getPitch(54, note));
                        break;
                    }
                }
                break;
            }
            case realPiano: {
                if (instrumentId == 0) {
                    player.playSound(loc, "minecraft:lkrb.piano.p" + note + "fff", velocity * 2, 1);
                }
                break;
            }
            case MSGSPiano: {
                if (instrumentId == 0) {
                    player.playSound(loc, "minecraft:piano." + note, velocity * 2, 1);
                }
                break;
            }
        }
    }

    public static void stopNote(ResourcePack resourcePack, Player player, int instrumentId, int note) {
        switch (resourcePack) {
            case realPiano: {
                if (instrumentId == 0) {
                    player.stopSound("minecraft:lkrb.piano.p" + note + "fff");
                }
                break;
            }
            case MSGSPiano: {
                if (instrumentId == 0) {
                    player.stopSound("minecraft:piano." + note);
                }
                break;
            }
        }
    }

    public enum ResourcePack {
        vanilla,
        realPiano,
        MSGSPiano
    }
}
