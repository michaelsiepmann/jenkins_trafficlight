package de.misi.idea.plugins.jenkins.parser

import de.misi.idea.plugins.jenkins.parser.data.Job
import de.misi.idea.plugins.jenkins.parser.data.impl.JobImpl
import org.junit.Assert.assertEquals
import org.junit.Test

class JobHelperTest {

    @Test
    fun testFilterByState() {
        assertEquals("testBlue, testBlueAborted", JOBS.filterByState(JobState.blue).convertToName())
        assertEquals("testYellow", JOBS.filterByState(JobState.yellow).convertToName())
        assertEquals("testRed", JOBS.filterByState(JobState.red).convertToName())
    }

    @Test
    fun testFindWorstState() {
        assertEquals("red", JOBS.findWorstState { _, _ -> }.name)
        assertEquals("yellow", JOBS.removeState(JobState.red).findWorstState { _, _ -> }.name)
        assertEquals("blue", JOBS.removeState(JobState.red).removeState(JobState.yellow).findWorstState { _, _ -> }.name)
    }

    private fun List<Job>.convertToName() = map { it.name }.joinToString()

    private fun List<Job>.removeState(state: JobState) = filter { it.state != state }

    companion object {
        private val JOBS = arrayListOf(
                JobImpl("testBlue", "blue", ""),
                JobImpl("testBlueAborted", "blue_aborted", ""),
                JobImpl("testYellow", "yellow", ""),
                JobImpl("testRed", "red", "")
        )
    }
}