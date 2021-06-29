package io.github.biligoldenwater.midiplayer.utils;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class PlayNote {
    private Player player;
    private boolean isBroadcast;

    public PlayNote(Player targetPlayer, boolean isBroadcast) {
        this.player = targetPlayer;
        this.isBroadcast = isBroadcast;
    }

    public float calcNotePitch(int note) {
//        return (float) Math.pow(2, (note - 66) / 12.0);
        return (float) Math.pow(2, (note - 66) / 12.0);
    }

    public void noteOn(int instrumentId, int note, int velocity) {
        Location loc = player.getLocation();
        World world = loc.getWorld();
        if (isBroadcast && world != null) {
//            world.playSound(loc, Sound.BLOCK_NOTE_BLOCK_HARP, velocity * 2, calcNotePitch(note));
            world.playSound(loc, "minecraft:lkrb.piano.p"+note+ getVelocity(velocity), velocity * 2, 1);
        } else {
            player.playSound(loc, Sound.BLOCK_NOTE_BLOCK_HARP, velocity * 2, calcNotePitch(note));
        }
    }

    public void noteOff(int instrumentId, int note) {

    }

    public String getVelocity(long velocity) {
        if (velocity <= 20+10) {
            return "ppp";
        } else if (velocity <= 40+5) {
            return "pp";
        } else if (velocity <= 50+7) {
            return "p";
        } else if (velocity <= 64+3) {
            return "mp";
        } else if (velocity <= 70+5) {
            return "mf";
        } else if (velocity <= 80+5) {
            return "f";
        } else if (velocity <= 90+13) {
            return "ff";
        }
        return "fff";
    }
}
