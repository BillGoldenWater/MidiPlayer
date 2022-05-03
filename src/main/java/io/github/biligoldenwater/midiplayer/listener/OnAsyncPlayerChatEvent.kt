package io.github.biligoldenwater.midiplayer.listener

import io.github.biligoldenwater.midiplayer.instance
import io.github.biligoldenwater.midiplayer.utils.MidiPlay
import io.github.biligoldenwater.midiplayer.utils.PlayNote
import io.github.biligoldenwater.midiplayer.utils.PlayNote.ResourcePack
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import java.io.File

class OnAsyncPlayerChatEvent : Listener {
    @EventHandler
    fun onAsyncPlayerChatEvent(event: AsyncPlayerChatEvent) {
        val playerName = event.player.name
        val message = event.message
        val playList = instance.playList
        val playing = playList[playerName]
        if (playing != null) {
            playing.stop()
            playList.remove(playerName)
            return
        }
        if (!message.startsWith("@play ")) return
        val midiFile = File(File(instance.dataFolder, "musics"), message.replace("@play ", ""))
        event.player.sendMessage(midiFile.toString())
        if (!midiFile.exists()) return

//        File midiFile = new File("D:\\Music\\Midis\\U.N.オーエンは彼女なのか？.mid");
//        File midiFile = new File("D:\\Music\\Midis\\When Christmas comes to town.mid");
//        File midiFile = new File("D:\\Music\\Midis\\勾指起誓双轨.mid");
//        File midiFile = new File("D:\\Music\\Midis\\Luv letter -Dj Okawari.mid");
//        File midiFile = new File("D:\\Music\\Midis\\千本樱.mid");
//        File midiFile = new File("D:\\Music\\Midis\\Hedwigs Theme.mid");
//        File midiFile = new File("D:\\Music\\Midis\\Rubia.mid");
//        File midiFile = new File("D:\\Music\\Midis\\D大调卡农.mid");
        val playNote = PlayNote(ResourcePack.RealPiano, event.player.location, 10, true)
        val midiPlay = MidiPlay(midiFile, playNote, event.player, false)
        midiPlay.runTaskAsynchronously(instance)
        playList[playerName] = midiPlay
    }
}