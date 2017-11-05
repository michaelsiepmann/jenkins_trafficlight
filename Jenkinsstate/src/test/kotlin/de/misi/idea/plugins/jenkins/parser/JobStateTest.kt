package de.misi.idea.plugins.jenkins.parser

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class JobStateTest {

    @Test
    fun isWorseWithBlue() {
        assertFalse(JobState.blue.isWorser(JobState.blue))
        assertTrue(JobState.blue.isWorser(JobState.yellow))
        assertTrue(JobState.blue.isWorser(JobState.red))
        assertTrue(JobState.blue.isWorser(JobState.unknown))
    }

    @Test
    fun isWorseWithYellow() {
        assertFalse(JobState.yellow.isWorser(JobState.blue))
        assertFalse(JobState.yellow.isWorser(JobState.yellow))
        assertTrue(JobState.yellow.isWorser(JobState.red))
        assertFalse(JobState.yellow.isWorser(JobState.unknown))
    }

    @Test
    fun isWorseWithRed() {
        assertFalse(JobState.red.isWorser(JobState.blue))
        assertFalse(JobState.red.isWorser(JobState.yellow))
        assertFalse(JobState.red.isWorser(JobState.red))
        assertFalse(JobState.red.isWorser(JobState.unknown))
    }
}