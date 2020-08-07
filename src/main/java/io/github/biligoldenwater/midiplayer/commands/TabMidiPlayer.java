package io.github.biligoldenwater.midiplayer.commands;

import io.github.biligoldenwater.midiplayer.MidiPlayer;
import io.github.biligoldenwater.midiplayer.modules.CheckPermissions;
import io.github.biligoldenwater.midiplayer.modules.GetMidis;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class TabMidiPlayer {
    public static void registerTabMidiPlayer(){
        JavaPlugin plugin = MidiPlayer.getInstance();
        plugin.getServer().getPluginCommand("midiplayer").setTabCompleter(new TabCompleter() {
            @Override
            public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
                List<String> completions = new ArrayList<>();
                switch (args.length){
                    case 1:
                        if(CheckPermissions.hasPermissions(sender,"midiplayer.commands.help") && "help".startsWith(args[0]))completions.add("help");
                        if(CheckPermissions.hasPermissions(sender,"midiplayer.commands.list") && "list".startsWith(args[0]))completions.add("list");
                        if(CheckPermissions.hasPermissions(sender,"midiplayer.commands.play") && "play".startsWith(args[0]))completions.add("play");
                        if(CheckPermissions.hasPermissions(sender,"midiplayer.commands.stop") && "stop".startsWith(args[0]))completions.add("stop");
                        if(CheckPermissions.hasPermissions(sender,"midiplayer.commands.toggleprogressbar") && "toggleprogressbar".startsWith(args[0]))completions.add("toggleprogressbar");
                        if(CheckPermissions.hasPermissions(sender,"midiplayer.commands.toggledisplayfunction") && "toggledisplayfunction".startsWith(args[0]))completions.add("toggledisplayfunction");
                        if(CheckPermissions.hasPermissions(sender,"midiplayer.commands.resourcepacks") && "resourcepacks".startsWith(args[0]))completions.add("resourcepacks");
                        if(CheckPermissions.hasPermissions(sender,"midiplayer.commands.reload") && "reload".startsWith(args[0]))completions.add("reload");
                        return completions;
                    case 2:
                        if(args[0].equals("play")) {
                            for (int i = 0; i < GetMidis.getMidis(MidiPlayer.getMusicsPath()).size(); ++i) {
                                if(String.valueOf(i).startsWith(args[1])){
                                    completions.add(String.valueOf(i));
                                }
                            }
                        }
                        return completions;
                    case 3:
                        if(args[0].equals("play")){
                            for(int i = 0;i<=2;++i){
                                completions.add(String.valueOf(i));
                            }
                        }
                        return completions;
                    case 4:
                        if(args[0].equals("play")){
                            if("0".startsWith(args[3]))completions.add("0");
                            if("1".startsWith(args[3]))completions.add("1");
                        }
                        return completions;

                }
                return completions;
            }
        });
    }
}
