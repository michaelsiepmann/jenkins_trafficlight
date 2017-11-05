package de.misi.idea.plugins.jenkins.parser

import de.misi.idea.plugins.jenkins.parser.JobState.error
import de.misi.idea.plugins.jenkins.parser.data.Job
import de.misi.idea.plugins.jenkins.resources.IconResources
import de.misi.idea.plugins.jenkins.settings.WorkspaceSettings

internal data class ErrorStateHandler(private val message: String) : JenkinsStateHandler {

    override val jobs: List<Job> = emptyList()

    override fun getText(settings: WorkspaceSettings) = "Error"

    override fun getTooltip(settings: WorkspaceSettings) = message

    override fun findWorstState() = error

    override fun getWorstStateIcon(settings: WorkspaceSettings) = IconResources.WARNING_ICON

    override fun getNotificationMessage() = message
}