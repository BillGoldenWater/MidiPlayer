package io.github.biligoldenwater.midiplayer.modules;

import io.github.biligoldenwater.midiplayer.MidiPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;

public class CommandMidiPlayer {
    private static final int pluginInfo = 1;
    private static final int fullHelp = 0;

    public static void registerCommandMidiPlayer(){
        JavaPlugin plugin = MidiPlayer.getInstance();
        plugin.getCommand("midiplayer").setExecutor((sender, command, label, args) -> {
            switch (args.length){
                case 1:
                    switch (args[0]){
                        case "help":
                            if(!CheckPermissions.hasPermissions(sender,"midiplayer.commands.help"))return true;
                            sendHelpMessages(sender,fullHelp);
                            return true;
                        case "list":
                            if(!CheckPermissions.hasPermissions(sender,"midiplayer.commands.list"))return true;
                            List<File> midis = GetMidis.getMidis(MidiPlayer.getMusicsPathName());
                            sender.sendMessage("There is all midis:");
                            for (int i = 0;i<midis.size();++i) {
                                sender.sendMessage("Index:"+i+" Name:"+midis.get(i).getName());
                            }
                            return true;
                        case "play":
                            if(!CheckPermissions.hasPermissions(sender,"midiplayer.commands.play"))return true;
                            return true;
                        case "stop":
                            if(!CheckPermissions.hasPermissions(sender,"midiplayer.commands.stop"))return true;
                            return true;
                        case "resourcepacks":
                            if(!CheckPermissions.hasPermissions(sender,"midiplayer.commands.resourcepacks"))return true;
                            return true;
                        case "reload":
                            if(!CheckPermissions.hasPermissions(sender,"midiplayer.commands.reload"))return true;
                            plugin.reloadConfig();
                            sender.sendMessage("Config reloaded");
                            return true;
                        default:
                            sendHelpMessages(sender,pluginInfo);
                            return true;
                    }
                case 2:
                default:
                    sendHelpMessages(sender,pluginInfo);
                    return true;
            }
        });
    }

    private static void sendHelpMessages(CommandSender sender,int helpLevel){
        switch (helpLevel){
            case pluginInfo:
                sender.sendMessage("MidiPlayer by.Golden_Water");
                sender.sendMessage("For show help message please use:");
                sender.sendMessage("/midiplayer help or /mp help");
            case fullHelp:
            default:
                sender.sendMessage("MidiPlayer by.Golden_Water");
                sender.sendMessage("Usage:/midiplayer help (Show this message.)");
                sender.sendMessage("Usage:/midiplayer list (List all midis with index and name.)");
                sender.sendMessage("Usage:/midiplayer play <index> [resourcePackIndex] [useSoundStop]");
                sender.sendMessage("Play midi use index,and the default resource pack is vanilla(index 0) resources.");
                sender.sendMessage("useSoundStop:Use or not use(1 or 0) sound stop on end of a note. (default is 0)");
                sender.sendMessage("Usage:/midiplayer stop (Stop play midi.)");
                sender.sendMessage("Usage:/midiplayer resourcepacks (Show all available resource packs)");
                sender.sendMessage("Usage:/midiplayer reload (Reload config.)");
                break;
        }
    }
}
