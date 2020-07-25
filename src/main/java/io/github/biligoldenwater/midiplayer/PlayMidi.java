package io.github.biligoldenwater.midiplayer;

import io.github.biligoldenwater.midiplayer.modules.PlayNote;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static io.github.biligoldenwater.midiplayer.modules.int2hexStrOrHexStr2int.hexStr2int;
import static io.github.biligoldenwater.midiplayer.modules.int2hexStrOrHexStr2int.int2hexStr;

public class PlayMidi {
    private double msPerTick;
    private Map<String,List<List<Integer>>> midiData = new HashMap<>();
    //private long tick = 0;
    private long midiLength;
    private boolean isRunning = false;

    public void initMidi(JavaPlugin plugin, File midiFile){
        try {
            Sequence s1 = MidiSystem.getSequence(midiFile);
            //plugin.getLogger().info("tickLength:"+ s1.getTickLength());
            //plugin.getLogger().info(Arrays.toString(s1.getTracks()[0].get(0).getMessage().getMessage()));
            midiLength = s1.getTickLength();
            Track[] tracks = s1.getTracks();
            int ticksPerBeat = s1.getResolution();
            plugin.getLogger().info("ticksPerBeat:"+ticksPerBeat);
            int msPerBeat;
            int trackID = 0;
            int trackCount = 0;

            for (int i = 0;i<tracks.length;++i) {
                Track track  = tracks[i];
                //plugin.getLogger().info("ticks:" + track.ticks());

                long lastTick = 0;
                List<List<Integer>> trackData = new ArrayList<>();

                for(int x = 0;x<track.size();++x) {
                    byte[] chars = track.get(x).getMessage().getMessage();

                    List<Integer> event = new ArrayList<>();

                    long nowTick = track.get(x).getTick();
                    if (lastTick != nowTick) {
                        List<Integer> delay = new ArrayList<>();
                        int delayTicks = (int) (nowTick - lastTick);
                        delay.add(0xDD);
                        delay.add(delayTicks);
                        trackData.add(delay);
                        //plugin.getLogger().info("delayTicks("+trackID+"):" + delayTicks);
                        lastTick = nowTick;
                    }

                    StringBuilder output;
                    switch (Byte.toUnsignedInt(chars[0])) {
                        case 0xFF://meta-event
                            switch (Byte.toUnsignedInt(chars[1])) {
                                case 0x03://Set name
                                    output = new StringBuilder();
                                    for (int k = 3; k < chars.length; ++k) {
                                        output.append(String.valueOf(getChars(new byte[]{chars[k]})));
                                    }
                                    //plugin.getLogger().info("Name: " + output.toString());
                                    break;
                                case 0x51://Set speed
                                    StringBuilder speedHex = new StringBuilder();
                                    for (int k = 3; k < chars.length; ++k) {
                                        speedHex.append(int2hexStr(Byte.toUnsignedInt(chars[k])));
                                    }
                                    msPerBeat = hexStr2int(speedHex.toString());
                                    event.add(0x51);
                                    event.add((int) getMsPerTick(msPerBeat, ticksPerBeat));
                                    //plugin.getLogger().info("("+msPerBeat+"/"+ticksPerBeat+")Set speed to:" + getMsPerTick(msPerBeat,ticksPerBeat));
                                    break;
                                case 0x04://Set musical instrument
                                    output = new StringBuilder();
                                    output.append("Musical instrument:");
                                    for (int k = 3; k < chars.length; ++k) {
                                        output.append(String.valueOf(getChars(new byte[]{chars[k]})));
                                    }
                                    plugin.getLogger().info(output.toString());
                                    break;
                                case 0x05:
                                    event.add(0x05);
                                    for (int k = 3; k < chars.length; ++k) {
                                        event.add((int) chars[k]);
                                    }
                                    break;
                                case 0x58:
                                case 0x59:
                                    break;
                                default:
                                    //plugin.getLogger().info(Arrays.toString(chars));
                                    break;
                            }
                            break;
                        case 0x9F:
                        case 0x9E:
                        case 0x9D:
                        case 0x9C:
                        case 0x9B:
                        case 0x9A:
                        case 0x99:
                        case 0x98:
                        case 0x97:
                        case 0x96:
                        case 0x95:
                        case 0x94:
                        case 0x93:
                        case 0x92:
                        case 0x91:
                        case 0x90://note on
                            event.add(0x90);
                            event.add(Byte.toUnsignedInt(chars[1]));
                            event.add(Byte.toUnsignedInt(chars[2]));
                            //plugin.getLogger().info("Note " + chars[1] + "on.");
                            break;
                        case 0x8F:
                        case 0x8E:
                        case 0x8D:
                        case 0x8C:
                        case 0x8B:
                        case 0x8A:
                        case 0x89:
                        case 0x88:
                        case 0x87:
                        case 0x86:
                        case 0x85:
                        case 0x84:
                        case 0x83:
                        case 0x82:
                        case 0x81:
                        case 0x80://note off
                            event.add(0x80);
                            event.add(Byte.toUnsignedInt(chars[1]));
                            event.add(Byte.toUnsignedInt(chars[2]));
                            //plugin.getLogger().info("Note " + chars[1] + "off.");
                            break;
                        case 0xC0://Change musical instrument
                            event.add(0xC0);
                            event.add(Byte.toUnsignedInt(chars[1]));
                            //plugin.getLogger().info("Set musical instrument to:"+chars[1]);
                            break;
                        default:
                            plugin.getLogger().info(Arrays.toString(chars));
                            break;
                    }

                    if (!event.isEmpty()) {
                        trackData.add(event);
                    }
                }
                if (!trackData.isEmpty()) {
                    if(i!=0){
                        trackCount++;
                    }
                    midiData.put(String.valueOf(trackID), trackData);
                    List<List<Integer>> a = new ArrayList<>();
                    List<Integer> b = new ArrayList<>();
                    b.add(trackCount);
                    a.add(b);
                    midiData.put("trackCount",a);
                    trackID++;
                }
            }



        } catch (Exception e) {
            e.printStackTrace();
        }
        //plugin.getLogger().info(String.valueOf(midiData));
    }

