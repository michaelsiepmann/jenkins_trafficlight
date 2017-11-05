package de.misi.idea.plugins.jenkins.parser.data.impl

import de.misi.idea.plugins.jenkins.parser.JobState
import de.misi.idea.plugins.jenkins.parser.data.HealthReport
import de.misi.idea.plugins.jenkins.parser.data.Job
import de.misi.idea.plugins.jenkins.parser.findJobState

internal data class JobImpl(
        override val name: String,
        private val color: String,
        override val url: String,
        override val healthReport: HealthReport
) : Job {
    override val state: JobState
        get() = findJobState(color)
}