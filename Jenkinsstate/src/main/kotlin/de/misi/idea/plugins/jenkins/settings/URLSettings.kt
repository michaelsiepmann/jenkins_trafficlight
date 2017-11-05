package de.misi.idea.plugins.jenkins.settings

import com.intellij.util.xmlb.annotations.Tag

internal data class URLSettings(
        @Tag
        var url: String = "",
        @Tag
        var authentication: Authentication = Authentication()
)