package de.misi.idea.plugins.jenkins.parser

import de.misi.idea.plugins.jenkins.parser.data.Job

internal fun List<Job>.filterByState(state: JobState) = filter { it.state == state }

internal fun List<Job>.findWorstState(updater: (Double, String) -> Unit): JobState {
    var worst = JobState.blue
    forEachIndexed { index, job ->
        updater((index / size).toDouble(), job.name)
        if (worst.isWorser(job.state)) {
            worst = job.state
        }
    }
    return worst
}
