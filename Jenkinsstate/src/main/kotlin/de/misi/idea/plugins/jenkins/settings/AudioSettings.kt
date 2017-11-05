package de.misi.idea.plugins.jenkins.settings

import com.intellij.util.xmlb.annotations.Attribute
import com.intellij.util.xmlb.annotations.Tag

internal data class AudioSettings(
        @Attribute("enabled")
        var playAudioFile: Boolean = false,
        @Tag
        var audioFile: String = ""
)