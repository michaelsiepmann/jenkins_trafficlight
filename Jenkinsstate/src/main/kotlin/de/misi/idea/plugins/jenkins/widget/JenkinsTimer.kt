package de.misi.idea.plugins.jenkins.widget

import com.intellij.openapi.application.ApplicationManager
import kotlinx.coroutines.experimental.launch

internal class JenkinsTimer(private val waitingSeconds: Int, active: Boolean, private val tick: suspend () -> Unit) {

    @Volatile
    var enabled = active
    @Volatile
    var stopped = false

    fun start() {
        ApplicationManager.getApplication().executeOnPooledThread {
            while (!stopped) {
                if (enabled) {
                    launch {
                        tick()
                    }
                }
                Thread.sleep(waitingSeconds * 1000L)
            }
        }
    }

    fun stop() {
        stopped = true
    }
}