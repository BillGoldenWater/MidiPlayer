package io.github.biligoldenwater.midiplayer.utils;

import io.github.biligoldenwater.midiplayer.MidiPlayer;

public class Debug {
    public static void print(String debugMessage) {
        if (MidiPlayer.getInstance().isDebug()) {
            System.out.print(debugMessage);
        }
    }
}
