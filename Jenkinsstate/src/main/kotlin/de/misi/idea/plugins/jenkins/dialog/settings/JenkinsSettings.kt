package de.misi.idea.plugins.jenkins.dialog.settings

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import de.misi.idea.plugins.jenkins.settings.WorkspaceSettings
import javax.swing.JComponent

internal class JenkinsSettings(val settings: WorkspaceSettings, project: Project) : DialogWrapper(project) {

    private val settingsPanel = JenkinsSettingsPanel()

    val mainPanel: JComponent
        get() = settingsPanel.rootPanel

    val modified: Boolean
        get() = settingsPanel.isModified

    override fun createCenterPanel() = mainPanel

    val currentSettings: WorkspaceSettings
        get()  = settingsPanel.createSettings()

    fun reset() {
        settingsPanel.reset(settings)
    }
}