package de.misi.idea.plugins.jenkins.parser

import de.misi.idea.plugins.jenkins.parser.data.Job
import de.misi.idea.plugins.jenkins.settings.WorkspaceSettings
import javax.swing.ImageIcon

internal interface JenkinsStateHandler {
    val jobs: List<Job>
    fun getText(settings: WorkspaceSettings): String
    fun getTooltip(settings: WorkspaceSettings): String
    fun findWorstState(): JobState
    fun getWorstStateIcon(settings: WorkspaceSettings): ImageIcon
    fun getNotificationMessage(): String
}