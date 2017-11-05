package de.misi.idea.plugins.jenkins.parser.data.views

import com.google.gson.annotations.SerializedName

internal data class View(
        var clazz: String = "",
        val name: String = "",
        var url: String = ""

) {
    override fun toString() = name
}