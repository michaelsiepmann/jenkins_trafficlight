package de.misi.idea.plugins.jenkins.parser.data.views

import de.misi.idea.plugins.jenkins.parser.data.JobsHolder
import de.misi.idea.plugins.jenkins.parser.data.Job

internal data class Hudson(val views: List<View>, override val jobs: List<Job>) : JobsHolder