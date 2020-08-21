package io.github.biligoldenwater.midiplayer.modules;

import net.minecraft.server.v1_12_R1.*;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class SendActionBar {
    public static void sendActionBar(Player player, String text){
        ChatComponentText actionBar = new ChatComponentText(text);
        PacketPlayOutChat pack = new PacketPlayOutChat(actionBar, ChatMessageType.GAME_INFO);
        CraftPlayer craftPlayer = (CraftPlayer) player;
        EntityPlayer entityPlayer = craftPlayer.getHandle();
        PlayerConnection playerConnection = entityPlayer.playerConnection;

        playerConnection.sendPacket(pack);
    }
}
