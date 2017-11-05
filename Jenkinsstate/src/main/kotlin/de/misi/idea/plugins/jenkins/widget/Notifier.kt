package de.misi.idea.plugins.jenkins.widget

import de.misi.idea.plugins.jenkins.parser.JenkinsStateHandler
import de.misi.idea.plugins.jenkins.parser.JobState
import de.misi.idea.plugins.jenkins.settings.WorkspaceSettings

internal interface Notifier {
    fun notify(state: JobState, notificationMessage: String, settings: WorkspaceSettings, handler: JenkinsStateHandler)
}