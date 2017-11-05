package de.misi.idea.plugins.jenkins.dialog.settings

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import de.misi.idea.plugins.jenkins.resources.getString
import de.misi.idea.plugins.jenkins.settings.WorkspaceSettings

internal class JenkinsStateConfigurable(project: Project) : Configurable {

    private val settings = WorkspaceSettings.getInstance(project)
    private val changedSettings = settings.copy()
    private val jenkinsSettings = JenkinsSettings(changedSettings, project)

    override fun isModified() = jenkinsSettings.modified

    override fun getDisplayName() = getString("settings.label")

    override fun apply() {
        settings.loadState(jenkinsSettings.currentSettings)
    }

    override fun createComponent() = jenkinsSettings.mainPanel

    override fun reset() {
        changedSettings.loadState(settings)
        jenkinsSettings.reset()
    }

    override fun getHelpTopic() = getString("settings.helptext")
}