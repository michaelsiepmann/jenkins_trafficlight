package de.misi.idea.plugins.jenkins

import javazoom.jl.player.Player
import java.io.File

internal fun playMediaFile(mediaFile: String?) {
    if (mediaFile?.isNotEmpty() == true) {
        File(mediaFile).apply {
            if (exists()) {
                Thread {
                    Player(inputStream()).play()
                }.start()
            }
        }
    }
}
