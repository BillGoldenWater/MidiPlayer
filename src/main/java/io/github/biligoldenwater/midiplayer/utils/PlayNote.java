package io.github.biligoldenwater.midiplayer.utils;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlayNote {
    private final ResourcePack resourcePack;
    private final boolean broadcast;
    private final int range;

    private final Player player;
    private final Location loc;

    public PlayNote(ResourcePack resourcePack, Player targetPlayer, boolean broadcast, int range) {
        this.resourcePack = resourcePack;
        this.player = targetPlayer;
        this.broadcast = broadcast;
        this.range = range;
        this.loc = targetPlayer.getLocation();
    }

    public PlayNote(ResourcePack resourcePack, Location loc, int range) {
        this.resourcePack = resourcePack;
        this.loc = loc;
        this.range = range;
        this.broadcast = false;
        this.player = null;
    }

    public void noteOn(int instrumentId, int note, int velocity) {
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
                    playSound(resourcePack, p, loc, instrumentId, note, (float) velocity / 127);
                }
            }
        } else {
            playSound(resourcePack, player, player.getLocation(), instrumentId, note, velocity / 127.0F);
        }
    }

    public void noteOff(int instrumentId, int note) {
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
                    stopSound(resourcePack, p, instrumentId, note);
                }
            }
        } else {
            stopSound(resourcePack, player, instrumentId, note);
        }
    }

    public static void playSound(ResourcePack resourcePack, Player player, Location loc, int instrumentId, int note, float velocity) {
        switch (resourcePack) {
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

    public static void stopSound(ResourcePack resourcePack, Player player, int instrumentId, int note) {
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
