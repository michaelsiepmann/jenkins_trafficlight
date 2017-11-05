package de.misi.idea.plugins.jenkins

import de.misi.idea.plugins.jenkins.settings.WorkspaceSettings

internal interface ChangeListener {
    fun onChange(settings: WorkspaceSettings)
}