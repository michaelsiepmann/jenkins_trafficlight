package de.misi.idea.plugins.jenkins.parser

import de.misi.idea.plugins.jenkins.parser.JobState.blue
import de.misi.idea.plugins.jenkins.parser.JobState.red
import de.misi.idea.plugins.jenkins.parser.JobState.yellow
import de.misi.idea.plugins.jenkins.parser.data.Job
import de.misi.idea.plugins.jenkins.resources.getString
import de.misi.idea.plugins.jenkins.settings.WorkspaceSettings

internal data class JenkinsStateHandlerImpl(private val currentJobs: List<Job> = emptyList()) : JenkinsStateHandler {

    override val jobs: List<Job>
        get() = currentJobs

    override fun getText(settings: WorkspaceSettings): String {
        val worstState = findWorstState()
        return when (worstState) {
            blue -> if (settings.blueIsGreen) {
                getString("jenkins.state.green")
            } else {
                getString("jenkins.state.blue")
            }
            red -> getString("jenkins.state.red")
            yellow -> getString("jenkins.state.yellow")
            else -> worstState.name
        }
    }

    override fun getTooltip(settings: WorkspaceSettings) = findWorstState().getStateName(settings)

    override fun findWorstState() = jobs.findWorstState { _, _ -> }

    override fun getWorstStateIcon(settings: WorkspaceSettings) = findWorstState().getStateIcon(settings)

    override fun getNotificationMessage() = jobs
            .filterByState(findWorstState())
            .joinToString(separator = "<br/>", prefix = "Failed Jobs : <br/>", limit = 100, truncated = "...") {
                it.name
            }
}

