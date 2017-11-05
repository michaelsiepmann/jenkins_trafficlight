package de.misi.idea.plugins.jenkins.parser.data

import de.misi.idea.plugins.jenkins.parser.data.Job
import de.misi.idea.plugins.jenkins.parser.data.JobsHolder

internal data class Dashboard(val name: String, val url: String, val description: String, override val jobs: List<Job>) : JobsHolder
