package de.misi.idea.plugins.jenkins.parser.data

import de.misi.idea.plugins.jenkins.parser.JobState

internal interface Job {
    val name: String
    val url: String
    val state: JobState
    val healthReport: HealthReport
}