package io.github.biligoldenwater.midiplayer;

import io.github.biligoldenwater.midiplayer.api.PlayingNoteParticles;
import io.github.biligoldenwater.midiplayer.modules.NoteParticle;
import io.github.biligoldenwater.midiplayer.modules.PlayNote;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.github.biligoldenwater.midiplayer.modules.int2hexStrOrHexStr2int.hexStr2int;
import static io.github.biligoldenwater.midiplayer.modules.int2hexStrOrHexStr2int.int2hexStr;

public class PlayMidi {
    private double msPerTick;
    private final Map<String,List<List<Long>>> midiData = new HashMap<>();
    private long tick = 0;
    private int delayMultiple = 1;
    private long midiLength;
    private boolean isRunning = false;
    private double x = 0;
    private double z = 0;
    private double y = 0;
    private byte yaw = 0;
    private List<Location> particles = new ArrayList<>();

    public void initMidi(JavaPlugin plugin, Player player, File midiFile, boolean useDisplayFunction){
        plugin.getLogger().info("Initialize Midi for player:"+player.getName());
        try {
            Sequence s1 = MidiSystem.getSequence(midiFile);
            //plugin.getLogger().info("tickLength:"+ s1.getTickLength());
            //plugin.getLogger().info(Arrays.toString(s1.getTracks()[0].get(0).getMessage().getMessage()));
            midiLength = s1.getTickLength()+100;
            Track[] tracks = s1.getTracks();
            int ticksPerBeat = s1.getResolution();
            //plugin.getLogger().info("ticksPerBeat:"+ticksPerBeat);
            int microSecondPerBeat;
            int trackID = 0;
            int trackCount = 0;

            for (int i = 0;i<tracks.length;++i) {
                Track track  = tracks[i];
                //plugin.getLogger().info("ticks:" + track.ticks());

                long lastTick = 0;
                List<List<Long>> trackData = new ArrayList<>();

                for(int x = 0;x<track.size();++x) {
                    byte[] chars = track.get(x).getMessage().getMessage();
//                    if(Byte.toUnsignedInt(chars[1])==0x2F){
//                        if (!trackData.isEmpty()) {
//                            if(i!=0){
//                                trackCount++;
//                            }
//                            midiData.put(String.valueOf(trackID), trackData);
//                            List<List<Integer>> a = new ArrayList<>();
//                            List<Integer> b = new ArrayList<>();
//                            b.add(trackCount);
//                            a.add(b);
//                            midiData.put("trackCount",a);
//                            trackID++;
//                        }
//                    }

                    List<Long> event = new ArrayList<>();

                    long nowTick = track.get(x).getTick();
                    if (lastTick != nowTick) {
                        List<Long> delay = new ArrayList<>();
                        long delayTicks = nowTick - lastTick;
                        delay.add((long) 0xDD);
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
                                    microSecondPerBeat = hexStr2int(speedHex.toString());
                                    event.add((long) 0x51);
                                    if(msPerTick<getMsPerTick(microSecondPerBeat, ticksPerBeat)){
                                        msPerTick = getMsPerTick(microSecondPerBeat, ticksPerBeat);
                                        plugin.getLogger().info("("+microSecondPerBeat+"/"+ticksPerBeat+")Set speed to:" + getMsPerTick(microSecondPerBeat,ticksPerBeat));
                                    }
                                    event.add((long) getMsPerTick(microSecondPerBeat, ticksPerBeat));
                                    plugin.getLogger().info("("+microSecondPerBeat+"/"+ticksPerBeat+")Track: "+i+" Change speed to:" + getMsPerTick(microSecondPerBeat,ticksPerBeat));
                                    //plugin.getLogger().info(Arrays.toString(chars));

                                    break;
                                case 0x04://Set musical instrument
                                    output = new StringBuilder();
                                    output.append("Musical instrument:");
                                    for (int k = 3; k < chars.length; ++k) {
                                        output.append(String.valueOf(getChars(new byte[]{chars[k]})));
                                    }
                                    //plugin.getLogger().info(output.toString());
                                    break;
                                case 0x05:
                                    event.add((long) 0x05);
                                    for (int k = 3; k < chars.length; ++k) {
                                        event.add((long) Byte.toUnsignedInt(chars[k]));
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
                            event.add((long) 0x90);
                            event.add((long) Byte.toUnsignedInt(chars[1]));
                            if (chars.length==3){
                                event.add((long) Byte.toUnsignedInt(chars[2]));
                            }
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
                            event.add((long) 0x80);
                            event.add((long) Byte.toUnsignedInt(chars[1]));
                            if (chars.length==3){
                                event.add((long) Byte.toUnsignedInt(chars[2]));
                            }
                            //plugin.getLogger().info("Note " + chars[1] + "off.");
                            break;
                        case 0xC0://Change musical instrument
                            event.add((long) 0xC0);
                            event.add((long) Byte.toUnsignedInt(chars[1]));
                            //plugin.getLogger().info("Set musical instrument to:"+chars[1]);
                            break;
                        default:
                            //plugin.getLogger().info(Arrays.toString(chars));
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
                    List<List<Long>> a = new ArrayList<>();
                    List<Long> b = new ArrayList<>();
                    b.add((long) trackCount);
                    a.add(b);
                    midiData.put("trackCount",a);
                    trackID++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //msPerTick = 1000;
        calcDelayMultiple();

        plugin.getLogger().info("delayMultiple:" + delayMultiple);
        //plugin.getLogger().info(String.valueOf(midiData));

        if (useDisplayFunction) {
            Location location = player.getLocation();
            float yaw = location.getYaw();

            if (yaw > -45 && yaw < 45) {
                x = location.getX() + (65);
                z = location.getZ() + (25);
                this.yaw = 0; // x-
            } else if (yaw > 90 - 45 && yaw < 90 + 45) {
                x = location.getX() + (-25);
                z = location.getZ() + (65);
                this.yaw = 1; // z-
            } else if (yaw > -90 - 45 && yaw < -90 + 45) {
                x = location.getX() + (25);
                z = location.getZ() + (-65);
                this.yaw = 3; // z+
            } else {
                x = location.getX() + (-65);
                z = location.getZ() + (-25);
                this.yaw = 2; // x+
            }
            y = location.getY();

            Location noteLocation = new Location(player.getWorld(), x, y + 11, z);

            for (int i = 21; i <= 108; ++i) {
                noteLocation.setY(y + 11);
                switch (this.yaw) {
                    case 0: // x-
                        noteLocation.setX(x - i);
                        noteLocation.setZ(z);
                        break;
                    case 1: // z-
                        noteLocation.setX(x);
                        noteLocation.setZ(z - i);
                        break;
                    case 2: // x+
                        noteLocation.setX(x + i);
                        noteLocation.setZ(z);
                        break;
                    case 3: // z+
                        noteLocation.setX(x);
                        noteLocation.setZ(z + i);
                        break;
                }
                player.sendBlockChange(noteLocation, Material.WOOL, (byte) (i % 2 == 0 ? 15 : 0));
            }
        }
    }

    public void playMidi(Player targetPlayer, boolean useSoundStop, byte resourcePack, boolean useProgressBar, boolean useDisplayFunction){
        MidiPlayer.getInstance().getLogger().info("Play Midi for player:"+targetPlayer.getName());
        isRunning = true;
        BukkitRunnable midiPlayer = new BukkitRunnable() {
            @Override
            public void run() {
                long trackCount = midiData.get("trackCount").get(0).get(0);
                //MidiPlayer.getInstance().getLogger().info(String.valueOf(msPerTick)+String.valueOf(midiData));
//                if(trackCount<1){
//                    targetPlayer.sendMessage("Can't play the midi,because it doesn't have music data");
//                    this.cancel();
//                    return;
//                }

                List<BukkitRunnable> tracks = new ArrayList<>();

                BukkitRunnable updateTick = new BukkitRunnable() {
                    @Override
                    public void run() {
                        final long startTime = System.currentTimeMillis();
                        while (tick <= midiLength && isRunning){
                            if( (System.currentTimeMillis()-startTime) < (tick * msPerTick / 1000) ){
                                tick--;
                                try {
                                    Thread.sleep(2);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                tick++;
                            }
                        }
                        targetPlayer.sendMessage("End of a song");
                        isRunning = false;
                        if(useDisplayFunction) {
                            Location noteLocation = new Location(targetPlayer.getWorld(), x, y + 11, z);

                            for (int i = 21; i <= 108; ++i) {
                                noteLocation.setY(y + 11);
                                switch (yaw) {
                                    case 0: // x-
                                        noteLocation.setX(x - i);
                                        noteLocation.setZ(z);
                                        break;
                                    case 1: // z-
                                        noteLocation.setX(x);
                                        noteLocation.setZ(z - i);
                                        break;
                                    case 2: // x+
                                        noteLocation.setX(x + i);
                                        noteLocation.setZ(z);
                                        break;
                                    case 3: // z+
                                        noteLocation.setX(x);
                                        noteLocation.setZ(z + i);
                                        break;
                                }
                                targetPlayer.sendBlockChange(noteLocation, Material.AIR, (byte) 0);
                            }
                            cancel();
                        }
                    }
                };
                tracks.add(updateTick);

//                BukkitRunnable updateParticles = new BukkitRunnable() {
//                    @Override
//                    public void run() {
//                        while (isRunning){
//                            for(int i=0;i<particles.size();++i){
//                                targetPlayer.spawnParticle(Particle.NOTE,particles.get(i),1);
//
//                                particles.get(i).setY(particles.get(i).getBlockY()-0.5);
//
//                                if(particles.get(i).getY()<=1){
//                                    particles.remove(i);
//                                    i--;
//                                }
//                            }
//                            try {
//                                Thread.sleep(50);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                };
//
//                tracks.add(updateParticles);

                for(int i = 0;i<=trackCount;++i){
                    int finalI = i;
                    BukkitRunnable runnable = new BukkitRunnable() {
                        final int trackId = finalI;
                        final List<List<Long>> data = midiData.get(String.valueOf(trackId));
                        double nowTick = 0;
                        long musicalInstrument = 0;
                        int tickMultiple = 1;
                        //final long startTime = System.currentTimeMillis();
                        @Override
                        public void run() {
                            int i = 0;
                            if(data == null)return;
                            while (i<data.size() && isRunning){
                                if(nowTick>tick){
                                    try {
                                        Thread.sleep( (long) ( ( (msPerTick / 1000) * delayMultiple ) ) );
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    continue;
                                }
                                List<Long> event = data.get(i);
                                if (event.get(0) == 0x90) { // note on
                                    //targetPlayer.sendMessage(String.valueOf(event.get(0))+" "+event.get(1)+ " " + event.get(2));
                                    if (musicalInstrument <= 7 || (musicalInstrument >= 16 && musicalInstrument <= 23)) {
                                        //MidiPlayer.getInstance().getLogger().info(String.valueOf(nowTick)+" "+String.valueOf(tick));
                                        Location noteLocation = targetPlayer.getLocation();
                                        if (event.size() == 3) {
                                            PlayNote.playNote(targetPlayer, event.get(1), event.get(2), resourcePack);
                                            //targetPlayer.sendMessage("[Debug] Note: " + event.get(1) + " Strength: " + PlayNote.getStrength(event.get(2)) + " " + event.get(2));
                                            if(useDisplayFunction) {

                                                noteLocation.setY(y + 10);
                                                switch (yaw) {
                                                    case 0: // x-
                                                        noteLocation.setX(x - event.get(1));
                                                        noteLocation.setZ(z + PlayNote.getStrength_num(event.get(2)));
                                                        break;
                                                    case 1: // z-
                                                        noteLocation.setX(x - PlayNote.getStrength_num(event.get(2)));
                                                        noteLocation.setZ(z - event.get(1));
                                                        break;
                                                    case 2: // x+
                                                        noteLocation.setX(x + event.get(1));
                                                        noteLocation.setZ(z - PlayNote.getStrength_num(event.get(2)));
                                                        break;
                                                    case 3: // z+
                                                        noteLocation.setX(x + PlayNote.getStrength_num(event.get(2)));
                                                        noteLocation.setZ(z + event.get(1));
                                                        break;
                                                }
//                                                particles.add(noteLocation);
                                            }
                                        } else {
                                            PlayNote.playNote(targetPlayer, event.get(1), 127, resourcePack);

                                            if (useDisplayFunction) {

                                                noteLocation.setY(y + 10);
                                                switch (yaw) {
                                                    case 0: // x-
                                                        noteLocation.setX(x - event.get(1));
                                                        noteLocation.setZ(z);
                                                        break;
                                                    case 1: // z-
                                                        noteLocation.setX(x);
                                                        noteLocation.setZ(z - event.get(1));
                                                        break;
                                                    case 2: // x+
                                                        noteLocation.setX(x + event.get(1));
                                                        noteLocation.setZ(z);
                                                        break;
                                                    case 3: // z+
                                                        noteLocation.setX(x);
                                                        noteLocation.setZ(z + event.get(1));
                                                        break;
                                                }
                                            }
                                        }

                                        if(useDisplayFunction) {
                                            NoteParticle noteParticle = new NoteParticle(noteLocation);
                                            noteParticle.start(MidiPlayer.getInstance(), 50, 1);
                                            PlayingNoteParticles.playingNoteParticles.add(noteParticle);
                                        }

                                    } else {
                                        targetPlayer.sendMessage("Invalid musical instrument:" + musicalInstrument);
                                    }
                                } else if (event.get(0) == 0x80) { // note off
                                    if (resourcePack != PlayNote.vanilla && useSoundStop) {
                                        if (musicalInstrument <= 7) {
                                            if (event.size() == 3) {
                                                PlayNote.stopNote(targetPlayer, event.get(1), event.get(2), resourcePack);
                                            } else {
                                                PlayNote.stopNote(targetPlayer, event.get(1), 127, resourcePack);
                                            }
                                        }
                                    }
                                } else if (event.get(0) == 0xC0) { // 更改乐器
                                    musicalInstrument = event.get(1);
                                } else if (event.get(0) == 0xDD) { // 延时
                                    nowTick += event.get(1) * tickMultiple;
                                    midiLength += (event.get(1) * tickMultiple) - event.get(1);
                                    //targetPlayer.sendMessage("Now time: "+(System.currentTimeMillis()-startTime)+" Time should be: " + (tick * msPerTick / 1000) + " aaa:" + ((System.currentTimeMillis()-startTime) - (tick * msPerTick / 1000)));
                                } else if (event.get(0) == 0x05) { //歌词
                                    StringBuilder output = new StringBuilder();
                                    output.append("Lyrics:");
                                    for (int k = 1; k < event.size(); ++k) {
                                        output.append(String.valueOf(getChars(new byte[]{event.get(k).byteValue()})));
                                    }
                                    targetPlayer.sendMessage(output.toString());
                                } else if (event.get(0) == 0x51){ // 更改速度
                                    //msPerTick = event.get(1);
                                    tickMultiple = (int) (msPerTick/event.get(1));
                                    MidiPlayer.getInstance().getLogger().info("Change Track: "+trackId+" msPerTick to:"+msPerTick);
                                }

                                float percent = ((float) tick / (float) midiLength)*100; // 计算播放到的百分比
                                StringBuilder progressBar = new StringBuilder();
                                for (int j = 0;j<(int)((percent / 100) *50);++j){
                                    progressBar.append("="); // 已播放部分
                                }
                                for (int j = 0;j<50-(int)((percent / 100) *50);++j){
                                    progressBar.append("-"); // 未播放部分
                                }
                                //targetPlayer.sendMessage(String.valueOf(event.get(0))+event.get(1));
                                if(useProgressBar){
                                    targetPlayer.sendTitle( "§r", "["+progressBar+"] "+((int) (percent*100) * 1.0 /100) + "%", 0, 100, 0);
                                }
                                i++;
                            }
                            if(useProgressBar){
                                targetPlayer.sendTitle( "§r", "§r",0,0,0);
                            }
                            cancel();
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

    private void calcDelayMultiple(){
        for(int i = 1;i<=100;++i){
            if ((msPerTick/1000)*delayMultiple<1.0){
                //MidiPlayer.getInstance().getLogger().info(String.valueOf(msPerTick/1000));
                delayMultiple+=1;
            } else {
                break;
            }
        }
        if(delayMultiple>=100){
            MidiPlayer.getInstance().getLogger().warning("The midi has a wrong delay.");
        }
    }

    private double getMsPerTick(int msPerBeat,int ticksPerBeat){
        return (double) msPerBeat / (double) ticksPerBeat;
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

