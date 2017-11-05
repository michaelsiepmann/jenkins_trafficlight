package de.misi.idea.plugins.jenkins.widget

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogBuilder
import com.intellij.openapi.wm.CustomStatusBarWidget
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import de.misi.idea.plugins.jenkins.ChangeListener
import de.misi.idea.plugins.jenkins.dialog.states.JenkinsStates
import de.misi.idea.plugins.jenkins.parser.JenkinsStateHandler
import de.misi.idea.plugins.jenkins.parser.JenkinsStateHandlerImpl
import de.misi.idea.plugins.jenkins.parser.JobState
import de.misi.idea.plugins.jenkins.parser.createJenkinsStateHandler
import de.misi.idea.plugins.jenkins.resources.getString
import de.misi.idea.plugins.jenkins.settings.WorkspaceSettings
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.withTimeout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.concurrent.TimeUnit

internal class CurrentStateWidget(private val project: Project, private val notifier: Notifier = NotifierImpl()) : CustomStatusBarWidget, ChangeListener {

    private lateinit var timer: JenkinsTimer

    private val statePanel = StatePanel()
    private var jobs: JenkinsStateHandler = JenkinsStateHandlerImpl()
    private var lastState = JobState.unknown
    private var lastNotificationMessage = ""

    fun initialize(settings: WorkspaceSettings) {
        timer = JenkinsTimer(settings.waitingSeconds, settings.enableJenkinsCheck) {
            val deferred = withTimeout(1L, TimeUnit.MINUTES) {
                async {
                    createJenkinsStateHandler(WorkspaceSettings.getInstance(project).urlSettings)
                }
            }
            jobs = deferred.await()
            ApplicationManager.getApplication().invokeLater {
                updateState(jobs, settings)
            }
        }
        timer.start()
        statePanel.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                val jenkinsStates = JenkinsStates(jobs, settings)
                val builder = DialogBuilder()
                builder.setTitle(getString("statelist.title"))
                builder.removeAllActions()
                builder.addCloseButton()
                builder.setCenterPanel(jenkinsStates.rootPanel)
                builder.show()
            }
        })
    }

    @Suppress("FunctionName")
    override fun ID() = "JenkinsState"

    override fun getPresentation(p0: StatusBarWidget.PlatformType) = null

    override fun getComponent() = statePanel

    override fun install(statusBar: StatusBar) {
    }

    override fun dispose() {
    }

    override fun onChange(settings: WorkspaceSettings) {
        timer.enabled = settings.enableJenkinsCheck
        if (!settings.enableJenkinsCheck) {
            statePanel.toolTipText = "disabled"
        }
    }

    private fun updateState(handler: JenkinsStateHandler, settings: WorkspaceSettings) {
        val state = handler.findWorstState()
        statePanel.icon = handler.getWorstStateIcon(settings)
        statePanel.toolTipText = handler.getTooltip(settings)
        val notificationMessage = handler.getNotificationMessage()
        if (lastState != state || lastNotificationMessage != notificationMessage) {
            lastState = state
            lastNotificationMessage = notificationMessage
            notifier.notify(state, notificationMessage, settings, handler)
        }
    }

    fun stopTimer() {
        timer.stop()
    }

    companion object {
        const val NOTIFICATION_ID = "Jenkins State"
    }
}