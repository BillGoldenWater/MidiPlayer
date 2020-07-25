package io.github.biligoldenwater.midiplayer.modules;

import io.github.biligoldenwater.midiplayer.MidiPlayer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GetMidis {
    public static List<File> getMidis(String path) {
        List<File> midis = new ArrayList<>();
        File musics = new File(path);

        if(musics.exists() && !musics.isDirectory()){
            if (!musics.delete()){
                MidiPlayer.getInstance().getLogger().warning("Fail to delete a same name file for musics folder.");
            }
        }

        if(!musics.exists()){
            if (!musics.mkdir()){
                MidiPlayer.getInstance().getLogger().warning("Fail to create musics folder.");
            }
        }

        String[] musicNames = musics.list();

        if (musicNames == null || musicNames.length < 1){
            return midis;
        }

        for(String fileName : musicNames){
            midis.add(new File(path+"\\"+fileName));
        }
        return midis;
    }
}
