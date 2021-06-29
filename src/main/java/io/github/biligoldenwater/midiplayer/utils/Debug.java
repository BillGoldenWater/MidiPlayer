package io.github.biligoldenwater.midiplayer.utils;

public class Debug {
    public static void print(String debugMessage) {
        if (true) { //MidiPlayer.getInstance().isDebug()
            System.out.print(debugMessage);
        }
    }
}
