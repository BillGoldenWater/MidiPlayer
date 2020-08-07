package io.github.biligoldenwater.midiplayer.modules;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GetMidis {
    public static List<File> getMidis(File path) {
        List<File> midis = new ArrayList<>();

        File[] musicNames = path.listFiles();

        if (musicNames == null || musicNames.length < 1){
            return midis;
        }

        Collections.addAll(midis, musicNames);

        return midis;
    }
}
