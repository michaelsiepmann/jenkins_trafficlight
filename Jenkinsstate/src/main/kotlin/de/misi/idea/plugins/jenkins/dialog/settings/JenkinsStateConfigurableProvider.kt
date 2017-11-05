package de.misi.idea.plugins.jenkins.dialog.settings

import com.intellij.openapi.options.ConfigurableProvider
import com.intellij.openapi.project.Project

internal class JenkinsStateConfigurableProvider(private val project: Project) : ConfigurableProvider() {

    override fun createConfigurable() = JenkinsStateConfigurable(project)
}