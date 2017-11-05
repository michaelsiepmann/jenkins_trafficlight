package de.misi.idea.plugins.jenkins.dialog.states

import com.intellij.ide.browsers.BrowserLauncher
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBScrollPane
import de.misi.idea.plugins.jenkins.dialog.ArrayListModel
import de.misi.idea.plugins.jenkins.parser.JenkinsStateHandler
import de.misi.idea.plugins.jenkins.parser.data.Job
import de.misi.idea.plugins.jenkins.settings.WorkspaceSettings
import org.apache.commons.lang3.StringUtils.isNotEmpty
import org.apache.commons.logging.LogFactory
import java.awt.Component
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.net.URI
import java.net.URISyntaxException
import javax.swing.JLabel

internal class JenkinsStates(jenkinsStateHandler: JenkinsStateHandler, private val settings: WorkspaceSettings) {

    private val stateList = JBList<Job>()
    private val listLabel = JLabel()

    val rootPanel get() = JBScrollPane(stateList)

    init {
        stateList.model = ArrayListModel(jenkinsStateHandler.jobs)
        stateList.setCellRenderer { _, value, _, isSelected, _ -> updateListLabel(value, isSelected) }
        stateList.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(event: MouseEvent?) {
                onMouseClicked(event!!)
            }
        })
    }

    private fun onMouseClicked(event: MouseEvent) {
        if (event.clickCount == 2) {
            val job = stateList.selectedValue
            if (job != null && isNotEmpty(job.url)) {
                try {
                    BrowserLauncher.instance.browse(URI(job.url))
                } catch (e: URISyntaxException) {
                    LOG.error("", e)
                }
            }
        }
    }

    private fun updateListLabel(job: Job, isSelected: Boolean): Component {
        listLabel.text = job.name
        listLabel.isOpaque = true
        listLabel.background = if (isSelected) JBColor.LIGHT_GRAY else JBColor.WHITE
        listLabel.icon = job.state.getStateIcon(settings)
        listLabel.toolTipText = "<html><b>${job.state.getStateName(settings)}</b><br>${job.healthReport.description}</html>"
        return listLabel
    }

    companion object {
        private val LOG = LogFactory.getLog(JenkinsStates::class.java)
    }
}