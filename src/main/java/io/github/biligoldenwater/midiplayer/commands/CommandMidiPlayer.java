package io.github.biligoldenwater.midiplayer.commands;

import io.github.biligoldenwater.midiplayer.MidiPlayer;
import io.github.biligoldenwater.midiplayer.PlayMidi;
import io.github.biligoldenwater.midiplayer.api.PlayingMidis;
import io.github.biligoldenwater.midiplayer.modules.*;
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
                        FileConfiguration config;
                        HashMapLoadAndSave hashMapLoadAndSave;
                        switch (args[0]) {
                            case "?":
                            case "？":
                            case "help":
                                if (!CheckPermissions.hasPermissions(sender, "midiplayer.commands.help")) return true;
                                sendHelpMessages(sender, fullHelp); // 发送完整帮助信息
                                return true;
                            case "list":
                                if (!CheckPermissions.hasPermissions(sender, "midiplayer.commands.list")) return true;
                                List<File> midis = GetMidis.getMidis(MidiPlayer.getMusicsPath()); // 获取所有Midi文件

                                sendMidisListWithMultiplePage(sender,midis,1); // 发送列表
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
                                if (playMidi != null && playMidi.isRunning()) { // 如果播放中的midi不为空且正在播放则停止播放
                                    MidiPlayer.getInstance().getLogger().info("Force stop midi player for player:" + sender.getName() + ".");
                                    playMidi.stopSound();
                                    sender.sendMessage("Stopped.");
                                } else {
                                    sender.sendMessage("Can't stop,because it doesn't playing.");
                                }
                                return true;
                            case "toggleprogressbar":
                                if (!CheckPermissions.hasPermissions(sender, "midiplayer.commands.toggleprogressbar"))
                                    return true;
                                config = plugin.getConfig(); // 获取配置文件
                                hashMapLoadAndSave = new HashMapLoadAndSave(); // 新建对象

                                HashMap<Object, Object> configMap_progressbar = hashMapLoadAndSave.loadHashMap(config, "progressbar"); // 获取数据
                                Object isEnable_progressbar = configMap_progressbar.get(sender.getName()); // 获取当前切换进度条显示的玩家的数据

                                if (isEnable_progressbar == null || isEnable_progressbar.equals(true)) { // 如果为空或为true则切换至false
                                    sender.sendMessage("Try to toggle to off.");
                                    isEnable_progressbar = false;
                                } else if (isEnable_progressbar.equals(false)) { // 如果为false则切换至true
                                    sender.sendMessage("Try to toggle to on.");
                                    isEnable_progressbar = true;
                                }
                                configMap_progressbar.put(sender.getName(), isEnable_progressbar); // 将更改后的放入Map
                                hashMapLoadAndSave.saveHashMap(config, plugin, "progressbar", configMap_progressbar); // 存储到配置文件
                                plugin.saveConfig(); // 保存配置文件
                                sender.sendMessage("Toggled.");
                                return true;
                            case "toggledisplayfunction":
                                if (!CheckPermissions.hasPermissions(sender, "midiplayer.commands.toggledisplayfunction"))
                                    return true;
                                config = plugin.getConfig(); // 获取配置文件
                                hashMapLoadAndSave = new HashMapLoadAndSave(); // 新建对象

                                if(!config.getBoolean("allowPlayersUseDisplayFunction",false)){
                                    sender.sendMessage("This function doesn't enable at this server.");
                                    return true;
                                }

                                HashMap<Object, Object> configMap_displayFunction = hashMapLoadAndSave.loadHashMap(config, "displayFunction"); // 获取数据
                                Object isEnable_displayFunction = configMap_displayFunction.get(sender.getName()); // 获取当前切换进度条显示的玩家的数据

                                if (isEnable_displayFunction == null || isEnable_displayFunction.equals(false)) { // 如果为空或为false则切换至true
                                    sender.sendMessage("Try to toggle to on.");
                                    isEnable_displayFunction = true;
                                }else if (isEnable_displayFunction.equals(true)) { // 如果为true则切换至false
                                    sender.sendMessage("Try to toggle to off.");
                                    isEnable_displayFunction = false;
                                }
                                configMap_displayFunction.put(sender.getName(), isEnable_displayFunction); // 将更改后的放入Map
                                hashMapLoadAndSave.saveHashMap(config, plugin, "displayFunction", configMap_displayFunction); // 存储到配置文件
                                plugin.saveConfig(); // 保存配置文件
                                sender.sendMessage("Toggled.");
                                return true;
                            case "resourcepacks":
                                if (!CheckPermissions.hasPermissions(sender, "midiplayer.commands.resourcepacks")) return true;

                                sender.sendMessage("There is all support resourcepacks:");

                                JsonMessage clickToDownload = new JsonMessage("Click to download.");
                                JsonMessage downloadUnavailable = new JsonMessage("Doesn't have download link,click to search.");
                                clickToDownload.setBold("Click to download.",true);
                                clickToDownload.setColor("Click to download.",ColorNames.aqua);
                                downloadUnavailable.setBold("Doesn't have download link,click to search.",true);
                                downloadUnavailable.setColor("Doesn't have download link,click to search,",ColorNames.gray);

                                JsonMessage Vanilla = new JsonMessage("Index: 0 Vanilla");
                                JsonMessage MSGSPiano = new JsonMessage("Index: 1 MSGS Piano ");
                                JsonMessage RealPiano = new JsonMessage("Index: 2 Real Piano ");

                                MSGSPiano.addText("[Download]");
                                MSGSPiano.setBold("[Download]",true);
                                MSGSPiano.setColor("[Download]",ColorNames.aqua);
                                MSGSPiano.addHoverEvent("[Download]",JsonMessage.hoverEvent.action.show_text,clickToDownload.getJsonArray());
                                MSGSPiano.addClickEvent("[Download]",JsonMessage.clickEvent.action.open_url,"https://www.mcbbs.net/thread-733975-1-1.html");

                                RealPiano.addText("[Download]");
                                RealPiano.setBold("[Download]",true);
                                RealPiano.setColor("[Download]",ColorNames.gray);
                                RealPiano.addHoverEvent("[Download]",JsonMessage.hoverEvent.action.show_text,downloadUnavailable.getJsonArray());
                                RealPiano.addClickEvent("[Download]",JsonMessage.clickEvent.action.open_url,"https://www.bing.com/search?q=Minecraft+real+piano+resource+pack");

                                Vanilla.sendTo(sender);
                                MSGSPiano.sendTo(sender);
                                RealPiano.sendTo(sender);


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
                        if(args[0].equals("list")){
                            if (!CheckPermissions.hasPermissions(sender, "midiplayer.commands.list")) return true;
                            List<File> midis = GetMidis.getMidis(MidiPlayer.getMusicsPath()); // 获取所有Midi文件

                            int page;

                            try {
                                page = Integer.parseInt(args[1]);
                            } catch (Exception e){
                                sender.sendMessage("Invalid page.");
                                return true;
                            }

                            sendMidisListWithMultiplePage(sender,midis,page);

                            return true;
                        }
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

    private static void sendMidisListWithMultiplePage(CommandSender sender, List<File> midis,final int page){
        if(page <= 0){
            sender.sendMessage("Invalid page.");
            return;
        }
        if (midis.size() > 10 * ( page - 1 ) ) { // 如果超过十个则将开头改为这里是第x页
            sender.sendMessage("There is page "+page+":");

        } else if(midis.size() <= 10) {
            sender.sendMessage("There is all midis:");

            for (int i = 0; i < midis.size(); ++i) { // 遍历所有
                sendMidiIndexAndName(sender,i,midis);
            }

            return;
        } else {
            sender.sendMessage("Invalid page.");
            return;
        }

        for (int i = 10*(page-1); i < Math.min(midis.size(), 10*page); ++i) { // 如果没有超过10个则遍历当前页所有文件 如果超过则只遍历当前页
            sendMidiIndexAndName(sender,i,midis);
        }

        if (midis.size() > 10) { // 如果超过十个则在末尾发送翻页命令提示
            sender.sendMessage("Use /midiplayer list [page] to visit page "+( page+1 )+".");
            JsonMessage message = new JsonMessage("Previous page");

            message.addText(" | ");
            message.addText("Next page");

            if (page>1){
                message.setColor("Previous page", ColorNames.aqua);
                message.addHoverEvent("Previous page", JsonMessage.hoverEvent.action.show_text, "Click to turn page.");
                message.addClickEvent("Previous page", JsonMessage.clickEvent.action.run_command, "/mp list "+ (page-1) );
            } else {
                message.setColor("Previous page", ColorNames.gray);
                message.addHoverEvent("Previous page", JsonMessage.hoverEvent.action.show_text, "No previous page.");
                message.addClickEvent("Previous page", JsonMessage.clickEvent.action.change_page, 1 );
            }
            message.setColor(" | ",ColorNames.gray);
            message.addHoverEvent(" | ", JsonMessage.hoverEvent.action.show_text, "Split line.");
            message.addClickEvent(" | ", JsonMessage.clickEvent.action.run_command, "/mp list " + page );
            if (midis.size() > 10 * page){
                message.setColor("Next page", ColorNames.aqua);
                message.addHoverEvent("Next page", JsonMessage.hoverEvent.action.show_text, "Click to turn page.");
                message.addClickEvent("Next page", JsonMessage.clickEvent.action.run_command, "/mp list " + (page+1) );
            } else {
                message.setColor("Next page", ColorNames.gray);
                message.addHoverEvent("Next page", JsonMessage.hoverEvent.action.show_text, "No next page.");
                message.addClickEvent("Next page", JsonMessage.clickEvent.action.change_page, 1 );
            }

            message.sendTo(sender);

        }
    }

    private static void sendMidiIndexAndName(CommandSender sender, int i, List<File> midis){
        String fileName = midis.get(i).getName(); // 获取文件名
        fileName = fileName.replaceAll(".mid", ""); // 删除后缀
        fileName = fileName.replace(".midi", "");
        //fileName = SplitString.splitString("\\",fileName)[SplitString.splitString("\\",fileName).length-1];

        JsonMessage message = new JsonMessage("Index: " + i + " Name: " + fileName + " [");

        message.addText("Play");
        message.addText("] [");
        message.addText("Get");
        message.addText("]");

        message.setColor("Play", ColorNames.aqua);
        message.addHoverEvent("Play", JsonMessage.hoverEvent.action.show_text,"Click to play the midi with resource pack 2.");
        message.addClickEvent("Play", JsonMessage.clickEvent.action.run_command, "/mp play " + i + " 2");

        message.setColor("] [", ColorNames.reset);

        message.setColor("Get", ColorNames.aqua);
        message.addHoverEvent("Get", JsonMessage.hoverEvent.action.show_text,"Click to send command to your chat bar.");
        message.addClickEvent("Get", JsonMessage.clickEvent.action.suggest_command, "/mp play " + i);

        message.setColor("]",ColorNames.reset);

        message.sendTo(sender);
    }

    private static void sendHelpMessages(CommandSender sender, int helpLevel){
        switch (helpLevel){
            case pluginInfo:
                sender.sendMessage("");
                sender.sendMessage("MidiPlayer by.§eGolden§7_§bWater");
                sender.sendMessage("For show help message please use:");
                sender.sendMessage("/midiplayer help or /mp help");
                break;
            case subCommandPlay:
                sender.sendMessage("");
                sender.sendMessage("Usage: /midiplayer play <index> [resourcePackIndex] [useSoundStop]");
                sender.sendMessage("      Play midi use index.");
                sender.sendMessage("      index:");
                sender.sendMessage("        You can use command:/midiplayer list to get midi index.");
                sender.sendMessage("      resourcePackIndex:");
                sender.sendMessage("        You can use command:/midiplayer resourcepacks to get resource pack index.");
                sender.sendMessage("        Default resource pack is vanilla(index 0) resources.");
                sender.sendMessage("      useSoundStop:");
                sender.sendMessage("        Use or not use(1 or 0) sound stop on end of a note.(default is 0)");
                sender.sendMessage("        Sometime");
                break;
            case fullHelp:
            default:
                sender.sendMessage("");
                sender.sendMessage("MidiPlayer by.§eGolden§7_§bWater");
                sender.sendMessage("Usage: /midiplayer help (Show this message.)");
                sender.sendMessage("Usage: /midiplayer list [page] (List all midis with index and name.)");
                sender.sendMessage("Usage: /midiplayer play <index> [resourcePackIndex] [useSoundStop] (Use this command to get more details.)");
                sender.sendMessage("Usage: /midiplayer stop (Stop play midi.)");
                sender.sendMessage("Usage: /midiplayer toggleprogressbar (Toggle progress bar display(Need replay).)");
                sender.sendMessage("Usage: /midiplayer toggledisplayfunction (Toggle display function enable(Need replay).)");
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

        List<File> midis = GetMidis.getMidis(MidiPlayer.getMusicsPath()); // 获取所有Midi文件

        if(midis.size()-1 >= index){ // 如果索引不大于总midi数量则
            FileConfiguration config = plugin.getConfig();
            HashMapLoadAndSave hashMapLoadAndSave = new HashMapLoadAndSave();
            playingMidi = new PlayMidi(); // 新建播放器
            boolean useProgressBar = true;
            boolean useDisplayFunction = false;

            try {// 尝试转换至boolean
                useProgressBar = (boolean) hashMapLoadAndSave.loadHashMap(config,"progressbar").get(sender.getName());
            } catch (Exception ignored){
            }

            try {// 尝试转换至boolean
                useDisplayFunction = (boolean) hashMapLoadAndSave.loadHashMap(config,"displayFunction").get(sender.getName());
            } catch (Exception ignored){
            }

            if(!config.getBoolean("allowPlayersUseDisplayFunction",false)){
                useDisplayFunction = false;
            }

            playingMidi.initMidi(plugin, (Player) sender, midis.get(index), useDisplayFunction); // 初始化midi播放器
            playingMidi.playMidi((Player) sender,useSoundStop,resourcePack, useProgressBar, useDisplayFunction); // 播放

            PlayingMidis.playingMidis.put(sender.getName(),playingMidi); // 将播放器放入正在播放map
        } else {
            sender.sendMessage("Invalid midi index,please use /midiplayer list to view indexes."); // 发送播放失败消息
        }
    }
}
