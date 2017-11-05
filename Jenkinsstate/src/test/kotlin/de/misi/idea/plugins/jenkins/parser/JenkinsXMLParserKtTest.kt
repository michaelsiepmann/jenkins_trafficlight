package de.misi.idea.plugins.jenkins.parser

import de.misi.idea.plugins.jenkins.parser.data.Job
import de.misi.idea.plugins.jenkins.parser.data.views.View
import org.intellij.lang.annotations.Language
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.ByteArrayInputStream

class JenkinsXMLParserKtTest {

    @Test
    fun testParseJobs() {
        val dashboard = parseJobs(ByteArrayInputStream(XML_DASHBOARD.toByteArray()))
        dashboard.jobs.apply {
            assertEquals(2, size)
            assertEquals("Fail,red,http://localhost:8080/job/Fail/", get(0).toSimpleText())
            assertEquals("Pipeline,blue,http://localhost:8080/job/Pipeline/", get(1).toSimpleText())
        }
    }

    @Test
    fun testParseHudson() {
        val hudson = parseHudson(ByteArrayInputStream(XML_HUDSON.toByteArray()))
        hudson.views.apply {
            assertEquals(2, size)
            assertEquals("Ampel,http://localhost:8080/view/Ampel/", get(0).toSimpleText())
            assertEquals("all,http://localhost:8080/", get(1).toSimpleText())
        }
        hudson.jobs.apply {
            assertEquals(3, size)
            assertEquals("Fail,red,http://localhost:8080/job/Fail/", get(0).toSimpleText())
            assertEquals("Pipeline,blue,http://localhost:8080/job/Pipeline/", get(1).toSimpleText())
            assertEquals("Test,blue,http://localhost:8080/job/Test/", get(2).toSimpleText())
        }
    }

    private fun Job.toSimpleText() = "$name,${state.name},$url"

    private fun View.toSimpleText() = "$name,$url"

    companion object {
        @Language("xml")
        const val XML_DASHBOARD = """<dashboard _class="hudson.plugins.view.dashboard.Dashboard">
    <job _class="hudson.model.FreeStyleProject">
        <name>Fail</name>
        <url>http://localhost:8080/job/Fail/</url>
        <color>red</color>
    </job>
    <job _class="org.jenkinsci.plugins.workflow.job.WorkflowJob">
        <name>Pipeline</name>
        <url>http://localhost:8080/job/Pipeline/</url>
        <color>blue</color>
    </job>
    <name>Ampel</name>
    <url>http://localhost:8080/view/Ampel/</url>
</dashboard>"""

        @Language("xml")
        const val XML_HUDSON = """<hudson _class='hudson.model.Hudson'>
    <assignedLabel></assignedLabel>
    <mode>NORMAL</mode>
    <nodeDescription>Jenkins Master-Knoten</nodeDescription>
    <nodeName></nodeName>
    <numExecutors>2</numExecutors>
    <job _class='hudson.model.FreeStyleProject'>
        <name>Fail</name>
        <url>http://localhost:8080/job/Fail/</url>
        <color>red</color>
    </job>
    <job _class='org.jenkinsci.plugins.workflow.job.WorkflowJob'>
        <name>Pipeline</name>
        <url>http://localhost:8080/job/Pipeline/</url>
        <color>blue</color>
    </job>
    <job _class='hudson.model.FreeStyleProject'>
        <name>Test</name>
        <url>http://localhost:8080/job/Test/</url>
        <color>blue</color>
    </job>
    <overallLoad></overallLoad>
    <primaryView _class='hudson.model.AllView'>
        <name>all</name>
        <url>http://localhost:8080/</url>
    </primaryView>
    <quietingDown>false</quietingDown>
    <slaveAgentPort>-1</slaveAgentPort>
    <unlabeledLoad _class='jenkins.model.UnlabeledLoadStatistics'></unlabeledLoad>
    <useCrumbs>true</useCrumbs>
    <useSecurity>true</useSecurity>
    <view _class='hudson.plugins.view.dashboard.Dashboard'>
        <name>Ampel</name>
        <url>http://localhost:8080/view/Ampel/</url>
    </view>
    <view _class='hudson.model.AllView'>
        <name>all</name>
        <url>http://localhost:8080/</url>
    </view>
</hudson>"""
    }
}