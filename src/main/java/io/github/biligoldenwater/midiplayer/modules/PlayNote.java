package io.github.biligoldenwater.midiplayer.modules;

import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

public class PlayNote {
    public static final byte vanilla = 0;
    public static final byte MSGSPiano = 1;
    public static final byte realPiano = 2;


    public static void playNote(Player player, long note, long strength, byte resourcePack){

        switch (resourcePack){
            case vanilla:
                double perStep = 0.03409;
                player.playSound(player.getLocation(),"minecraft:block.note.harp", SoundCategory.RECORDS,255, (float) ((note-20) * perStep));
//                double perStep = 0.0227;
//                double[] position = {player.getLocation().getX(),player.getLocation().getY(),player.getLocation().getZ()};
//                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),
//                        "playsound minecraft:block.note.chime record "+ player.getName() +
//                                " "+position[0]+" "+position[1]+" "+position[2]+
//                                " 255 "+ ((note-20) * perStep));
                break;
            case realPiano:
                player.playSound(player.getLocation(),"minecraft:lkrb.piano.p"+note+getStrength(strength), SoundCategory.RECORDS,255,1);
//                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),
//                        "playsound minecraft:lkrb.piano.p"+note+getStrength(strength)+" record "+ player.getName() +
//                                " "+player.getLocation().getX()+" "+player.getLocation().getY()+" "+player.getLocation().getZ()+
//                                " 255");//+String.valueOf((strength/0x7F)*255));
                break;
            case MSGSPiano:
                player.playSound(player.getLocation(),"minecraft:piano."+note, SoundCategory.RECORDS,255,1);
//                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),
//                        "playsound minecraft:piano."+note+" record "+ player.getName() +
//                                " "+player.getLocation().getX()+" "+player.getLocation().getY()+" "+player.getLocation().getZ()+
//                                " 255");//+String.valueOf((strength/0x7F)*255));
                break;
        }

    }

    public static String getStrength(long strength){
        if (strength <= 20) {
            return "ppp";
        } else if (strength <= 40) {
            return "pp";
        } else if (strength <= 50) {
            return "p";
        } else if (strength <= 64) {
            return "mp";
        } else if (strength <= 70) {
            return "mf";
        } else if (strength <= 80) {
            return "f";
        } else if (strength <= 90) {
            return "ff";
        }
        return "fff";
    }

    public static void stopNote(Player player, long note, long strength, byte resourcePack){
        switch (resourcePack){
            case vanilla:
                player.stopSound("minecraft:block.note.harp", SoundCategory.RECORDS);
//                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),
//                        "stopsound "+player.getName()+" record minecraft:lkrb.piano.p"+note+getStrength(strength));
                break;
            case realPiano:
                player.stopSound("minecraft:lkrb.piano.p"+note+getStrength(strength), SoundCategory.RECORDS);
//                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),
//                        "stopsound "+player.getName()+" record minecraft:lkrb.piano.p"+note+getStrength(strength));
                break;
            case MSGSPiano:
                player.stopSound("minecraft:piano."+note, SoundCategory.RECORDS);
//                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),
//                        "stopsound "+player.getName()+" record minecraft:piano."+note);
                break;
        }

    }
}