    public void playMidi(Player targetPlayer ,boolean useSoundStop,int resourcePack){
        isRunning = true;
        BukkitRunnable midiPlayer = new BukkitRunnable() {
            @Override
            public void run() {
                int trackCount = midiData.get("trackCount").get(0).get(0);
                //MidiPlayer.getInstance().getLogger().info(String.valueOf(msPerTick)+String.valueOf(midiData));
//                if(trackCount<1){
//                    targetPlayer.sendMessage("Can't play the midi,because it doesn't have music data");
//                    this.cancel();
//                    return;
//                }

                List<BukkitRunnable> tracks = new ArrayList<>();

//                BukkitRunnable updateTick = new BukkitRunnable() {
//                    @Override
//                    public void run() {
//                        while (tick <= midiLength && isRunning){
//                            try {
//                                //MidiPlayer.getInstance().getLogger().info(""+(long) (msPerTick/1000)+", "+(int) (msPerTick*1000%1000000));
//                                Thread.sleep((long) (msPerTick/1000), (int) (msPerTick*1000%1000000));
//                                //Thread.sleep((long) msPerTick / 1000);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                            tick++;
//                        }
//                    }
//                };
//                tracks.add(updateTick);

                for(int i = 0;i<=trackCount;++i){
                    int finalI = i;
                    BukkitRunnable runnable = new BukkitRunnable() {
                        final int trackId = finalI;
                        final List<List<Integer>> data = midiData.get(String.valueOf(trackId));
                        double nowTick = 0;
                        int musicalInstrument = 0;
                        @Override
                        public void run() {
                            int i = 0;
                            while (i<data.size() && isRunning){
//                                if(nowTick>tick){
//                                    try {
//                                        Thread.sleep((long) msPerTick / 1000);
//                                    } catch (InterruptedException e) {
//                                        e.printStackTrace();
//                                    }
//                                    continue;
//                                }
                                List<Integer> event = data.get(i);
                                switch (event.get(0)){
                                    case 0x90://note on
                                        //targetPlayer.sendMessage(String.valueOf(event.get(0))+" "+event.get(1)+ " " + event.get(2));
                                        if (musicalInstrument<=7 || (musicalInstrument>=16 && musicalInstrument<=23)){
                                            //MidiPlayer.getInstance().getLogger().info(String.valueOf(nowTick)+" "+String.valueOf(tick));
                                            PlayNote.playNote(targetPlayer,event.get(1),event.get(2),resourcePack);
                                        } else {
                                            targetPlayer.sendMessage("Doesn't have musical instrument:"+musicalInstrument);
                                        }
                                        break;
                                    case 0x80://note off
                                        if(resourcePack!=PlayNote.vanilla && useSoundStop){
                                            if (musicalInstrument<=7){
                                                PlayNote.stopNote(targetPlayer,event.get(1),event.get(2),resourcePack);
                                            }
                                        }
                                        break;
                                    case 0xC0://Change musical instrument
                                        musicalInstrument = event.get(1);
                                        break;
                                    case 0xDD://Delay
                                        nowTick= nowTick + event.get(1);
                                        try {
                                            Thread.sleep( ((long)msPerTick*event.get(1))/1000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        break;
                                    case 0x05:
                                        StringBuilder output = new StringBuilder();
                                        output.append("Musical instrument:");
                                        for (int k = 1; k < event.size(); ++k) {
                                            output.append(String.valueOf(getChars(new byte[]{event.get(k).byteValue()})));
                                        }
                                        targetPlayer.sendMessage(output.toString());
                                        break;
                                }
                                float percent = ((float) nowTick / (float) midiLength)*100;
                                StringBuilder progressBar = new StringBuilder();
                                for (int j = 0;j<(int)((percent / 100) *50);++j){
                                    progressBar.append("=");
                                }
                                for (int j = 0;j<50-(int)((percent / 100) *50);++j){
                                    progressBar.append("-");
                                }
                                //targetPlayer.sendMessage(String.valueOf(event.get(0))+event.get(1));
                                targetPlayer.sendTitle( "§r", "["+progressBar+"] "+((int) (percent*100) * 1.0 /100) + "%", 0, 100, 0);
                                i++;
                            }
                            targetPlayer.sendTitle( "§r", "§r",0,0,0);
                        }
                    };
                    tracks.add(runnable);
                }
                for(BukkitRunnable runnable:tracks){
                    runnable.runTaskAsynchronously(MidiPlayer.getInstance());
                }
            }
        };
        midiPlayer.runTaskAsynchronously(MidiPlayer.getInstance());
    }

    public void stopSound(){
        isRunning = false;
    }

    public boolean isRunning(){
        return isRunning;
    }

    private double getMsPerTick(int msPerBeat,int ticksPerBeat){
        msPerTick = (double) msPerBeat / (double) ticksPerBeat;
        return msPerTick;
    }

    private char[] getChars (byte[] bytes) {
        Charset cs = StandardCharsets.US_ASCII;
        ByteBuffer bb = ByteBuffer.allocate (bytes.length);
        bb.put (bytes);
        bb.flip ();
        CharBuffer cb = cs.decode (bb);

        return cb.array();
    }

}

