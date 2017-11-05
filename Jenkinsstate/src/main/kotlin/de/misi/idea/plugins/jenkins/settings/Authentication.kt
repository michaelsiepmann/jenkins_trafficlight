package de.misi.idea.plugins.jenkins.settings

import com.intellij.util.xmlb.annotations.Attribute

internal data class Authentication(
        @Attribute("required")
        var required: Boolean = false,
        var username: String? = null,
        @Attribute("storeAPIToken")
        var storeAPIToken: Boolean = false
)
