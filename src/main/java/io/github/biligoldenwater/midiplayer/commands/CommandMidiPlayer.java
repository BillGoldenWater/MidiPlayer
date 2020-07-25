package io.github.biligoldenwater.midiplayer.commands;

import io.github.biligoldenwater.midiplayer.MidiPlayer;
import io.github.biligoldenwater.midiplayer.PlayMidi;
import io.github.biligoldenwater.midiplayer.api.PlayingMidis;
import io.github.biligoldenwater.midiplayer.modules.CheckPermissions;
import io.github.biligoldenwater.midiplayer.modules.GetMidis;
import io.github.biligoldenwater.midiplayer.modules.HashMapLoadAndSave;
import io.github.biligoldenwater.midiplayer.modules.PlayNote;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class CommandMidiPlayer {
    private static final int pluginInfo = 1;
    private static final int fullHelp = 0;
    private static final int subCommandPlay = 2;

    public static void registerCommandMidiPlayer(){
        JavaPlugin plugin = MidiPlayer.getInstance();
        plugin.getCommand("midiplayer").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
                switch (args.length) {
                    case 1:
                        switch (args[0]) {
                            case "?":
                            case "？":
                            case "help":
                                if (!CheckPermissions.hasPermissions(sender, "midiplayer.commands.help")) return true;
                                sendHelpMessages(sender, fullHelp); // 发送完整帮助信息
                                return true;
                            case "list":
                                if (!CheckPermissions.hasPermissions(sender, "midiplayer.commands.list")) return true;
                                List<File> midis = GetMidis.getMidis(MidiPlayer.getMusicsPathName()); // 获取所有Midi文件

                                sender.sendMessage("There is all midis:");

                                for (int i = 0; i < Math.min(midis.size(), 10); ++i) { // 如果没有超过10个则遍历所有文件 如果超过则之遍历前十
                                    String fileName = midis.get(i).getName(); // 获取文件名
                                    fileName = fileName.replaceAll(".mid", ""); // 删除后缀
                                    fileName = fileName.replace(".midi", "");
                                    sender.sendMessage("Index: " + i + " Name: " + fileName);
                                }
                                return true;
                            case "play":
                                if (!CheckPermissions.hasPermissions(sender, "midiplayer.commands.play")) return true;
                                if (!(sender instanceof Player)) { // 判断是否玩家
                                    sender.sendMessage("This command must execute by player.");
                                    return true;
                                }
                                sendHelpMessages(sender, subCommandPlay); // 发送该命令的帮助信息
                                return true;
                            case "stop":
                                if (!CheckPermissions.hasPermissions(sender, "midiplayer.commands.stop")) return true;
                                if (!(sender instanceof Player)) { // 判断是否玩家
                                    sender.sendMessage("This command must execute by player.");
                                    return true;
                                }
                                PlayMidi playMidi = PlayingMidis.playingMidis.get(sender.getName()); // 获取玩家的播放中midi
                                if (playMidi != null) { // 如果播放中的midi不为空则停止播放
                                    MidiPlayer.getInstance().getLogger().info("Force stop midi player for player:" + sender.getName() + ".");
                                    playMidi.stopSound();
                                    sender.sendMessage("Stopped.");
                                }
                                return true;
                            case "toggleprogressbar":
                                if (!CheckPermissions.hasPermissions(sender, "midiplayer.commands.toggleprogressbar"))
                                    return true;
                                FileConfiguration config = plugin.getConfig(); // 获取配置文件
                                HashMapLoadAndSave hashMapLoadAndSave = new HashMapLoadAndSave(); // 新建对象

                                HashMap<Object, Object> configMap = hashMapLoadAndSave.loadHashMap(config, "progressbar"); // 获取数据
                                Object isEnable = configMap.get(sender.getName()); // 获取当前切换进度条显示的玩家的数据

                                if (isEnable == null || isEnable.equals(true)) { // 如果为空或为true则切换至false
                                    sender.sendMessage("Try to toggle to off.");
                                    isEnable = false;
                                } else if (isEnable.equals(false)) { // 如果为false则切换至true
                                    sender.sendMessage("Try to toggle to on.");
                                    isEnable = true;
                                }

                                configMap.put(sender.getName(), isEnable); // 将更改后的放入Map
                                hashMapLoadAndSave.saveHashMap(config, plugin, "progressbar", configMap); // 存储到配置文件
                                plugin.saveConfig(); // 保存配置文件
                                sender.sendMessage("Toggled.");
                                return true;
                            case "resourcepacks":
                                if (!CheckPermissions.hasPermissions(sender, "midiplayer.commands.resourcepacks")) return true;
                                sender.getServer().dispatchCommand(sender.getServer().getConsoleSender(),"tellraw "+sender.getName()+" {\"text\":\"test\"}");
                                return true;
                            case "reload":
                                if (!CheckPermissions.hasPermissions(sender, "midiplayer.commands.reload")) return true;
                                plugin.reloadConfig(); // 重载配置
                                sender.sendMessage("Config reloaded");
                                return true;
                            default:
                                sendHelpMessages(sender, pluginInfo); // 发送插件信息
                                return true;
                        }
                    case 2:
                    case 3:
                    case 4:
                        if(args[0].equals("play")){
                            if (!CheckPermissions.hasPermissions(sender, "midiplayer.commands.play")) return true;
                            if (!(sender instanceof Player)) { // 判断是否玩家
                                sender.sendMessage("This command must execute by player.");
                                return true;
                            }

                            int index = 0;
                            try {
                                index = Integer.parseInt(args[1]);
                            } catch (Exception e){
                                sender.sendMessage("Invalid midi index,please use /midiplayer list to view midi indexes.");
                            }
                            if(args.length>2) {
                                boolean isByte = true;
                                byte resourcePackNum = -1;

                                try { // 尝试转换至byte
                                    resourcePackNum = Byte.parseByte(args[2]);
                                } catch (Exception e) {
                                    isByte = false; // 如果无法转换则将 isByte 设为false
                                }

                                if (isByte) {
                                    switch (resourcePackNum) {
                                        case PlayNote.vanilla: // 判断是否是资源包中的某个
                                        case PlayNote.MSGSPiano:
                                        case PlayNote.realPiano:
                                            if(args.length>3){
                                                try { // 尝试转换useSoundStop至byte
                                                    byte useSoundStop = Byte.parseByte(args[3]);
                                                    playMidi(plugin, sender, Integer.parseInt(args[1]), useSoundStop==1, resourcePackNum);// 播放
                                                    sender.sendMessage("Played.");
                                                    return true;
                                                } catch (Exception e){
                                                    sender.sendMessage("Invalid sound stop status,please use 1 OR 0(use OR not use)");
                                                }
                                            } else {
                                                playMidi(plugin, sender, Integer.parseInt(args[1]), false, resourcePackNum); // 如果是则用此资源包播放
                                                sender.sendMessage("Played.");
                                            }
                                            return true;
                                        default:
                                            sender.sendMessage("Invalid resource pack index,please use /midiplayer resourcepacks to view resource pack indexes."); // 发送错误信息
                                            return true;
                                    }
                                } else {
                                    sender.sendMessage("Invalid resource pack index,please use /midiplayer resourcepacks to view resource pack indexes."); // 发送错误信息
                                    return true;
                                }
                            } else {
                                playMidi(plugin,sender, index , false, PlayNote.vanilla);
                                sender.sendMessage("Played.");
                                return true;
                            }
                        }
                        return true;
                    default:
                        sendHelpMessages(sender, pluginInfo); // 发送插件信息
                        return true;
                }
            }
        });
    }

    private static void sendHelpMessages(CommandSender sender, int helpLevel){
        switch (helpLevel){
            case pluginInfo:
                sender.sendMessage("MidiPlayer by.Golden_Water");
                sender.sendMessage("For show help message please use:");
                sender.sendMessage("/midiplayer help or /mp help");
                break;
            case subCommandPlay:
                sender.sendMessage("Usage: /midiplayer play <index> [resourcePackIndex] [useSoundStop]");
                sender.sendMessage("      Play midi use index.You can use command:/midiplayer list");
                sender.sendMessage("      to get midi index.And the default");
                sender.sendMessage("      resource pack is vanilla(index 0) resources.");
                sender.sendMessage("      You can use command:/midiplayer resourcepacks");
                sender.sendMessage("      to get resource pack index.");
                sender.sendMessage("      useSoundStop:Use or not use(1 or 0)");
                sender.sendMessage("      sound stop on end of a note. (default is 0)");
                break;
            case fullHelp:
            default:
                sender.sendMessage("MidiPlayer by.Golden_Water");
                sender.sendMessage("Usage: /midiplayer help (Show this message.)");
                sender.sendMessage("Usage: /midiplayer list [page] (List all midis with index and name.)");
                sender.sendMessage("Usage: /midiplayer play <index> [resourcePackIndex] [useSoundStop] (Use this command to get more details.)");
                sender.sendMessage("Usage: /midiplayer stop (Stop play midi.)");
                sender.sendMessage("Usage: /midiplayer toggleprogressbar (Toggle progress bar display(Need replay).)");
                sender.sendMessage("Usage: /midiplayer resourcepacks (Show all available resource packs)");
                sender.sendMessage("Usage: /midiplayer reload (Reload config.)");
                break;
        }
    }

    private static void playMidi(JavaPlugin plugin, CommandSender sender, int index, boolean useSoundStop, byte resourcePack){
        PlayMidi playingMidi = PlayingMidis.playingMidis.get(sender.getName()); // 获取玩家的播放中midi

        if(playingMidi != null){ // 如果播放中的midi不为空则停止播放
            MidiPlayer.getInstance().getLogger().info("Force stop midi player for player:"+sender.getName()+".");
            sender.sendMessage("Because you have a midi are playing,so stop it.");
            playingMidi.stopSound();
            sender.sendMessage("Stopped.");
        }

        List<File> midis = GetMidis.getMidis(MidiPlayer.getMusicsPathName()); // 获取所有Midi文件

        if(midis.size()-1 >= index){ // 如果索引不大于总midi数量则
            FileConfiguration config = plugin.getConfig();
            HashMapLoadAndSave hashMapLoadAndSave = new HashMapLoadAndSave();
            playingMidi = new PlayMidi(); // 新建播放器
            boolean useProgressBar = true;

            try {// 尝试转换至boolean
                useProgressBar = (boolean) hashMapLoadAndSave.loadHashMap(config,"progressbar").get(sender.getName());
            } catch (Exception e){
                sender.sendMessage("§cError to read config file.");
                sender.sendMessage("§cPlease report to admin of the server admin.");
            }

            playingMidi.initMidi(plugin, (Player) sender, midis.get(index)); // 初始化midi播放器
            playingMidi.playMidi((Player) sender,useSoundStop,resourcePack, useProgressBar); // 播放

            PlayingMidis.playingMidis.put(sender.getName(),playingMidi); // 将播放器放入正在播放map
        } else {
            sender.sendMessage("Invalid midi index,please use /midiplayer list to view indexes."); // 发送播放失败消息
        }
    }
}
